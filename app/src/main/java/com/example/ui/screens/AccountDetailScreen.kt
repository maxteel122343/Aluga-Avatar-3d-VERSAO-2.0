package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.data.model.ActiveRental
import com.example.data.model.IncludedItem
import com.example.data.model.SharedAccount
import com.example.data.repository.AccountRepository
import com.example.ui.components.BottomActionButtons
import com.example.ui.components.CredentialsDeliveryDialog
import com.example.ui.components.IncludedItemsSection
import com.example.ui.components.PedestalView
import com.example.ui.components.PixCheckoutBottomSheet
import com.example.ui.components.PriceCalculatorCard
import com.example.ui.components.SocialProofRow
import com.example.ui.components.TopSocialProofPill
import com.example.ui.components.WarrantyModal
import com.example.ui.theme.Gray900
import com.example.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
  account: SharedAccount,
  repository: AccountRepository,
  onRequestAuth: (reason: String) -> Unit = {},
  onNavigateBack: (() -> Unit)? = null,
  onGoToRentals: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val session by repository.currentSession.collectAsState()

  var days by remember(account.id) { mutableIntStateOf(3) }
  var people by remember(account.id) { mutableIntStateOf(2) }
  var selectedItem by remember(account.id) { mutableStateOf<IncludedItem?>(null) }

  var showPixCheckout by remember { mutableStateOf(false) }
  var showWarrantyModal by remember { mutableStateOf(false) }
  var completedRental by remember { mutableStateOf<ActiveRental?>(null) }

  val calculation = remember(account.id, days, people) {
    repository.calculatePrice(account, days, people)
  }

  // Record view on enter
  LaunchedEffect(account.id) {
    repository.recordView(account.id)
  }

  fun handleRentClick() {
    if (session == null) {
      onRequestAuth("Faça login para alugar '${account.title}' e receber suas credenciais seguras.")
    } else {
      showPixCheckout = true
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = account.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
        },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Gray900
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = White
        )
      )
    },
    bottomBar = {
      BottomActionButtons(
        isOccupied = account.isOccupied,
        currentUsers = account.currentUsers,
        onRentClick = { handleRentClick() },
        onWarrantyClick = { showWarrantyModal = true },
        modifier = Modifier.background(White)
      )
    },
    containerColor = White
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Social Proof Pill (+2.345 alugaram nas últimas 24h)
      TopSocialProofPill(
        rentalsText = account.last24hRentalsText,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
      )

      // 2. 3D Pedestal Area with Character / GLB 3D Model and "Alugar" button
      PedestalView(
        avatarRes = account.avatarDrawableRes,
        avatarUrl = account.avatarUrl,
        model3dUrl = account.model3dUrl,
        selectedItem = selectedItem,
        onRentClick = { handleRentClick() }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Price Calculator & Dynamic Pricing ("Calcule o valor", Dias, Pessoas, Preço ao vivo)
      PriceCalculatorCard(
        days = days,
        people = people,
        formattedPrice = calculation.formattedPrice,
        discountPercent = calculation.discountPercent,
        onDaysChange = { days = it },
        onPeopleChange = { people = it }
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Social Proof Row (4.8 estrelas, 1.2k views, 342 likes, 12 já alugaram)
      SocialProofRow(
        rating = account.rating,
        views = account.viewsCount,
        likes = account.likesCount,
        isLiked = account.isLiked,
        rentedCount = account.rentedCount,
        onLikeToggle = { repository.toggleLike(account.id) }
      )

      // 5. Itens Incluídos (Chapéu, Mochila, Botas, Mapa)
      IncludedItemsSection(
        items = account.itemsIncluded,
        selectedItem = selectedItem,
        onItemClick = { item ->
          selectedItem = if (selectedItem?.id == item.id) null else item
        }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Pix Payment Bottom Sheet
  if (showPixCheckout) {
    PixCheckoutBottomSheet(
      account = account,
      calculation = calculation,
      onDismiss = { showPixCheckout = false },
      onPaymentSuccess = {
        scope.launch {
          val rental = repository.confirmPixRentalWithSupabase(
            account = account,
            days = days,
            peopleCount = people,
            totalPaid = calculation.finalPrice
          )
          showPixCheckout = false
          completedRental = rental
        }
      }
    )
  }

  // Warranty Information Modal
  if (showWarrantyModal) {
    WarrantyModal(
      onDismiss = { showWarrantyModal = false }
    )
  }

  // Delivery of credentials dialog after successful payment
  completedRental?.let { rental ->
    CredentialsDeliveryDialog(
      rental = rental,
      onDismiss = { completedRental = null },
      onGoToRentals = {
        completedRental = null
        onGoToRentals()
      }
    )
  }
}
