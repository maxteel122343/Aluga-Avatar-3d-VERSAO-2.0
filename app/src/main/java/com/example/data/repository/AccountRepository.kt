package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.auth.SessionManager
import com.example.data.model.AccountCredentials
import com.example.data.model.ActiveRental
import com.example.data.model.CredentialHistoryItem
import com.example.data.model.InAppNotification
import com.example.data.model.IncludedItem
import com.example.data.model.NotificationType
import com.example.data.model.RentalCalculation
import com.example.data.model.SharedAccount
import com.example.data.model.UserSession
import com.example.data.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class AccountRepository(private val context: Context) {

  private val tag = "AccountRepository"
  private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

  val supabase = SupabaseClient()
  val sessionManager = SessionManager(context)

  private val _accounts = MutableStateFlow<List<SharedAccount>>(emptyList())
  val accounts: StateFlow<List<SharedAccount>> = _accounts.asStateFlow()

  private val _activeRentals = MutableStateFlow<List<ActiveRental>>(emptyList())
  val activeRentals: StateFlow<List<ActiveRental>> = _activeRentals.asStateFlow()

  private val _inAppNotifications = MutableStateFlow<List<InAppNotification>>(emptyList())
  val inAppNotifications: StateFlow<List<InAppNotification>> = _inAppNotifications.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  val currentSession: StateFlow<UserSession?> = sessionManager.currentSession

  init {
    loadInitialSeed()
    refreshData()
  }

  fun refreshData() {
    scope.launch(Dispatchers.IO) {
      _isLoading.value = true
      try {
        // 1. Fetch accounts from Supabase
        val accResult = supabase.fetchAccounts()
        if (accResult.isSuccess) {
          val fetched = accResult.getOrNull()
          if (!fetched.isNullOrEmpty()) {
            _accounts.value = fetched
          }
        }

        // 2. Fetch rentals from Supabase
        val userId = sessionManager.currentSession.value?.userId
        val rentalsResult = supabase.fetchRentals(userId)
        if (rentalsResult.isSuccess) {
          val fetchedRentals = rentalsResult.getOrNull().orEmpty()
          _activeRentals.value = fetchedRentals
          checkRentalExpirations(fetchedRentals)
        }
      } catch (e: Exception) {
        Log.e(tag, "Error during refreshData", e)
      } finally {
        _isLoading.value = false
      }
    }
  }

  private fun checkRentalExpirations(rentals: List<ActiveRental>) {
    val now = System.currentTimeMillis()
    scope.launch(Dispatchers.IO) {
      for (rental in rentals) {
        if (rental.status.equals("ACTIVE", ignoreCase = true)) {
          val totalDurationMillis = rental.durationDays.toLong() * 24 * 60 * 60 * 1000L
          val elapsed = now - rental.startTimeMillis
          val remaining = totalDurationMillis - elapsed

          if (remaining <= 0) {
            // Expired! Update rental status in Supabase
            supabase.updateRentalStatus(rental.id, "EXPIRED")

            // Decrement account usage in Supabase
            val account = _accounts.value.find { it.id == rental.accountId }
            if (account != null) {
              val newUsers = (account.currentUsers - 1).coerceAtLeast(0)
              val isOccupied = newUsers >= account.maxUsers
              supabase.updateAccountUsage(account.id, newUsers, account.rentedCount, isOccupied)
            }

            postNotification(
              title = "Aluguel Expirado",
              message = "O tempo de aluguel da conta '${rental.accountTitle}' expirou. O acesso às credenciais foi bloqueado.",
              type = NotificationType.WARNING
            )
          } else if (remaining < 24 * 60 * 60 * 1000L) {
            val hours = (remaining / (60 * 60 * 1000L)).coerceAtLeast(1)
            postNotification(
              title = "Aluguel Expirando",
              message = "Atenção: O aluguel da conta '${rental.accountTitle}' expira em cerca de ${hours}h. Renove para não perder o acesso!",
              type = NotificationType.INFO
            )
          }
        }
      }
    }
  }

  fun generateSecurePassword(): String {
    val prefixes = listOf("Cyber", "Alpha", "Safe", "Titan", "Vip", "Pro", "Shield", "Neon")
    val symbols = listOf("!", "@", "#", "$", "*")
    val randomPrefix = prefixes.random()
    val randomNum = (1000..9999).random()
    val randomSymbol = symbols.random()
    return "$randomPrefix$randomSymbol$randomNum"
  }

  fun postNotification(title: String, message: String, type: NotificationType = NotificationType.INFO) {
    if (_inAppNotifications.value.none { it.title == title }) {
      val notification = InAppNotification(
        id = UUID.randomUUID().toString(),
        title = title,
        message = message,
        type = type
      )
      _inAppNotifications.value = listOf(notification) + _inAppNotifications.value
    }
  }

  fun dismissNotification(id: String) {
    _inAppNotifications.value = _inAppNotifications.value.filterNot { it.id == id }
  }

  fun getAccountById(id: String): SharedAccount? {
    return _accounts.value.find { it.id == id } ?: _accounts.value.firstOrNull()
  }

  fun toggleLike(accountId: String) {
    val current = _accounts.value
    var updatedLikes = 0
    _accounts.value = current.map { acc ->
      if (acc.id == accountId) {
        val newIsLiked = !acc.isLiked
        val newLikes = if (newIsLiked) acc.likesCount + 1 else (acc.likesCount - 1).coerceAtLeast(0)
        updatedLikes = newLikes
        acc.copy(isLiked = newIsLiked, likesCount = newLikes)
      } else acc
    }

    scope.launch(Dispatchers.IO) {
      supabase.toggleLike(accountId, updatedLikes)
    }
  }

  fun recordView(accountId: String) {
    val account = _accounts.value.find { it.id == accountId } ?: return
    val currentViews = account.viewsCount
    val newViews = if (currentViews.contains("k")) {
      currentViews
    } else {
      val count = (currentViews.replace("+", "").toIntOrNull() ?: 100) + 1
      "$count"
    }

    scope.launch(Dispatchers.IO) {
      supabase.incrementViews(accountId, newViews)
    }
  }

  fun calculatePrice(account: SharedAccount, days: Int, people: Int): RentalCalculation {
    val finalPrice: Double
    val discountPercent: Int
    val rawTotal: Double

    if (account.id == "acc_primary" && days == 3 && people == 2 && account.customDiscountPercent == 0) {
      finalPrice = 149.90
      discountPercent = 25
      rawTotal = 199.90
    } else {
      val baseCost = (account.basePricePerDay * days) * (1.0 + (people - 1) * 0.65)
      var disc = 10
      if (days >= 3) disc += 10
      if (days >= 7) disc += 10
      if (people >= 2) disc += 5
      if (people >= 3) disc += 5
      if (account.customDiscountPercent > 0) {
        disc += account.customDiscountPercent
      }
      disc = disc.coerceAtMost(55)

      discountPercent = disc
      rawTotal = baseCost * 1.30
      finalPrice = rawTotal * (1.0 - (discountPercent / 100.0))
    }

    val discountAmount = rawTotal - finalPrice
    val formattedPrice = String.format(Locale("pt", "BR"), "R$ %.2f", finalPrice)

    return RentalCalculation(
      days = days,
      people = people,
      rawTotal = rawTotal,
      discountPercent = discountPercent,
      discountAmount = discountAmount,
      finalPrice = finalPrice,
      formattedPrice = formattedPrice
    )
  }

  suspend fun createAccountWithSupabase(
    title: String,
    category: String,
    description: String,
    basePrice: Double,
    maxPeople: Int,
    avatarRes: Int,
    avatarUrl: String = "",
    model3dUrl: String = "",
    username: String,
    pass: String
  ): Result<SharedAccount> {
    val currentUserId = sessionManager.currentSession.value?.userId
    val token = sessionManager.currentSession.value?.accessToken

    val newId = "acc_" + UUID.randomUUID().toString().take(8)
    val defaultItems = listOf(
      IncludedItem(newId + "_item1", "Item Básico", R.drawable.item_hat, "", "Acessório padrão", "Pose Aventura"),
      IncludedItem(newId + "_item2", "Mochila Pro", R.drawable.item_backpack, "", "Inventário expandido", "Pose Viagem"),
      IncludedItem(newId + "_item3", "Acesso VIP", R.drawable.item_boots, "", "Passes e benefícios", "Pose Caminhada"),
      IncludedItem(newId + "_item4", "Guia Rápido", R.drawable.item_map, "", "Instruções passo a passo", "Pose Exploração")
    )

    val newAccount = SharedAccount(
      id = newId,
      title = title.ifBlank { "Nova Conta Compartilhada" },
      category = category.ifBlank { "Geral" },
      description = description.ifBlank { "Conta compartilhada verificada com suporte 24h e avatar 3D." },
      basePricePerDay = basePrice.coerceAtLeast(9.90),
      currentUsers = 1,
      maxUsers = maxPeople.coerceIn(1, 10),
      rating = 5.0,
      viewsCount = "100+",
      likesCount = 1,
      isLiked = false,
      rentedCount = 0,
      last24hRentalsText = "Recém adicionada",
      avatarDrawableRes = avatarRes,
      avatarUrl = avatarUrl,
      model3dUrl = model3dUrl,
      itemsIncluded = defaultItems,
      credentials = AccountCredentials(
        username = username.ifBlank { "user_shared_" + (100..999).random() },
        password = pass.ifBlank { generateSecurePassword() },
        pin = (1000..9999).random().toString(),
        server = "BR-Sudeste Cloud",
        profileAssigned = "Perfil Privado #" + (1..maxPeople).random(),
        rules = "Compartilhar a senha com terceiros resulta em banimento permanente."
      ),
      isOccupied = false,
      createdBy = currentUserId,
      isActive = true
    )

    val insertResult = supabase.insertAccount(newAccount, token)
    if (insertResult.isFailure) {
      Log.w(tag, "Supabase insert failed, keeping local fallback", insertResult.exceptionOrNull())
    }

    _accounts.value = listOf(newAccount) + _accounts.value
    postNotification(
      title = "Conta Criada!",
      message = "O card '${newAccount.title}' foi publicado com sucesso no Feed.",
      type = NotificationType.SUCCESS
    )

    return Result.success(newAccount)
  }

  suspend fun updateCredentials(
    accountId: String,
    newUsername: String,
    newPass: String
  ): Result<Boolean> {
    val currentUserId = sessionManager.currentSession.value?.userId

    // Update locally
    _accounts.value = _accounts.value.map { acc ->
      if (acc.id == accountId) {
        acc.copy(
          credentials = acc.credentials.copy(
            username = newUsername,
            password = newPass
          )
        )
      } else acc
    }

    val res = supabase.updateAccountCredentials(accountId, newUsername, newPass, currentUserId)
    if (res.isSuccess) {
      postNotification(
        title = "Credenciais Atualizadas",
        message = "As novas credenciais foram salvas no Supabase e passam a valer para os próximos aluguéis.",
        type = NotificationType.SUCCESS
      )
    }
    return res
  }

  suspend fun toggleCardActive(accountId: String, isActive: Boolean): Result<Boolean> {
    _accounts.value = _accounts.value.map { acc ->
      if (acc.id == accountId) acc.copy(isActive = isActive) else acc
    }

    val res = supabase.toggleAccountActive(accountId, isActive)
    postNotification(
      title = if (isActive) "Card Ativado" else "Card Desativado",
      message = if (isActive) "O card agora aparece normalmente no Feed público." else "O card foi ocultado do Feed.",
      type = NotificationType.INFO
    )
    return res
  }

  suspend fun deleteAccount(accountId: String): Result<Boolean> {
    val acc = _accounts.value.find { it.id == accountId }
    _accounts.value = _accounts.value.filterNot { it.id == accountId }

    val res = supabase.deleteAccount(accountId)
    postNotification(
      title = "Card Removido",
      message = "O card '${acc?.title ?: ""}' foi excluído do Supabase.",
      type = NotificationType.INFO
    )
    return res
  }

  suspend fun updatePricing(
    accountId: String,
    basePrice: Double,
    customDiscount: Int
  ): Result<Boolean> {
    _accounts.value = _accounts.value.map { acc ->
      if (acc.id == accountId) {
        acc.copy(
          basePricePerDay = basePrice,
          customDiscountPercent = customDiscount
        )
      } else acc
    }
    val res = supabase.updateAccountPricing(accountId, basePrice, customDiscount)
    postNotification(
      title = "Preços Atualizados",
      message = "Regras de preço e desconto progressivo salvas com sucesso.",
      type = NotificationType.SUCCESS
    )
    return res
  }

  suspend fun getCredentialHistory(accountId: String): List<CredentialHistoryItem> {
    val res = supabase.fetchCredentialHistory(accountId)
    return res.getOrNull().orEmpty()
  }

  suspend fun getAccountRentals(accountId: String): List<ActiveRental> {
    val res = supabase.fetchAccountRentals(accountId)
    return res.getOrNull().orEmpty()
  }

  suspend fun confirmPixRentalWithSupabase(
    account: SharedAccount,
    days: Int,
    peopleCount: Int,
    totalPaid: Double
  ): ActiveRental {
    val currentUserId = sessionManager.currentSession.value?.userId ?: "guest_user"
    val token = sessionManager.currentSession.value?.accessToken

    // Snapshot of current credentials at rental time!
    val currentCredentials = account.credentials

    val rental = ActiveRental(
      id = "rent_" + UUID.randomUUID().toString().take(8),
      accountId = account.id,
      userId = currentUserId,
      accountTitle = account.title,
      avatarDrawableRes = account.avatarDrawableRes,
      avatarUrl = account.avatarUrl,
      days = days,
      peopleCount = peopleCount,
      totalPaid = totalPaid,
      startTimeMillis = System.currentTimeMillis(),
      durationDays = days,
      credentials = currentCredentials,
      status = "ACTIVE"
    )

    _activeRentals.value = listOf(rental) + _activeRentals.value

    // Update account usage locally
    val updatedCurrent = (account.currentUsers + 1).coerceAtMost(account.maxUsers)
    val updatedOccupied = updatedCurrent >= account.maxUsers
    val updatedRentedCount = account.rentedCount + 1

    _accounts.value = _accounts.value.map { acc ->
      if (acc.id == account.id) {
        acc.copy(
          currentUsers = updatedCurrent,
          rentedCount = updatedRentedCount,
          isOccupied = updatedOccupied
        )
      } else acc
    }

    // Persist to Supabase in background
    scope.launch(Dispatchers.IO) {
      supabase.insertRental(rental, token)
      supabase.updateAccountUsage(account.id, updatedCurrent, updatedRentedCount, updatedOccupied)
    }

    postNotification(
      title = "Pagamento PIX Aprovado!",
      message = "Aluguel ativo por $days dias. Suas credenciais foram liberadas!",
      type = NotificationType.SUCCESS
    )

    return rental
  }

  fun renewRental(rentalId: String, extraDays: Int) {
    val rental = _activeRentals.value.find { it.id == rentalId }
    val account = rental?.let { r -> _accounts.value.find { it.id == r.accountId } }
    // If creator updated credentials, renewal can optionally upgrade credentials or maintain active access
    val updatedCredentials = account?.credentials

    _activeRentals.value = _activeRentals.value.map { r ->
      if (r.id == rentalId) {
        r.copy(
          days = r.days + extraDays,
          durationDays = r.durationDays + extraDays,
          status = "ACTIVE",
          credentials = updatedCredentials ?: r.credentials
        )
      } else r
    }

    scope.launch(Dispatchers.IO) {
      supabase.updateRentalDuration(rentalId, extraDays, updatedCredentials)
    }

    postNotification(
      title = "Aluguel Renovado",
      message = "Mais $extraDays dias adicionados ao seu plano com sucesso!",
      type = NotificationType.SUCCESS
    )
  }

  fun cancelRental(rentalId: String) {
    val rental = _activeRentals.value.find { it.id == rentalId }
    _activeRentals.value = _activeRentals.value.filterNot { it.id == rentalId }

    scope.launch(Dispatchers.IO) {
      supabase.updateRentalStatus(rentalId, "CANCELLED")
      if (rental != null) {
        val account = _accounts.value.find { it.id == rental.accountId }
        if (account != null) {
          val newUsers = (account.currentUsers - 1).coerceAtLeast(0)
          supabase.updateAccountUsage(account.id, newUsers, account.rentedCount, false)
        }
      }
    }
  }

  // -------------------------------------------------------------
  // AUTH WRAPPERS
  // -------------------------------------------------------------

  suspend fun login(email: String, pass: String): Result<UserSession> {
    val res = supabase.signIn(email, pass)
    if (res.isSuccess) {
      val session = res.getOrThrow()
      sessionManager.saveSession(session)
      refreshData()
      postNotification(
        title = "Bem-vindo de volta!",
        message = "Login realizado com sucesso como ${session.email}.",
        type = NotificationType.SUCCESS
      )
    }
    return res
  }

  suspend fun register(email: String, pass: String): Result<UserSession> {
    val res = supabase.signUp(email, pass)
    if (res.isSuccess) {
      val session = res.getOrThrow()
      sessionManager.saveSession(session)
      refreshData()
      postNotification(
        title = "Conta criada!",
        message = "Seu cadastro foi realizado com sucesso.",
        type = NotificationType.SUCCESS
      )
    }
    return res
  }

  fun logout() {
    val userEmail = sessionManager.currentSession.value?.email ?: ""
    sessionManager.clearSession()
    _activeRentals.value = emptyList()
    refreshData()
    postNotification(
      title = "Desconectado",
      message = "Você encerrou a sessão ($userEmail).",
      type = NotificationType.INFO
    )
  }

  // -------------------------------------------------------------
  // SEED FALLBACK (Ensures instant UI on start)
  // -------------------------------------------------------------

  private fun loadInitialSeed() {
    val initialItems = listOf(
      IncludedItem("item_hat", "Chapéu", R.drawable.item_hat, "", "Chapéu de safari explorador", "Pose Aventura"),
      IncludedItem("item_backpack", "Mochila", R.drawable.item_backpack, "", "Mochila táctica azul com compartimentos", "Pose Viagem"),
      IncludedItem("item_boots", "Botas", R.drawable.item_boots, "", "Botas todo-terreno de couro", "Pose Caminhada"),
      IncludedItem("item_map", "Mapa", R.drawable.item_map, "", "Mapa de navegação detalhado com GPS", "Pose Exploração")
    )

    val seedAccounts = listOf(
      SharedAccount(
        id = "acc_primary",
        title = "Aventureiro Deluxe Pass",
        category = "Aventura & RPG",
        description = "Acesso completo à conta compartilhada Aventureiro com skins lendárias, inventário desbloqueado e perfil VIP.",
        basePricePerDay = 29.90,
        currentUsers = 2,
        maxUsers = 4,
        rating = 4.8,
        viewsCount = "1.2k",
        likesCount = 342,
        isLiked = false,
        rentedCount = 12,
        last24hRentalsText = "+2.345 alugaram nas últimas 24h",
        avatarDrawableRes = R.drawable.avatar_character_pedestal,
        model3dUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Fox/glTF-Binary/Fox.glb",
        itemsIncluded = initialItems,
        credentials = AccountCredentials(
          username = "aventureiro_pro_04",
          password = "SafeRentPass@2026",
          pin = "7821",
          server = "América do Sul (Latência 12ms)",
          profileAssigned = "Perfil #2 (Privado & Seguro)",
          rules = "Compartilhar a senha com terceiros resulta em banimento permanente."
        ),
        isActive = true
      ),
      SharedAccount(
        id = "acc_gamer",
        title = "Gamer Girl Pro Stream",
        category = "Streaming & Jogos",
        description = "Conta compartilhada com catálogo Gamer completo, passes de temporada ativados e cosméticos épicos de anime.",
        basePricePerDay = 34.90,
        currentUsers = 3,
        maxUsers = 5,
        rating = 5.0,
        viewsCount = "4.1k",
        likesCount = 1240,
        isLiked = false,
        rentedCount = 76,
        last24hRentalsText = "+890 alugaram nas últimas 24h",
        avatarDrawableRes = R.drawable.char_gamer,
        model3dUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Binary/Duck.glb",
        itemsIncluded = listOf(
          IncludedItem("g_headset", "Headset RGB", R.drawable.item_hat, "", "Fone espacial surround 7.1", "Pose Streamer"),
          IncludedItem("g_jacket", "Jaqueta Neon", R.drawable.item_backpack, "", "Casaco cibernético reativo à música", "Pose Neon"),
          IncludedItem("g_sneakers", "Tênis Glow", R.drawable.item_boots, "", "Calçado de led programável", "Pose High Jump"),
          IncludedItem("g_hud", "Game Pass", R.drawable.item_map, "", "Acesso a 500+ títulos AAA", "Pose Vitória")
        ),
        credentials = AccountCredentials(
          username = "neon_gamer_vip",
          password = "CyberNeon2026!",
          pin = "1337",
          server = "Global Fast CDN",
          profileAssigned = "Perfil #3 (Gamer Stream)",
          rules = "Compartilhar a senha com terceiros resulta em banimento permanente."
        ),
        isActive = true
      ),
      SharedAccount(
        id = "acc_cyber",
        title = "Cyber Soldier Mech VIP",
        category = "Ficção & Ação",
        description = "Armaduras de nanotecnologia e armas futuristas prontas para combate. Conta compartilhada verificada e otimizada.",
        basePricePerDay = 39.90,
        currentUsers = 4,
        maxUsers = 4,
        rating = 4.9,
        viewsCount = "3.8k",
        likesCount = 920,
        isLiked = false,
        rentedCount = 53,
        last24hRentalsText = "+1.120 alugaram nas últimas 24h",
        avatarDrawableRes = R.drawable.char_cyber,
        model3dUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/CesiumMilkTruck/glTF-Binary/CesiumMilkTruck.glb",
        itemsIncluded = listOf(
          IncludedItem("c_armor", "Nano Armadura", R.drawable.item_hat, "", "Exoesqueleto de titânio reforçado", "Pose Sentinela"),
          IncludedItem("c_rifle", "Rifle Plasma", R.drawable.item_backpack, "", "Canhão de energia com mira laser", "Pose Mira"),
          IncludedItem("c_boots", "Propulsores", R.drawable.item_boots, "", "Botas antigravidade para voo curto", "Pose Decolagem"),
          IncludedItem("c_radar", "Radar Satélite", R.drawable.item_map, "", "Scanner térmico de longo alcance", "Pose Varredura")
        ),
        credentials = AccountCredentials(
          username = "mech_soldier_09",
          password = "MechProtocol#99",
          pin = "9090",
          server = "Ultra Secure BR-01",
          profileAssigned = "Perfil #1 (Titan Unit)",
          rules = "Compartilhar a senha com terceiros resulta em banimento permanente."
        ),
        isOccupied = true,
        isActive = true
      )
    )

    _accounts.value = seedAccounts
  }
}
