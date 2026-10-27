package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.White

@Composable
fun TopSocialProofPill(
  rentalsText: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(30.dp),
    color = White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
    shadowElevation = 2.dp,
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("top_social_proof_pill")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      // Group of 4 avatars
      Image(
        painter = painterResource(id = R.drawable.avatars_group),
        contentDescription = "Usuários que alugaram",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .height(30.dp)
          .width(96.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Green plus circle icon
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(GreenIcon),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = White,
          modifier = Modifier.size(13.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Rental text
      Text(
        text = rentalsText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray700
      )
    }
  }
}
