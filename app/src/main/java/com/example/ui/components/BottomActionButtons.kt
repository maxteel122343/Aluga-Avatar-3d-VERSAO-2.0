package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray600
import com.example.ui.theme.Gray900
import com.example.ui.theme.White

@Composable
fun BottomActionButtons(
  isOccupied: Boolean,
  currentUsers: Int,
  onRentClick: () -> Unit,
  onWarrantyClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Primary Button: "Alugar agora" (or "ALUGADA por X pessoas")
    Button(
      onClick = onRentClick,
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isOccupied) Color(0xFFEF4444) else BluePrimary,
        contentColor = White
      ),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .weight(1.3f)
        .height(52.dp)
        .testTag("primary_rent_button")
    ) {
      Icon(
        imageVector = if (isOccupied) Icons.Default.HourglassTop else Icons.Default.ShoppingBag,
        contentDescription = null,
        tint = White,
        modifier = Modifier.size(19.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (isOccupied) "ALUGADA por $currentUsers pessoas" else "Alugar agora",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    // Secondary Button: "Tenho interesse • Garantia"
    OutlinedButton(
      onClick = onWarrantyClick,
      colors = ButtonDefaults.outlinedButtonColors(
        containerColor = White,
        contentColor = Gray900
      ),
      border = BorderStroke(1.dp, Gray300),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .weight(1.1f)
        .height(52.dp)
        .testTag("warranty_info_button")
    ) {
      Text(
        text = "Tenho interesse • Garantia",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray900,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        imageVector = Icons.Rounded.VerifiedUser,
        contentDescription = "Garantia",
        tint = Gray600,
        modifier = Modifier.size(17.dp)
      )
    }
  }
}
