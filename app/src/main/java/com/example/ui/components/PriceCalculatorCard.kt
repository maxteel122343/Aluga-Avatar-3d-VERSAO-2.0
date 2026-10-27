package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
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
  currentUsers: Int = 2,
  maxUsers: Int = 5,
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

    // Dual Counter Container (Dias & Pessoas alugando)
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = White,
      border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(IntrinsicSize.Min)
          .padding(vertical = 10.dp, horizontal = 12.dp),
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
            Column {
              Text(
                text = "Dias",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Gray700
              )
              Text(
                text = "$days ${if (days > 1) "dias" else "dia"}",
                fontSize = 11.sp,
                color = Gray500
              )
            }
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
            .padding(horizontal = 10.dp)
            .width(1.dp)
            .fillMaxHeight(0.75f)
            .background(Gray200)
        )

        // Section: "2/5 pessoas alugando" (Clara percepção de ocupação compartilhada)
        Row(
          modifier = Modifier
            .weight(1.25f)
            .testTag("people_occupancy_section"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.People,
              contentDescription = "Pessoas alugando",
              tint = BluePrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "${currentUsers}/${maxUsers} pessoas",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
              )
              Text(
                text = "alugando agora",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = BluePrimary
              )
            }
          }

          // Stepper for slots / people count with progressive discount
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
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
                  contentDescription = "Diminuir vagas",
                  tint = if (people > 1) Gray900 else Gray400,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Text(
              text = "$people",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Gray900,
              modifier = Modifier.padding(horizontal = 4.dp)
            )

            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Gray200, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              IconButton(
                onClick = { if (people < maxUsers) onPeopleChange(people + 1) },
                modifier = Modifier
                  .size(28.dp)
                  .testTag("increase_people_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Aumentar vagas",
                  tint = if (people < maxUsers) Gray900 else Gray400,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Percepção de Preço Compartilhado: "Quanto mais pessoas alugando, maior o desconto"
    Surface(
      color = Color(0xFFF0FDF4),
      shape = RoundedCornerShape(10.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.TrendingDown,
          contentDescription = null,
          tint = Color(0xFF16A34A),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Preço compartilhado: quanto mais pessoas no grupo, maior o desconto (-$discountPercent% aplicado)",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF15803D),
          lineHeight = 14.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Preço ao vivo & Tag de Desconto aplicado (EXACTLY AS IN SCREENSHOT)
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

      // Green Discount Pill Badge
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
