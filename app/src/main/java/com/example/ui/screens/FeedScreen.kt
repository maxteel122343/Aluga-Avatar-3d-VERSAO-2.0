package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SharedAccount
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
import com.example.ui.theme.StarGold
import com.example.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
  repository: AccountRepository,
  onSelectAccount: (SharedAccount) -> Unit,
  onCreateCardClick: () -> Unit
) {
  val accounts by repository.accounts.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Todas") }

  val categories = listOf("Todas", "Aventura & RPG", "Streaming & Jogos", "Ficção & Ação")

  val filteredAccounts = accounts.filter { acc ->
    val matchesSearch = acc.title.contains(searchQuery, ignoreCase = true) ||
        acc.description.contains(searchQuery, ignoreCase = true)
    val matchesCategory = selectedCategory == "Todas" || acc.category.equals(selectedCategory, ignoreCase = true)
    matchesSearch && matchesCategory
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BluePrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "AlugaConta",
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = Gray900
              )
              Text(
                text = "Aluguel de Contas Compartilhadas",
                fontSize = 11.sp,
                color = Gray500
              )
            }
          }
        },
        actions = {
          IconButton(onClick = { repository.refreshData() }) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Atualizar dados",
              tint = Gray900
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onCreateCardClick,
        containerColor = BluePrimary,
        contentColor = White,
        modifier = Modifier.testTag("create_account_fab")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Criar Card")
      }
    },
    containerColor = White
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Buscar contas compartilhadas...", fontSize = 14.sp, color = Gray500) },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Gray500)
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Gray50,
          unfocusedContainerColor = Gray50,
          focusedBorderColor = BluePrimary,
          unfocusedBorderColor = Gray200
        ),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("search_field")
      )

      // Category filter chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BluePrimary,
              selectedLabelColor = White,
              containerColor = Gray100,
              labelColor = Gray700
            ),
            border = null,
            shape = RoundedCornerShape(20.dp)
          )
        }
      }

      // Accounts List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 80.dp)
      ) {
        items(filteredAccounts, key = { it.id }) { account ->
          AccountFeedCard(
            account = account,
            onCardClick = { onSelectAccount(account) }
          )
        }
      }
    }
  }
}

@Composable
fun AccountFeedCard(
  account: SharedAccount,
  onCardClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("feed_card_${account.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = White),
    border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top status pill & Category
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (account.isOccupied) Color(0xFFFEE2E2) else GreenTagBg,
          shape = RoundedCornerShape(20.dp)
        ) {
          Text(
            text = if (account.isOccupied) "ALUGADA • Limite Atingido" else "Disponível (${account.currentUsers}/${account.maxUsers} usando)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (account.isOccupied) Color(0xFFDC2626) else GreenTagText,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Text(
          text = account.category,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = Gray500
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Middle: Pedestal Avatar preview + Title & Social metrics
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Pedestal Mini Preview
        Box(
          modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Gray50)
            .border(1.dp, Gray200, RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.BottomCenter
        ) {
          // Mini pedestal disk
          Box(
            modifier = Modifier
              .padding(bottom = 6.dp)
              .width(80.dp)
              .height(14.dp)
              .background(
                brush = Brush.verticalGradient(
                  colors = listOf(Color.White, Color(0xFFE5E7EB))
                ),
                shape = CircleShape
              )
              .border(0.5.dp, Color(0xFFD1D5DB), CircleShape)
          )

          if (account.avatarUrl.isNotBlank()) {
            AsyncImage(
              model = account.avatarUrl,
              contentDescription = account.title,
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .size(100.dp)
                .padding(bottom = 8.dp)
            )
          } else {
            Image(
              painter = painterResource(id = if (account.avatarDrawableRes != 0) account.avatarDrawableRes else R.drawable.avatar_character_pedestal),
              contentDescription = account.title,
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .size(100.dp)
                .padding(bottom = 8.dp)
            )
          }

          if (account.model3dUrl.isNotBlank()) {
            Surface(
              color = Color(0xDD2563EB),
              shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
              modifier = Modifier.align(Alignment.TopStart)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ViewInAr,
                  contentDescription = null,
                  tint = White,
                  modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("3D", color = White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info Column
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = account.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = account.description,
            fontSize = 12.sp,
            color = Gray500,
            maxLines = 2,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Social proof mini metrics
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text(text = "${account.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray900)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Gray500, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text(text = account.viewsCount, fontSize = 11.sp, color = Gray500)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text(text = "${account.likesCount}", fontSize = 11.sp, color = Gray500)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Row: Price & Action Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "A partir de", fontSize = 11.sp, color = Gray500)
          Text(
            text = String.format("R$ %.2f / dia", account.basePricePerDay),
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Gray900
          )
        }

        Button(
          onClick = onCardClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (account.isOccupied) Color(0xFFEF4444) else BluePrimary,
            contentColor = White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(42.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (account.isOccupied) "Ver Status" else "Alugar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
