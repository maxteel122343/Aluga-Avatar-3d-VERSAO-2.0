package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RentalCalculation
import com.example.data.model.SharedAccount
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.GreenTagBg
import com.example.ui.theme.GreenTagText
import com.example.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixCheckoutBottomSheet(
  account: SharedAccount,
  calculation: RentalCalculation,
  onDismiss: () -> Unit,
  onPaymentSuccess: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()

  var secondsLeft by remember { mutableIntStateOf(900) } // 15 minutes
  var isProcessingPayment by remember { mutableStateOf(false) }
  var hasCopiedPix by remember { mutableStateOf(false) }

  // Countdown timer
  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000L)
      secondsLeft--
    }
  }

  val pixCode = remember(account.id, calculation.formattedPrice) {
    "00020126580014br.gov.bcb.pix0136${account.id.take(8)}-alugaconta-segura5204000053039865405${calculation.finalPrice}5802BR5910ALUGACONTA6009SAOPAULO62070503***6304"
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = White,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF00BFA5))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "PAGAMENTO PIX",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF00897B),
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Finalizar Aluguel",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Gray500)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Summary Card
      Surface(
        color = Gray100,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = account.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Gray900)
            Text(text = calculation.formattedPrice, fontWeight = FontWeight.Black, fontSize = 16.sp, color = BluePrimary)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${calculation.days} dias • ${calculation.people} pessoas",
              fontSize = 13.sp,
              color = Gray500
            )
            Text(
              text = "Desconto de ${calculation.discountPercent}%",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = GreenIcon
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // QR Code Container
      Box(
        modifier = Modifier
          .size(190.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(White)
          .border(2.dp, Gray200, RoundedCornerShape(16.dp))
          .padding(12.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(160.dp)) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val numBlocks = 19
          val blockSize = canvasWidth / numBlocks

          // Render finder pattern at top-left, top-right, bottom-left
          fun drawFinder(startX: Float, startY: Float) {
            // Outer 7x7 square
            drawRoundRect(
              color = Color.Black,
              topLeft = Offset(startX, startY),
              size = Size(blockSize * 7, blockSize * 7),
              cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Inner white 5x5 square
            drawRoundRect(
              color = Color.White,
              topLeft = Offset(startX + blockSize, startY + blockSize),
              size = Size(blockSize * 5, blockSize * 5),
              cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            // Center black 3x3 square
            drawRoundRect(
              color = Color.Black,
              topLeft = Offset(startX + blockSize * 2, startY + blockSize * 2),
              size = Size(blockSize * 3, blockSize * 3),
              cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
          }

          drawFinder(0f, 0f)
          drawFinder(blockSize * (numBlocks - 7), 0f)
          drawFinder(0f, blockSize * (numBlocks - 7))

          // Pseudo random reproducible matrix pattern
          for (r in 0 until numBlocks) {
            for (c in 0 until numBlocks) {
              val inFinder1 = r < 7 && c < 7
              val inFinder2 = r < 7 && c >= numBlocks - 7
              val inFinder3 = r >= numBlocks - 7 && c < 7
              if (!inFinder1 && !inFinder2 && !inFinder3) {
                val seed = (r * 31 + c * 17 + account.id.hashCode())
                if ((seed % 3 == 0) || (seed % 7 == 0)) {
                  drawRect(
                    color = Color.Black,
                    topLeft = Offset(c * blockSize, r * blockSize),
                    size = Size(blockSize * 0.92f, blockSize * 0.92f)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Timer row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Timer,
          contentDescription = null,
          tint = Gray500,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        val minutes = secondsLeft / 60
        val secs = secondsLeft % 60
        Text(
          text = "QR Code expira em ${String.format("%02d:%02d", minutes, secs)}",
          fontSize = 12.sp,
          color = Gray500
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Pix Copia e Cola box
      Surface(
        color = Gray100,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            clipboardManager.setText(AnnotatedString(pixCode))
            hasCopiedPix = true
            Toast.makeText(context, "Código PIX copiado!", Toast.LENGTH_SHORT).show()
          }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = pixCode,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Gray700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (hasCopiedPix) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
              contentDescription = "Copiar",
              tint = if (hasCopiedPix) GreenIcon else BluePrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (hasCopiedPix) "Copiado!" else "Copiar",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (hasCopiedPix) GreenIcon else BluePrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Instant Simulation Payment Button
      Button(
        onClick = {
          coroutineScope.launch {
            isProcessingPayment = true
            delay(1200L) // Simulate bank webhook verification
            isProcessingPayment = false
            onPaymentSuccess()
          }
        },
        enabled = !isProcessingPayment,
        colors = ButtonDefaults.buttonColors(
          containerColor = GreenIcon,
          contentColor = White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("simulate_pix_payment_button")
      ) {
        if (isProcessingPayment) {
          CircularProgressIndicator(
            color = White,
            strokeWidth = 2.dp,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Confirmando PIX no Banco...",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        } else {
          Icon(
            imageVector = Icons.Default.FlashOn,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Simular Pagamento PIX (Liberação Imediata)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "⚡ Ao confirmar o pagamento, as credenciais da conta compartilhada são liberadas imediatamente na sua tela.",
        fontSize = 11.sp,
        color = Gray500,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        lineHeight = 16.sp
      )
    }
  }
}
