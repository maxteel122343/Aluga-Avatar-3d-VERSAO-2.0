package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ActiveRental
import com.example.data.model.CredentialHistoryItem
import com.example.data.model.IncludedItem
import com.example.data.model.SharedAccount
import com.example.data.repository.AccountRepository
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray50
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.GreenTagBg
import com.example.ui.theme.GreenTagText
import com.example.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorPanelScreen(
  repository: AccountRepository,
  onRequestAuth: (reason: String) -> Unit = {},
  onAccountCreated: (SharedAccount) -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val session by repository.currentSession.collectAsState()
  val allAccounts by repository.accounts.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Novo Card, 1: Meus Cards

  // Modals for managing card
  var editingCardCredentials by remember { mutableStateOf<SharedAccount?>(null) }
  var editingCardPricing by remember { mutableStateOf<SharedAccount?>(null) }
  var editingCardItems by remember { mutableStateOf<SharedAccount?>(null) }
  var historyCard by remember { mutableStateOf<SharedAccount?>(null) }
  var deletingCard by remember { mutableStateOf<SharedAccount?>(null) }
  var viewingRentalsCard by remember { mutableStateOf<SharedAccount?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Painel do Criador",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Gray900
            )
            Text(
              text = if (selectedTab == 0) "Criar e publicar nova conta" else "Gerenciar contas e credenciais",
              fontSize = 12.sp,
              color = Gray500
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
      )
    },
    containerColor = White
  ) { paddingValues ->
    if (session == null) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(80.dp)
            .background(Color(0xFFEFF6FF), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier.size(40.dp)
          )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Acesso Restrito ao Criador",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Faça login com sua conta do Supabase para publicar novas contas, alterar senhas e gerenciar aluguéis.",
          fontSize = 14.sp,
          color = Gray500,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
          onClick = { onRequestAuth("Faça login para acessar o painel de criação e controle.") },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("creator_login_prompt_button")
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Entrar ou Cadastrar", fontWeight = FontWeight.Bold)
        }
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Sub-Tabs: "Criar Card" vs "Meus Cards & Gestão"
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = White,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = BluePrimary
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Criar Card", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
            }
          },
          selectedContentColor = BluePrimary,
          unselectedContentColor = Gray500,
          modifier = Modifier.testTag("tab_create_card")
        )

        val myCardsCount = allAccounts.count { it.createdBy == session?.userId || it.id.startsWith("acc_") }
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Meus Cards ($myCardsCount)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
            }
          },
          selectedContentColor = BluePrimary,
          unselectedContentColor = Gray500,
          modifier = Modifier.testTag("tab_manage_cards")
        )
      }

      if (selectedTab == 0) {
        CreateNewCardTab(
          repository = repository,
          onAccountCreated = onAccountCreated
        )
      } else {
        ManageCardsTab(
          repository = repository,
          onEditCredentials = { editingCardCredentials = it },
          onEditPricing = { editingCardPricing = it },
          onEditItems = { editingCardItems = it },
          onViewHistory = { historyCard = it },
          onDeleteCard = { deletingCard = it },
          onViewRentals = { viewingRentalsCard = it }
        )
      }
    }
  }

  // Dialog: Edit Credentials
  editingCardCredentials?.let { card ->
    EditCredentialsDialog(
      card = card,
      repository = repository,
      onDismiss = { editingCardCredentials = null }
    )
  }

  // Dialog: Edit Pricing & Discount Rules
  editingCardPricing?.let { card ->
    EditPricingDialog(
      card = card,
      repository = repository,
      onDismiss = { editingCardPricing = null }
    )
  }

  // Dialog: Edit Included Items & Pose GIFs
  editingCardItems?.let { card ->
    EditAccountItemsDialog(
      card = card,
      repository = repository,
      onDismiss = { editingCardItems = null }
    )
  }

  // Dialog: Credential History
  historyCard?.let { card ->
    CredentialHistoryDialog(
      card = card,
      repository = repository,
      onDismiss = { historyCard = null }
    )
  }

  // Dialog: Delete Card Confirmation
  deletingCard?.let { card ->
    AlertDialog(
      onDismissRequest = { deletingCard = null },
      title = { Text("Excluir Card?", fontWeight = FontWeight.Bold) },
      text = { Text("Tem certeza que deseja remover '${card.title}' do Supabase? Os aluguéis ativos permanecerão até expirarem.") },
      confirmButton = {
        Button(
          onClick = {
            scope.launch {
              repository.deleteAccount(card.id)
              deletingCard = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
          Text("Sim, Excluir")
        }
      },
      dismissButton = {
        TextButton(onClick = { deletingCard = null }) {
          Text("Cancelar", color = Gray500)
        }
      }
    )
  }

  // Dialog: View Account Rentals History
  viewingRentalsCard?.let { card ->
    AccountRentalsDialog(
      card = card,
      repository = repository,
      onDismiss = { viewingRentalsCard = null }
    )
  }
}

data class CreatorItemDraft(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "",
  val poseName: String = "",
  val mediaUrl: String = "",
  val iconRes: Int = R.drawable.item_hat
)

@Composable
fun CreateNewCardTab(
  repository: AccountRepository,
  onAccountCreated: (SharedAccount) -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Aventura & RPG") }
  var description by remember { mutableStateOf("") }
  var basePriceText by remember { mutableStateOf("29.90") }
  var maxPeople by remember { mutableIntStateOf(4) }
  var selectedAvatarRes by remember { mutableIntStateOf(R.drawable.avatar_character_pedestal) }

  var customAvatarUrl by remember { mutableStateOf("") }
  var model3dUrl by remember {
    mutableStateOf("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Fox/glTF-Binary/Fox.glb")
  }

  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  var isUploadingAvatar by remember { mutableStateOf(false) }
  var isUploadingModel by remember { mutableStateOf(false) }
  var isPublishing by remember { mutableStateOf(false) }

  var draftItems by remember {
    mutableStateOf(
      listOf(
        CreatorItemDraft(
          id = "draft_1",
          name = "Chapéu Safari",
          poseName = "Pose Aventura",
          mediaUrl = "https://media.giphy.com/media/3o7TKTDnUxE0g2fSE8/giphy.gif",
          iconRes = R.drawable.item_hat
        ),
        CreatorItemDraft(
          id = "draft_2",
          name = "Mochila Tática",
          poseName = "Pose Viagem",
          mediaUrl = "https://media.giphy.com/media/l0HlHJGHe3yAMhdQY/giphy.gif",
          iconRes = R.drawable.item_backpack
        ),
        CreatorItemDraft(
          id = "draft_3",
          name = "Botas de Couro",
          poseName = "Pose Caminhada",
          mediaUrl = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif",
          iconRes = R.drawable.item_boots
        )
      )
    )
  }

  var uploadingItemIdx by remember { mutableIntStateOf(-1) }

  val itemPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null && uploadingItemIdx in draftItems.indices) {
      val targetIdx = uploadingItemIdx
      scope.launch {
        try {
          val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
          }
          if (bytes != null) {
            val fileName = "item_pose_${UUID.randomUUID().toString().take(8)}.gif"
            val uploadRes = repository.supabase.uploadFile("assets", fileName, bytes, "image/gif")
            if (uploadRes.isSuccess) {
              val url = uploadRes.getOrThrow()
              draftItems = draftItems.mapIndexed { idx, itm ->
                if (idx == targetIdx) itm.copy(mediaUrl = url) else itm
              }
              Toast.makeText(context, "Mídia da pose salva no Storage!", Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Falha no upload do arquivo", Toast.LENGTH_SHORT).show()
        } finally {
          uploadingItemIdx = -1
        }
      }
    }
  }

  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      isUploadingAvatar = true
      scope.launch {
        try {
          val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
          }
          if (bytes != null) {
            val fileName = "avatar_${UUID.randomUUID().toString().take(8)}.jpg"
            val uploadRes = repository.supabase.uploadFile("assets", fileName, bytes, "image/jpeg")
            if (uploadRes.isSuccess) {
              customAvatarUrl = uploadRes.getOrThrow()
              Toast.makeText(context, "Avatar salvo no Supabase Storage!", Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Falha no upload da imagem", Toast.LENGTH_SHORT).show()
        } finally {
          isUploadingAvatar = false
        }
      }
    }
  }

  val modelPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null) {
      isUploadingModel = true
      scope.launch {
        try {
          val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
          }
          if (bytes != null) {
            val fileName = "model_${UUID.randomUUID().toString().take(8)}.glb"
            val uploadRes = repository.supabase.uploadFile("models3d", fileName, bytes, "model/gltf-binary")
            if (uploadRes.isSuccess) {
              model3dUrl = uploadRes.getOrThrow()
              Toast.makeText(context, "Modelo 3D salvo no Supabase Storage!", Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Falha no upload do modelo 3D", Toast.LENGTH_SHORT).show()
        } finally {
          isUploadingModel = false
        }
      }
    }
  }

  val avatarOptions = listOf(
    Pair("Aventureiro 3D", R.drawable.avatar_character_pedestal),
    Pair("Gamer Neon", R.drawable.char_gamer),
    Pair("Cyber Robot", R.drawable.char_cyber)
  )

  val model3dPresets = listOf(
    Pair("Fox 3D (Animada)", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Fox/glTF-Binary/Fox.glb"),
    Pair("Duck (Mascote)", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Binary/Duck.glb"),
    Pair("Cyber Truck", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/CesiumMilkTruck/glTF-Binary/CesiumMilkTruck.glb")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .padding(bottom = 80.dp)
  ) {
    // Section 1: Avatar 3D
    Text(
      text = "1. Avatar & Modelo 3D para o Pedestal",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = Gray900
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      avatarOptions.forEach { (name, res) ->
        val isSelected = selectedAvatarRes == res && customAvatarUrl.isBlank()
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Gray50)
            .border(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) BluePrimary else Gray200,
              shape = RoundedCornerShape(14.dp)
            )
            .clickable {
              selectedAvatarRes = res
              customAvatarUrl = ""
            }
            .padding(8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(70.dp)
              .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = res),
              contentDescription = name,
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BluePrimary else Gray700
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Upload custom image
    Surface(
      color = Color(0xFFF9FAFB),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = "Upload de Avatar Personalizado", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Gray900)
          Text(
            text = if (customAvatarUrl.isNotBlank()) "Avatar enviado para o Supabase!" else "Selecione foto da galeria",
            fontSize = 11.sp,
            color = if (customAvatarUrl.isNotBlank()) GreenIcon else Gray500
          )
        }
        Button(
          onClick = {
            imagePickerLauncher.launch(
              androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (customAvatarUrl.isNotBlank()) GreenIcon else BluePrimary,
            contentColor = White
          ),
          shape = RoundedCornerShape(8.dp),
          enabled = !isUploadingAvatar
        ) {
          if (isUploadingAvatar) {
            CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
          } else {
            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (customAvatarUrl.isNotBlank()) "Trocar" else "Upload", fontSize = 12.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3D Model glb
    Text(text = "Modelo 3D (.glb)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gray900)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      model3dPresets.forEach { (label, url) ->
        val isSelected = model3dUrl == url
        Surface(
          color = if (isSelected) Color(0xFFEFF6FF) else Gray100,
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BluePrimary else Gray200),
          modifier = Modifier
            .weight(1f)
            .clickable { model3dUrl = url }
        ) {
          Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.ViewInAr,
              contentDescription = null,
              tint = if (isSelected) BluePrimary else Gray500,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) BluePrimary else Gray700,
              maxLines = 1
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = model3dUrl,
      onValueChange = { model3dUrl = it },
      label = { Text("URL do Modelo 3D (.glb)") },
      placeholder = { Text("https://.../meu_modelo.glb") },
      trailingIcon = {
        IconButton(
          onClick = { modelPickerLauncher.launch("*/*") },
          enabled = !isUploadingModel
        ) {
          if (isUploadingModel) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
          } else {
            Icon(Icons.Default.CloudUpload, contentDescription = "Upload .glb", tint = BluePrimary)
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Section 2: Account Details
    Text(
      text = "2. Detalhes da Conta (Obrigatório)",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = Gray900
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = title,
      onValueChange = { title = it },
      label = { Text("Nome da Conta / Passe *") },
      placeholder = { Text("Ex: Aventureiro Deluxe Pass") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("creator_title_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = description,
      onValueChange = { description = it },
      label = { Text("Descrição dos benefícios *") },
      placeholder = { Text("Descreva skins, vantagens e recursos incluídos...") },
      modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .testTag("creator_desc_input"),
      shape = RoundedCornerShape(12.dp),
      maxLines = 3
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = basePriceText,
        onValueChange = { basePriceText = it },
        label = { Text("Preço / Dia (R$) *") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )

      OutlinedTextField(
        value = maxPeople.toString(),
        onValueChange = { it.toIntOrNull()?.let { v -> maxPeople = v } },
        label = { Text("Limite Pessoas *") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 3: Credenciais com Gerador
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "3. Credenciais de Acesso (Obrigatório)",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        Text(
          text = "Entregues ao cliente automaticamente após o PIX.",
          fontSize = 11.sp,
          color = Gray500
        )
      }

      OutlinedButton(
        onClick = {
          password = repository.generateSecurePassword()
          Toast.makeText(context, "Nova senha forte gerada!", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Gerar Senha", fontSize = 11.sp, color = BluePrimary)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = username,
      onValueChange = { username = it },
      label = { Text("Usuário da Conta *") },
      placeholder = { Text("ex: aventureiro_vip_br") },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = password,
      onValueChange = { password = it },
      label = { Text("Senha da Conta *") },
      placeholder = { Text("ex: CyberPass#2026") },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Section 4: Itens Incluídos & GIFs de Pose
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "4. Itens Incluídos & Preview de Pose (GIF)",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        Text(
          text = "Ao tocar no item no pedestal, a animação da pose substitui o avatar 3D.",
          fontSize = 11.sp,
          color = Gray500
        )
      }

      OutlinedButton(
        onClick = {
          draftItems = draftItems + CreatorItemDraft(
            name = "Novo Item #${draftItems.size + 1}",
            poseName = "Pose Especial",
            mediaUrl = "https://media.giphy.com/media/3o7TKMt1VVNkHV2PaE/giphy.gif",
            iconRes = R.drawable.item_map
          )
        },
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Adicionar", fontSize = 11.sp, color = BluePrimary)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    draftItems.forEachIndexed { index, draft ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Item #${index + 1}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = BluePrimary
            )
            if (draftItems.size > 1) {
              IconButton(
                onClick = {
                  draftItems = draftItems.filterIndexed { i, _ -> i != index }
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = draft.name,
              onValueChange = { newName ->
                draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(name = newName) else itm }
              },
              label = { Text("Nome do Item *") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = draft.poseName,
              onValueChange = { newPose ->
                draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(poseName = newPose) else itm }
              },
              label = { Text("Nome da Pose") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = draft.mediaUrl,
            onValueChange = { newUrl ->
              draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(mediaUrl = newUrl) else itm }
            },
            label = { Text("URL do GIF / Vídeo de Pose *") },
            placeholder = { Text("https://.../pose.gif") },
            trailingIcon = {
              IconButton(
                onClick = {
                  uploadingItemIdx = index
                  itemPickerLauncher.launch("image/*")
                }
              ) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload Mídia", tint = BluePrimary)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Quick Presets
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              color = Color(0xFFEFF6FF),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.clickable {
                draftItems = draftItems.mapIndexed { i, itm ->
                  if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/3o7TKTDnUxE0g2fSE8/giphy.gif", poseName = "Pose Aventura") else itm
                }
              }
            ) {
              Text("Aventura GIF", fontSize = 10.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
            }

            Surface(
              color = Color(0xFFEFF6FF),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.clickable {
                draftItems = draftItems.mapIndexed { i, itm ->
                  if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/l0HlHJGHe3yAMhdQY/giphy.gif", poseName = "Pose Viagem") else itm
                }
              }
            ) {
              Text("Viagem GIF", fontSize = 10.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
            }

            Surface(
              color = Color(0xFFEFF6FF),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.clickable {
                draftItems = draftItems.mapIndexed { i, itm ->
                  if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif", poseName = "Pose Caminhada") else itm
                }
              }
            ) {
              Text("Caminhada GIF", fontSize = 10.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Submit Button
    Button(
      onClick = {
        if (title.isBlank()) {
          Toast.makeText(context, "Informe o nome da conta", Toast.LENGTH_SHORT).show()
          return@Button
        }
        if (username.isBlank() || password.isBlank()) {
          Toast.makeText(context, "Username e senha são obrigatórios!", Toast.LENGTH_SHORT).show()
          return@Button
        }

        val price = basePriceText.toDoubleOrNull() ?: 29.90
        val finalIncludedItems = draftItems.filter { it.name.isNotBlank() }.mapIndexed { idx, it ->
          IncludedItem(
            id = "item_${UUID.randomUUID().toString().take(6)}",
            name = it.name,
            iconRes = it.iconRes,
            iconUrl = "",
            description = "Item exclusivo com animação de pose interativa",
            poseName = it.poseName.ifBlank { "Pose Especial" },
            mediaUrl = it.mediaUrl
          )
        }

        isPublishing = true
        scope.launch {
          val result = repository.createAccountWithSupabase(
            title = title,
            category = category,
            description = description,
            basePrice = price,
            maxPeople = maxPeople,
            avatarRes = selectedAvatarRes,
            avatarUrl = customAvatarUrl,
            model3dUrl = model3dUrl,
            username = username,
            pass = password,
            customItems = finalIncludedItems
          )
          isPublishing = false
          if (result.isSuccess) {
            val created = result.getOrThrow()
            Toast.makeText(context, "Card publicado com sucesso no Feed público!", Toast.LENGTH_LONG).show()
            onAccountCreated(created)
          } else {
            Toast.makeText(context, "Falha ao salvar no Supabase", Toast.LENGTH_SHORT).show()
          }
        }
      },
      enabled = !isPublishing,
      colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = White),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("publish_card_button")
    ) {
      if (isPublishing) {
        CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
      } else {
        Icon(imageVector = Icons.Default.Publish, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Publicar Card no Feed", fontSize = 15.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun ManageCardsTab(
  repository: AccountRepository,
  onEditCredentials: (SharedAccount) -> Unit,
  onEditPricing: (SharedAccount) -> Unit,
  onEditItems: (SharedAccount) -> Unit,
  onViewHistory: (SharedAccount) -> Unit,
  onDeleteCard: (SharedAccount) -> Unit,
  onViewRentals: (SharedAccount) -> Unit
) {
  val scope = rememberCoroutineScope()
  val accounts by repository.accounts.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = 80.dp)
  ) {
    items(accounts, key = { it.id }) { card ->
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Header: Title, Active toggle, Category
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = card.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
              )
              Text(
                text = "${card.category} • R$ ${String.format(java.util.Locale("pt", "BR"), "%.2f", card.basePricePerDay)}/dia",
                fontSize = 12.sp,
                color = Gray500
              )
            }

            // Switch to Activate/Deactivate Card
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (card.isActive) "Ativo" else "Oculto",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (card.isActive) GreenIcon else Gray500
              )
              Spacer(modifier = Modifier.width(6.dp))
              Switch(
                checked = card.isActive,
                onCheckedChange = { active ->
                  scope.launch { repository.toggleCardActive(card.id, active) }
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = White,
                  checkedTrackColor = GreenIcon
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Realtime Metrics Pill Row
          Surface(
            color = Gray50,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.People, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = "${card.currentUsers}/${card.maxUsers}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Gray900)
                }
                Text("Usando Agora", fontSize = 10.sp, color = Gray500)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Payments, contentDescription = null, tint = GreenIcon, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = "${card.rentedCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Gray900)
                }
                Text("Total Alugados", fontSize = 10.sp, color = Gray500)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = if (card.isOccupied) "Lotada" else if (card.isAlmostFull) "1 Vaga!" else "Disponível",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (card.isOccupied) Color(0xFFDC2626) else if (card.isAlmostFull) Color(0xFFD97706) else GreenIcon
                )
                Text("Status", fontSize = 10.sp, color = Gray500)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Current Credentials Box
          Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("CREDENCIAIS ATIVAS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                Text(
                  text = "${card.credentials.username}  •  ${card.credentials.password}",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Action Buttons: Edit Credentials, Pricing, History, Delete
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onEditCredentials(card) },
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Mudar Senha", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = { onEditPricing(card) },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Preço/Regras", fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = { onEditItems(card) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Editar Itens & Poses (${card.itemsIncluded.size})", fontSize = 12.sp, color = BluePrimary, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            TextButton(onClick = { onViewHistory(card) }) {
              Icon(Icons.Default.History, contentDescription = null, tint = Gray700, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Histórico de Senhas", fontSize = 11.sp, color = Gray700)
            }

            TextButton(onClick = { onViewRentals(card) }) {
              Icon(Icons.Default.People, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Ver Aluguéis", fontSize = 11.sp, color = BluePrimary)
            }

            IconButton(onClick = { onDeleteCard(card) }) {
              Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// DIALOGS FOR CREATOR MANAGEMENT
// -------------------------------------------------------------

@Composable
fun EditCredentialsDialog(
  card: SharedAccount,
  repository: AccountRepository,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var username by remember { mutableStateOf(card.credentials.username) }
  var password by remember { mutableStateOf(card.credentials.password) }
  var isSaving by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Key, contentDescription = null, tint = BluePrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Atualizar Credenciais", fontWeight = FontWeight.Bold, fontSize = 18.sp)
      }
    },
    text = {
      Column {
        Text(
          text = "As novas credenciais valem imediatamente para os PRÓXIMOS aluguéis. Quem já alugou continua com a senha anterior até o tempo expirar.",
          fontSize = 12.sp,
          color = Gray700,
          lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Novo Usuário") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Nova Senha") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          trailingIcon = {
            IconButton(onClick = {
              password = repository.generateSecurePassword()
            }) {
              Icon(Icons.Default.AutoAwesome, contentDescription = "Gerar senha", tint = BluePrimary)
            }
          }
        )

        Spacer(modifier = Modifier.height(6.dp))
        TextButton(onClick = { password = repository.generateSecurePassword() }) {
          Text("🎲 Gerar Senha Segura Automática", fontSize = 12.sp, color = BluePrimary)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (username.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Preencha usuário e senha", Toast.LENGTH_SHORT).show()
            return@Button
          }
          isSaving = true
          scope.launch {
            repository.updateCredentials(card.id, username, password)
            isSaving = false
            Toast.makeText(context, "Credenciais atualizadas com sucesso!", Toast.LENGTH_SHORT).show()
            onDismiss()
          }
        },
        enabled = !isSaving,
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
      ) {
        if (isSaving) {
          CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
          Text("Salvar Novas Credenciais")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = Gray500)
      }
    }
  )
}

@Composable
fun EditPricingDialog(
  card: SharedAccount,
  repository: AccountRepository,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var basePriceText by remember { mutableStateOf(card.basePricePerDay.toString()) }
  var extraDiscount by remember { mutableIntStateOf(card.customDiscountPercent) }
  var isSaving by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Editar Preço & Descontos", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text("Defina o valor base por dia e bônus de desconto progressivo.", fontSize = 12.sp, color = Gray500)
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = basePriceText,
          onValueChange = { basePriceText = it },
          label = { Text("Preço Base / Dia (R$)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Bônus de Desconto Promocional: $extraDiscount%", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(0, 5, 10, 15, 20).forEach { disc ->
            Surface(
              color = if (extraDiscount == disc) BluePrimary else Gray100,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { extraDiscount = disc }
            ) {
              Text(
                text = "+$disc%",
                color = if (extraDiscount == disc) White else Gray700,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val p = basePriceText.toDoubleOrNull() ?: card.basePricePerDay
          isSaving = true
          scope.launch {
            repository.updatePricing(card.id, p, extraDiscount)
            isSaving = false
            onDismiss()
          }
        },
        enabled = !isSaving,
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
      ) {
        Text("Salvar")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancelar", color = Gray500) }
    }
  )
}

@Composable
fun CredentialHistoryDialog(
  card: SharedAccount,
  repository: AccountRepository,
  onDismiss: () -> Unit
) {
  var history by remember { mutableStateOf<List<CredentialHistoryItem>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }

  androidx.compose.runtime.LaunchedEffect(card.id) {
    history = repository.getCredentialHistory(card.id)
    isLoading = false
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.History, contentDescription = null, tint = BluePrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Histórico de Alterações", fontWeight = FontWeight.Bold, fontSize = 17.sp)
      }
    },
    text = {
      Column(modifier = Modifier.height(280.dp)) {
        Text("Registros de usuários e senhas gravados para esta conta:", fontSize = 12.sp, color = Gray500)
        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(28.dp))
          }
        } else if (history.isEmpty()) {
          Text("Nenhuma alteração registrada ainda.", color = Gray500, fontSize = 13.sp)
        } else {
          LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(history) { item ->
              Surface(
                color = Gray50,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(text = "Usuário: ${item.username}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text(text = "Senha: ${item.password}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(text = "Data: ${item.changedAt.take(19).replace("T", " ")}", fontSize = 10.sp, color = Gray500)
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun AccountRentalsDialog(
  card: SharedAccount,
  repository: AccountRepository,
  onDismiss: () -> Unit
) {
  var rentals by remember { mutableStateOf<List<ActiveRental>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }

  androidx.compose.runtime.LaunchedEffect(card.id) {
    rentals = repository.getAccountRentals(card.id)
    isLoading = false
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.People, contentDescription = null, tint = BluePrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Aluguéis Desta Conta", fontWeight = FontWeight.Bold, fontSize = 17.sp)
      }
    },
    text = {
      Column(modifier = Modifier.height(280.dp)) {
        Text("Histórico de usuários que alugaram '${card.title}':", fontSize = 12.sp, color = Gray500)
        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(28.dp))
          }
        } else if (rentals.isEmpty()) {
          Text("Nenhum aluguel registrado ainda para esta conta.", color = Gray500, fontSize = 13.sp)
        } else {
          LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rentals) { r ->
              val isExpired = r.isExpired()
              Surface(
                color = if (isExpired) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpired) Color(0xFFFECACA) else Color(0xFFA7F3D0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(text = "R$ ${String.format(java.util.Locale("pt", "BR"), "%.2f", r.totalPaid)} • ${r.days} dias", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Credencial entregue: ${r.credentials.username}", fontSize = 11.sp, color = Gray700)
                  }
                  Text(
                    text = if (isExpired) "Expirado" else "Ativo",
                    color = if (isExpired) Color(0xFFDC2626) else GreenIcon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun EditAccountItemsDialog(
  card: SharedAccount,
  repository: AccountRepository,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var draftItems by remember {
    mutableStateOf(
      if (card.itemsIncluded.isNotEmpty()) {
        card.itemsIncluded.map {
          CreatorItemDraft(
            id = it.id,
            name = it.name,
            poseName = it.poseName,
            mediaUrl = it.mediaUrl,
            iconRes = it.iconRes
          )
        }
      } else {
        listOf(
          CreatorItemDraft("item_1", "Chapéu", "Pose Aventura", "https://media.giphy.com/media/3o7TKTDnUxE0g2fSE8/giphy.gif", R.drawable.item_hat),
          CreatorItemDraft("item_2", "Mochila", "Pose Viagem", "https://media.giphy.com/media/l0HlHJGHe3yAMhdQY/giphy.gif", R.drawable.item_backpack)
        )
      }
    )
  }

  var isSaving by remember { mutableStateOf(false) }
  var uploadingIdx by remember { mutableIntStateOf(-1) }

  val mediaUploadLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null && uploadingIdx in draftItems.indices) {
      val targetIdx = uploadingIdx
      scope.launch {
        try {
          val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
          }
          if (bytes != null) {
            val fileName = "pose_${UUID.randomUUID().toString().take(8)}.gif"
            val res = repository.supabase.uploadFile("assets", fileName, bytes, "image/gif")
            if (res.isSuccess) {
              val url = res.getOrThrow()
              draftItems = draftItems.mapIndexed { i, itm ->
                if (i == targetIdx) itm.copy(mediaUrl = url) else itm
              }
              Toast.makeText(context, "Mídia de pose salva no Storage!", Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Erro no upload", Toast.LENGTH_SHORT).show()
        } finally {
          uploadingIdx = -1
        }
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Editar Itens & Poses", fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text(card.title, fontSize = 12.sp, color = Gray500)
        }
        OutlinedButton(
          onClick = {
            draftItems = draftItems + CreatorItemDraft(
              name = "Novo Item #${draftItems.size + 1}",
              poseName = "Pose Especial",
              mediaUrl = "https://media.giphy.com/media/3o7TKMt1VVNkHV2PaE/giphy.gif",
              iconRes = R.drawable.item_map
            )
          },
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("+ Item", fontSize = 11.sp, color = BluePrimary)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Adicione ou edite os itens incluídos e as animações de pose (GIF/vídeo) que aparecem no pedestal.",
          fontSize = 11.sp,
          color = Gray500
        )
        Spacer(modifier = Modifier.height(10.dp))

        draftItems.forEachIndexed { index, item ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Gray50),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Item #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BluePrimary)
                if (draftItems.size > 1) {
                  IconButton(
                    onClick = { draftItems = draftItems.filterIndexed { i, _ -> i != index } },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                OutlinedTextField(
                  value = item.name,
                  onValueChange = { newN -> draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(name = newN) else itm } },
                  label = { Text("Nome", fontSize = 10.sp) },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp),
                  singleLine = true
                )

                OutlinedTextField(
                  value = item.poseName,
                  onValueChange = { newP -> draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(poseName = newP) else itm } },
                  label = { Text("Pose", fontSize = 10.sp) },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp),
                  singleLine = true
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = item.mediaUrl,
                onValueChange = { newU -> draftItems = draftItems.mapIndexed { i, itm -> if (i == index) itm.copy(mediaUrl = newU) else itm } },
                label = { Text("URL do GIF/Vídeo", fontSize = 10.sp) },
                trailingIcon = {
                  IconButton(
                    onClick = {
                      uploadingIdx = index
                      mediaUploadLauncher.launch("image/*")
                    }
                  ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Upload Mídia", tint = BluePrimary)
                  }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Presets
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                  color = Color(0xFFEFF6FF),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.clickable {
                    draftItems = draftItems.mapIndexed { i, itm ->
                      if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/3o7TKTDnUxE0g2fSE8/giphy.gif", poseName = "Pose Aventura") else itm
                    }
                  }
                ) {
                  Text("Aventura", fontSize = 9.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                }

                Surface(
                  color = Color(0xFFEFF6FF),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.clickable {
                    draftItems = draftItems.mapIndexed { i, itm ->
                      if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/l0HlHJGHe3yAMhdQY/giphy.gif", poseName = "Pose Viagem") else itm
                    }
                  }
                ) {
                  Text("Viagem", fontSize = 9.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                }

                Surface(
                  color = Color(0xFFEFF6FF),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.clickable {
                    draftItems = draftItems.mapIndexed { i, itm ->
                      if (i == index) itm.copy(mediaUrl = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif", poseName = "Pose Caminhada") else itm
                    }
                  }
                ) {
                  Text("Caminhada", fontSize = 9.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          isSaving = true
          val updatedItems = draftItems.filter { it.name.isNotBlank() }.mapIndexed { idx, it ->
            IncludedItem(
              id = if (it.id.isNotBlank()) it.id else "${card.id}_item_${idx + 1}",
              name = it.name,
              iconRes = it.iconRes,
              iconUrl = "",
              description = "Item especial com pose animada",
              poseName = it.poseName.ifBlank { "Pose Especial" },
              mediaUrl = it.mediaUrl
            )
          }

          scope.launch {
            repository.updateAccountIncludedItems(card.id, updatedItems)
            isSaving = false
            Toast.makeText(context, "Itens e poses salvos no Supabase!", Toast.LENGTH_SHORT).show()
            onDismiss()
          }
        },
        enabled = !isSaving,
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
      ) {
        if (isSaving) {
          CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
          Text("Salvar no Supabase")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = Gray500)
      }
    }
  )
}

