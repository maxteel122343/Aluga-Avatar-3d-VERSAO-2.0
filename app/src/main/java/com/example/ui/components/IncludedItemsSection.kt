package com.example.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.data.model.IncludedItem
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray50
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncludedItemsSection(
  items: List<IncludedItem>,
  selectedItem: IncludedItem?,
  onItemClick: (IncludedItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAllItemsSheet by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Header Row with "Itens incluídos" and "Ver todos" button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Itens incluídos",
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        if (items.isNotEmpty()) {
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = Gray100,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = "${items.size}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Gray700,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
          }
        }
      }

      Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
        modifier = Modifier
          .clickable { showAllItemsSheet = true }
          .testTag("view_all_items_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "Ver todos",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BluePrimary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Ver todos os itens",
            tint = BluePrimary,
            modifier = Modifier.size(13.dp)
          )
        }
      }
    }

    // Horizontally scrollable row of items (LazyRow)
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
    ) {
      items(items, key = { it.id }) { item ->
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
            .width(76.dp)
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
            if (item.iconUrl.isNotBlank()) {
              AsyncImage(
                model = item.iconUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Image(
                painter = painterResource(id = if (item.iconRes != 0) item.iconRes else com.example.R.drawable.item_hat),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )
            }

            // Small badge if item has animated pose GIF
            if (item.mediaUrl.isNotBlank()) {
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(16.dp)
                  .background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayCircle,
                  contentDescription = "Pose animada disponível",
                  tint = White,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = item.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BluePrimary else Gray700,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }

  // BottomSheet modal showing all items in larger cards with full preview
  if (showAllItemsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showAllItemsSheet = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = White
    ) {
      AllItemsSheetContent(
        items = items,
        selectedItem = selectedItem,
        onSelectForPedestal = { item ->
          onItemClick(item)
          showAllItemsSheet = false
        },
        onDismiss = { showAllItemsSheet = false }
      )
    }
  }
}

@Composable
private fun AllItemsSheetContent(
  items: List<IncludedItem>,
  selectedItem: IncludedItem?,
  onSelectForPedestal: (IncludedItem) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val gifImageLoader = remember(context) {
    ImageLoader.Builder(context)
      .components {
        if (Build.VERSION.SDK_INT >= 28) {
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
      }
      .build()
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp)
      .padding(bottom = 36.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Todos os Itens & Poses",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        Text(
          text = "Selecione qualquer item para testar a pose ao vivo no pedestal",
          fontSize = 12.sp,
          color = Gray500
        )
      }

      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Gray500)
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(items, key = { it.id }) { item ->
        val isEquipped = selectedItem?.id == item.id

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = if (isEquipped) Color(0xFFF0FDF4) else White),
          border = androidx.compose.foundation.BorderStroke(
            width = if (isEquipped) 2.dp else 1.dp,
            color = if (isEquipped) Color(0xFF22C55E) else Gray200
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left: GIF Preview or Item Icon
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Gray50)
                .border(1.dp, Gray200, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              if (item.mediaUrl.isNotBlank()) {
                AsyncImage(
                  model = ImageRequest.Builder(context)
                    .data(item.mediaUrl)
                    .crossfade(true)
                    .build(),
                  imageLoader = gifImageLoader,
                  contentDescription = "Pose preview ${item.name}",
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.fillMaxSize().padding(4.dp)
                )
              } else if (item.iconUrl.isNotBlank()) {
                AsyncImage(
                  model = item.iconUrl,
                  contentDescription = item.name,
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.fillMaxSize().padding(8.dp)
                )
              } else {
                Image(
                  painter = painterResource(id = if (item.iconRes != 0) item.iconRes else com.example.R.drawable.item_hat),
                  contentDescription = item.name,
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.fillMaxSize().padding(8.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details and Action Button
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = item.name,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gray900
                )

                if (isEquipped) {
                  Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = "Equipado",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF16A34A),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              if (item.poseName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Pose: ${item.poseName}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BluePrimary
                )
              }

              if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.description,
                  fontSize = 11.sp,
                  color = Gray500,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Button(
                onClick = { onSelectForPedestal(item) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isEquipped) Color(0xFF16A34A) else BluePrimary,
                  contentColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = White,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isEquipped) "Ver no Pedestal" else "Equipar Pose no Pedestal",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
