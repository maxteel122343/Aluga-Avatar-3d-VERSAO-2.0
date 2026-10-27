package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveRental
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRentalsScreen(
  repository: AccountRepository,
  onExploreClick: () -> Unit
) {
  val rentals by repository.activeRentals.collectAsState()
  var rentalToRenew by remember { mutableStateOf<ActiveRental?>(null) }

  // Tick every second to update countdowns
  var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000L)
      currentTime = System.currentTimeMillis()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Meus Aluguéis",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Gray900
            )
            Text(
              text = "Acesso e credenciais das suas contas",
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
    if (rentals.isEmpty()) {
      // Empty state
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
            .clip(CircleShape)
            .background(Gray100),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.HourglassBottom,
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(40.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Nenhum aluguel ativo no momento",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Escolha uma conta compartilhada no feed, realize o pagamento via PIX e libere seu acesso instantaneamente.",
          fontSize = 13.sp,
          color = Gray500,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = onExploreClick,
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = White),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp)
        ) {
          Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Explorar Contas no Feed", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 80.dp)
      ) {
        items(rentals, key = { it.id }) { rental ->
          ActiveRentalCard(
            rental = rental,
            currentTime = currentTime,
            onRenewClick = { rentalToRenew = rental }
          )
        }
      }
    }
  }

  // Renewal Dialog
  rentalToRenew?.let { rental ->
    AlertDialog(
      onDismissRequest = { rentalToRenew = null },
      title = { Text(text = "Renovar Aluguel", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(text = "Deseja estender o acesso à conta '${rental.accountTitle}'?")
          Spacer(modifier = Modifier.height(12.dp))
          Text(text = "Selecione o período de renovação com 20% de desconto adicional:")
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repository.renewRental(rental.id, 3)
            rentalToRenew = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Text("+3 Dias (R$ 39,90)")
        }
      },
      dismissButton = {
        TextButton(onClick = { rentalToRenew = null }) {
          Text("Cancelar", color = Gray500)
        }
      }
    )
  }
}

@Composable
fun ActiveRentalCard(
  rental: ActiveRental,
  currentTime: Long,
  onRenewClick: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var showPassword by remember { mutableStateOf(false) }

  val remainingMillis = rental.remainingMillis()
  val isExpired = remainingMillis <= 0L

  val totalSeconds = remainingMillis / 1000L
  val daysLeft = totalSeconds / (24 * 3600)
  val hoursLeft = (totalSeconds % (24 * 3600)) / 3600
  val minutesLeft = (totalSeconds % 3600) / 60
  val secondsLeft = totalSeconds % 60

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("rental_card_${rental.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = White),
    border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Avatar, Title, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Gray100),
          contentAlignment = Alignment.Center
        ) {
          if (rental.avatarUrl.isNotBlank()) {
            AsyncImage(
              model = rental.avatarUrl,
              contentDescription = rental.accountTitle,
              contentScale = ContentScale.Fit,
              modifier = Modifier.size(50.dp)
            )
          } else {
            Image(
              painter = painterResource(id = if (rental.avatarDrawableRes != 0) rental.avatarDrawableRes else R.drawable.avatar_character_pedestal),
              contentDescription = rental.accountTitle,
              contentScale = ContentScale.Fit,
              modifier = Modifier.size(50.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = rental.accountTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${rental.days} dias contratados • ${rental.peopleCount} pessoas",
            fontSize = 12.sp,
            color = Gray500
          )
        }

        Surface(
          color = if (isExpired) Color(0xFFFEE2E2) else GreenTagBg,
          shape = RoundedCornerShape(20.dp)
        ) {
          Text(
            text = if (isExpired) "Expirado" else "Ativo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isExpired) Color(0xFFDC2626) else GreenTagText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Live Countdown Box
      Surface(
        color = if (isExpired) Color(0xFFFEF2F2) else Color(0xFFEFF6FF),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpired) Color(0xFFFECACA) else Color(0xFFBFDBFE)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = if (isExpired) Color(0xFFDC2626) else BluePrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isExpired) "Tempo esgotado" else "Tempo Restante:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Gray700
            )
          }

          Text(
            text = if (isExpired) "Finalizado" else "${daysLeft}d ${hoursLeft}h ${minutesLeft}m ${secondsLeft}s",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isExpired) Color(0xFFDC2626) else BluePrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Credentials Details or Locked State if Expired
      if (isExpired) {
        Surface(
          color = Color(0xFFFEF2F2),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Acesso Expirado e Bloqueado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFDC2626))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "O prazo deste aluguel encerrou. As credenciais antigas foram desativadas e a vaga foi liberada. Renove seu aluguel para obter novas credenciais atualizadas.",
              fontSize = 11.sp,
              color = Gray700,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              lineHeight = 15.sp
            )
          }
        }
      } else {
        // Ban Warning Banner
        Surface(
          color = Color(0xFFFEF2F2),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
        ) {
          Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Aviso: Compartilhar a senha com terceiros resulta em banimento permanente.",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFDC2626)
            )
          }
        }

        Surface(
          color = Gray50,
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Login
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "USUÁRIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = rental.credentials.username,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900,
                  fontFamily = FontFamily.Monospace
                )
              }
              IconButton(
                onClick = {
                  clipboardManager.setText(AnnotatedString(rental.credentials.username))
                  Toast.makeText(context, "Usuário copiado!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = BluePrimary, modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Password
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "SENHA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray500)
                Text(
                  text = if (showPassword) rental.credentials.password else "••••••••••••",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900,
                  fontFamily = FontFamily.Monospace
                )
              }
              Row {
                IconButton(onClick = { showPassword = !showPassword }, modifier = Modifier.size(32.dp)) {
                  Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(16.dp)
                  )
                }
                IconButton(
                  onClick = {
                    clipboardManager.setText(AnnotatedString(rental.credentials.password))
                    Toast.makeText(context, "Senha copiada!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = BluePrimary, modifier = Modifier.size(16.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PIN & Profile
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "PIN: ${rental.credentials.pin}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Gray700
              )
              Text(
                text = rental.credentials.profileAssigned,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GreenIcon
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action row: Renovar
      Button(
        onClick = onRenewClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = BluePrimary,
          contentColor = White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
      ) {
        Icon(imageVector = Icons.Default.Autorenew, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Renovar Acesso (+ Dias)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
