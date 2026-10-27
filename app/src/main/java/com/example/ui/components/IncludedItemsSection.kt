package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IncludedItem
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray50
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900

@Composable
fun IncludedItemsSection(
  items: List<IncludedItem>,
  selectedItem: IncludedItem?,
  onItemClick: (IncludedItem) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    Text(
      text = "Itens incluídos",
      fontSize = 19.sp,
      fontWeight = FontWeight.Bold,
      color = Gray900,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items.forEach { item ->
        val isSelected = selectedItem?.id == item.id
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) BluePrimary else Gray200,
          animationSpec = tween(200),
          label = "item_border"
        )
        val bgColor by animateColorAsState(
          targetValue = if (isSelected) BlueLight else Gray50,
          animationSpec = tween(200),
          label = "item_bg"
        )

        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onItemClick(item) }
            .testTag("included_item_${item.id}"),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(bgColor)
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
              )
              .padding(6.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = item.iconRes),
              contentDescription = item.name,
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = item.name,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BluePrimary else Gray700,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
