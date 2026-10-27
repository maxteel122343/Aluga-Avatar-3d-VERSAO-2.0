package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.HeartRed
import com.example.ui.theme.StarGold

@Composable
fun SocialProofRow(
  rating: Double,
  views: String,
  likes: Int,
  isLiked: Boolean,
  rentedCount: Int,
  onLikeToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    HorizontalDivider(color = Gray200, thickness = 1.dp)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Avaliação Média
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Avaliação",
            tint = StarGold,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = String.format("%.1f", rating),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "avaliação média",
          fontSize = 11.sp,
          color = Gray500,
          textAlign = TextAlign.Center
        )
      }

      // Vertical Divider
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight(0.7f)
          .background(Gray200)
      )

      // 2. Visualizações
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "Visualizações",
            tint = Gray900,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = views,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "visualizações",
          fontSize = 11.sp,
          color = Gray500,
          textAlign = TextAlign.Center
        )
      }

      // Vertical Divider
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight(0.7f)
          .background(Gray200)
      )

      // 3. Likes (Clickable)
      Column(
        modifier = Modifier
          .weight(1f)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onLikeToggle
          )
          .testTag("like_button"),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Curtir",
            tint = if (isLiked) HeartRed else Gray900,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$likes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLiked) HeartRed else Gray900
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "likes",
          fontSize = 11.sp,
          color = Gray500,
          textAlign = TextAlign.Center
        )
      }

      // Vertical Divider
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight(0.7f)
          .background(Gray200)
      )

      // 4. Pessoas já alugaram
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.People,
            contentDescription = "Pessoas que já alugaram",
            tint = GreenIcon,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$rentedCount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "pessoas já\nalugaram",
          fontSize = 10.sp,
          color = Gray500,
          textAlign = TextAlign.Center,
          lineHeight = 12.sp
        )
      }
    }

    HorizontalDivider(color = Gray200, thickness = 1.dp)
  }
}
