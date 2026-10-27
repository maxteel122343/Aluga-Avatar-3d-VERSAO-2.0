package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ActiveRental
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.GreenTagBg
import com.example.ui.theme.White

@Composable
fun CredentialsDeliveryDialog(
  rental: ActiveRental,
  onDismiss: () -> Unit,
  onGoToRentals: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var showPassword by remember { mutableStateOf(true) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Success Header Badge
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(GreenTagBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = GreenIcon,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Acesso Liberado com Sucesso!",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )

        Text(
          text = "Guarde suas credenciais para acessar a conta compartilhada:",
          fontSize = 13.sp,
          color = Gray500,
          modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Credentials Box
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Gray100,
          border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Username Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "LOGIN / USUÁRIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = rental.credentials.username,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900,
                  fontFamily = FontFamily.Monospace
                )
              }
              IconButton(onClick = {
                clipboardManager.setText(AnnotatedString(rental.credentials.username))
                Toast.makeText(context, "Login copiado!", Toast.LENGTH_SHORT).show()
              }) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar login", tint = BluePrimary, modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "SENHA DE ACESSO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = if (showPassword) rental.credentials.password else "••••••••••••",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900,
                  fontFamily = FontFamily.Monospace
                )
              }
              Row {
                IconButton(onClick = { showPassword = !showPassword }) {
                  Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Mostrar senha",
                    tint = Gray500,
                    modifier = Modifier.size(18.dp)
                  )
                }
                IconButton(onClick = {
                  clipboardManager.setText(AnnotatedString(rental.credentials.password))
                  Toast.makeText(context, "Senha copiada!", Toast.LENGTH_SHORT).show()
                }) {
                  Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar senha", tint = BluePrimary, modifier = Modifier.size(18.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Security PIN and Profile
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = "PIN DE SEGURANÇA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = rental.credentials.pin,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900,
                  fontFamily = FontFamily.Monospace
                )
              }
              Column {
                Text(text = "SEU PERFIL ATRIBUÍDO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = rental.credentials.profileAssigned,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenIcon
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prominent Ban Warning Banner
        Surface(
          color = Color(0xFFFEF2F2),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Aviso Importante: Compartilhar a senha com terceiros resulta em banimento permanente e cancelamento imediato do acesso.",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFDC2626),
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rental duration info
        Surface(
          color = Color(0xFFEFF6FF),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Período contratado: ${rental.days} dias para ${rental.peopleCount} pessoas. O cronômetro de acesso está ativo.",
              fontSize = 12.sp,
              color = BluePrimary,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Copy All Button
        Button(
          onClick = {
            val allData = """
              --- CONTA ALUGADA: ${rental.accountTitle} ---
              Login: ${rental.credentials.username}
              Senha: ${rental.credentials.password}
              PIN: ${rental.credentials.pin}
              Perfil: ${rental.credentials.profileAssigned}
              Servidor: ${rental.credentials.server}
              Duração: ${rental.days} dias
              Regras: ${rental.credentials.rules}
            """.trimIndent()
            clipboardManager.setText(AnnotatedString(allData))
            Toast.makeText(context, "Todos os dados copiados para a área de transferência!", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("copy_all_credentials_button")
        ) {
          Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Copiar Todas as Credenciais", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Go to rentals button
        OutlinedButton(
          onClick = onGoToRentals,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("go_to_my_rentals_button")
        ) {
          Text(text = "Ir para Meus Aluguéis Ativos", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
        }
      }
    }
  }
}
