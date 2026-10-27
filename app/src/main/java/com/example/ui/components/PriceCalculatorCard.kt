package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.GreenTagBg
import com.example.ui.theme.GreenTagText
import com.example.ui.theme.White

@Composable
fun PriceCalculatorCard(
  days: Int,
  people: Int,
  formattedPrice: String,
  discountPercent: Int,
  onDaysChange: (Int) -> Unit,
  onPeopleChange: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    // Title: Calcule o valor
    Text(
      text = "Calcule o valor",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Gray900,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    // Dual Counter Container (Dias & Pessoas)
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = White,
      border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(IntrinsicSize.Min)
          .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Section: Dias
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = "Dias",
              tint = Gray700,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Dias",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Gray700
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Minus Button
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Gray200, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              IconButton(
                onClick = { if (days > 1) onDaysChange(days - 1) },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("decrease_days_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Diminuir dias",
                  tint = if (days > 1) Gray900 else Gray400,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Text(
              text = "$days",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Gray900,
              modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Plus Button
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Gray200, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              IconButton(
                onClick = { onDaysChange(days + 1) },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("increase_days_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Aumentar dias",
                  tint = Gray900,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }

        // Vertical divider between Dias and Pessoas
        Box(
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(1.dp)
            .fillMaxHeight(0.7f)
            .background(Gray200)
        )

        // Section: Pessoas
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.People,
              contentDescription = "Pessoas",
              tint = Gray700,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Pessoas",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Gray700
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Minus Button
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Gray200, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              IconButton(
                onClick = { if (people > 1) onPeopleChange(people - 1) },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("decrease_people_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Diminuir pessoas",
                  tint = if (people > 1) Gray900 else Gray400,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Text(
              text = "$people",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Gray900,
              modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Plus Button
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Gray200, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              IconButton(
                onClick = { if (people < 10) onPeopleChange(people + 1) },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("increase_people_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Aumentar pessoas",
                  tint = Gray900,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Preço ao vivo & Tag de Desconto aplicado
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Preço ao vivo",
          fontSize = 13.sp,
          color = Gray500,
          fontWeight = FontWeight.Normal
        )
        Text(
          text = formattedPrice,
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          color = Gray900
        )
      }

      // Green Discount Pill Badge (EXACTLY AS IN SCREENSHOT)
      Surface(
        color = GreenTagBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("discount_applied_badge")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocalOffer,
            contentDescription = "Desconto",
            tint = GreenTagText,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Desconto aplicado",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GreenTagText
          )
        }
      }
    }
  }
}
