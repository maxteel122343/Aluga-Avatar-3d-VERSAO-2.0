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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
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

  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Aventura & RPG") }
  var description by remember { mutableStateOf("") }
  var basePriceText by remember { mutableStateOf("29.90") }
  var maxPeople by remember { mutableIntStateOf(4) }
  var selectedAvatarRes by remember { mutableIntStateOf(R.drawable.avatar_character_pedestal) }

  // Custom Avatar and 3D Model URLs
  var customAvatarUrl by remember { mutableStateOf("") }
  var model3dUrl by remember {
    mutableStateOf("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Fox/glTF-Binary/Fox.glb")
  }

  var isUploadingAvatar by remember { mutableStateOf(false) }
  var isUploadingModel by remember { mutableStateOf(false) }
  var isPublishing by remember { mutableStateOf(false) }

  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  // Media pickers using zero-permission Android Photo Picker for image
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
              Toast.makeText(context, "Avatar enviado para o Supabase Storage!", Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(context, "Erro no upload: " + uploadRes.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Falha ao ler imagem", Toast.LENGTH_SHORT).show()
        } finally {
          isUploadingAvatar = false
        }
      }
    }
  }

  // File picker for 3D model (.glb / .gltf)
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
              Toast.makeText(context, "Modelo 3D enviado para o Supabase Storage!", Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(context, "Erro no upload 3D: " + uploadRes.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Falha ao ler arquivo 3D", Toast.LENGTH_SHORT).show()
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
              text = "Publicar novo card de conta compartilhada",
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
    // Check if user is logged in
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
          text = "Para publicar suas contas e receber aluguéis com persistência no Supabase, faça login ou cadastre-se.",
          fontSize = 14.sp,
          color = Gray500,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
          onClick = { onRequestAuth("Faça login para acessar o painel de criação.") },
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
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState())
        .padding(bottom = 80.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Logged user pill
      Surface(
        color = Color(0xFFF3F4F6),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = GreenIcon, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Conectado como: ${session?.email}",
            fontSize = 12.sp,
            color = Gray700,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Section 1: 3D Avatar Selection on Pedestal
      Text(
        text = "1. Escolha o Modelo do Avatar no Pedestal",
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

      // Optional Avatar Upload to Supabase Storage
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
            Text(
              text = "Ou envie imagem personalizada",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              color = Gray900
            )
            Text(
              text = if (customAvatarUrl.isNotBlank()) "Imagem enviada para Supabase!" else "JPG, PNG gravados no Supabase Storage",
              fontSize = 11.sp,
              color = if (customAvatarUrl.isNotBlank()) GreenIcon else Gray500
            )
          }
          Button(
            onClick = {
              imagePickerLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                  ActivityResultContracts.PickVisualMedia.ImageOnly
                )
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
              Text(if (customAvatarUrl.isNotBlank()) "Alterar" else "Upload", fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 2: 3D Model (.glb / .gltf)
      Text(
        text = "2. Arquivo 3D (.glb / .gltf) para o Pedestal",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Gray900
      )
      Text(
        text = "O modelo 3D é renderizado ao vivo com rotação interativa de 360° no pedestal.",
        fontSize = 12.sp,
        color = Gray500
      )
      Spacer(modifier = Modifier.height(8.dp))

      // Preset 3D Model Pills
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
                modifier = Modifier.size(18.dp)
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

      // Custom 3D Model URL Input or File Upload
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

      Spacer(modifier = Modifier.height(18.dp))

      // Section 3: Account Details
      Text(
        text = "3. Dados da Conta Compartilhada",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Gray900
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Nome da Conta / Passe") },
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
        label = { Text("Descrição dos benefícios") },
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
          label = { Text("Preço Base / Dia (R$)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = maxPeople.toString(),
          onValueChange = { it.toIntOrNull()?.let { v -> maxPeople = v } },
          label = { Text("Limite Pessoas") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 4: Credenciais de Entrega Automática
      Text(
        text = "4. Credenciais de Acesso (Entrega Automática)",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Gray900
      )
      Text(
        text = "O sistema liberará esses dados ao cliente apenas após a confirmação do pagamento PIX.",
        fontSize = 12.sp,
        color = Gray500
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Usuário / Login") },
        placeholder = { Text("usuario_vip_2026") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Senha da Conta") },
        placeholder = { Text("SenhaForte@998") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Submit Button
      Button(
        onClick = {
          if (title.isBlank()) {
            Toast.makeText(context, "Por favor insira o nome da conta", Toast.LENGTH_SHORT).show()
            return@Button
          }

          val price = basePriceText.toDoubleOrNull() ?: 29.90
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
              pass = password
            )
            isPublishing = false
            if (result.isSuccess) {
              val created = result.getOrThrow()
              Toast.makeText(context, "Card salvo com sucesso no Supabase!", Toast.LENGTH_LONG).show()
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
          Text(text = "Publicar Card no Supabase", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
