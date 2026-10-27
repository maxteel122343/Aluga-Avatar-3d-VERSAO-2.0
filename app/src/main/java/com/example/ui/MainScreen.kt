package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.NotificationType
import com.example.data.model.SharedAccount
import com.example.data.repository.AccountRepository
import com.example.ui.screens.AccountDetailScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreatorPanelScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.MyRentalsScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.GreenIcon
import com.example.ui.theme.White

enum class ScreenTab {
  FEED,
  DETAIL,
  MY_RENTALS,
  CREATOR_PANEL
}

@Composable
fun MainScreen(
  repository: AccountRepository
) {
  val accounts by repository.accounts.collectAsState()
  val activeRentals by repository.activeRentals.collectAsState()
  val session by repository.currentSession.collectAsState()
  val notifications by repository.inAppNotifications.collectAsState()

  var currentTab by remember { mutableStateOf(ScreenTab.DETAIL) }
  var selectedAccount by remember { mutableStateOf<SharedAccount?>(null) }

  var showAuthScreen by remember { mutableStateOf(false) }
  var authReasonMessage by remember { mutableStateOf<String?>(null) }
  var showUserMenu by remember { mutableStateOf(false) }

  // Initial account matches the screenshot
  val currentAccount = selectedAccount ?: accounts.firstOrNull() ?: return

  // Back handling: If on secondary tab, return to feed or detail
  BackHandler(enabled = currentTab != ScreenTab.FEED) {
    currentTab = ScreenTab.FEED
  }

  fun requestAuth(reason: String) {
    authReasonMessage = reason
    showAuthScreen = true
  }

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = White,
        tonalElevation = 6.dp
      ) {
        // Tab 1: Detalhe (O card exato da imagem)
        NavigationBarItem(
          selected = currentTab == ScreenTab.DETAIL,
          onClick = { currentTab = ScreenTab.DETAIL },
          icon = {
            Icon(
              imageVector = Icons.Default.ViewInAr,
              contentDescription = "Card Atual",
              modifier = Modifier.size(22.dp)
            )
          },
          label = { Text("Card 3D", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BluePrimary,
            selectedTextColor = BluePrimary,
            unselectedIconColor = Gray500,
            unselectedTextColor = Gray500,
            indicatorColor = White
          ),
          modifier = Modifier.testTag("nav_detail")
        )

        // Tab 2: Feed
        NavigationBarItem(
          selected = currentTab == ScreenTab.FEED,
          onClick = { currentTab = ScreenTab.FEED },
          icon = {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = "Feed",
              modifier = Modifier.size(22.dp)
            )
          },
          label = { Text("Explorar", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BluePrimary,
            selectedTextColor = BluePrimary,
            unselectedIconColor = Gray500,
            unselectedTextColor = Gray500,
            indicatorColor = White
          ),
          modifier = Modifier.testTag("nav_feed")
        )

        // Tab 3: Meus Aluguéis
        NavigationBarItem(
          selected = currentTab == ScreenTab.MY_RENTALS,
          onClick = { currentTab = ScreenTab.MY_RENTALS },
          icon = {
            BadgedBox(
              badge = {
                if (activeRentals.isNotEmpty()) {
                  Badge { Text("${activeRentals.size}") }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = "Meus Aluguéis",
                modifier = Modifier.size(22.dp)
              )
            }
          },
          label = { Text("Aluguéis", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BluePrimary,
            selectedTextColor = BluePrimary,
            unselectedIconColor = Gray500,
            unselectedTextColor = Gray500,
            indicatorColor = White
          ),
          modifier = Modifier.testTag("nav_rentals")
        )

        // Tab 4: Criar Card
        NavigationBarItem(
          selected = currentTab == ScreenTab.CREATOR_PANEL,
          onClick = {
            if (session == null) {
              requestAuth("Faça login para acessar o painel do criador e publicar novos cards.")
            } else {
              currentTab = ScreenTab.CREATOR_PANEL
            }
          },
          icon = {
            Icon(
              imageVector = Icons.Default.AddCircle,
              contentDescription = "Criar Card",
              modifier = Modifier.size(22.dp)
            )
          },
          label = { Text("Criar Card", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BluePrimary,
            selectedTextColor = BluePrimary,
            unselectedIconColor = Gray500,
            unselectedTextColor = Gray500,
            indicatorColor = White
          ),
          modifier = Modifier.testTag("nav_creator")
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Global Bar: User Auth Status & Quick Login / Profile
        TopUserHeader(
          session = session,
          onLoginClick = { requestAuth("Entre na sua conta para sincronizar aluguéis no Supabase.") },
          onLogoutClick = { repository.logout() }
        )

        // Screen Content
        Box(modifier = Modifier.weight(1f)) {
          when (currentTab) {
            ScreenTab.DETAIL -> {
              AccountDetailScreen(
                account = currentAccount,
                repository = repository,
                onRequestAuth = { reason -> requestAuth(reason) },
                onNavigateBack = { currentTab = ScreenTab.FEED },
                onGoToRentals = { currentTab = ScreenTab.MY_RENTALS }
              )
            }

            ScreenTab.FEED -> {
              FeedScreen(
                repository = repository,
                onSelectAccount = { account ->
                  selectedAccount = account
                  currentTab = ScreenTab.DETAIL
                },
                onCreateCardClick = {
                  if (session == null) {
                    requestAuth("Faça login para criar seu card no Supabase.")
                  } else {
                    currentTab = ScreenTab.CREATOR_PANEL
                  }
                }
              )
            }

            ScreenTab.MY_RENTALS -> {
              MyRentalsScreen(
                repository = repository,
                onExploreClick = { currentTab = ScreenTab.FEED }
              )
            }

            ScreenTab.CREATOR_PANEL -> {
              CreatorPanelScreen(
                repository = repository,
                onRequestAuth = { reason -> requestAuth(reason) },
                onAccountCreated = { newAccount ->
                  selectedAccount = newAccount
                  currentTab = ScreenTab.DETAIL
                }
              )
            }
          }
        }
      }

      // In-App Notification Toast Banner
      val activeNotification = notifications.firstOrNull()
      AnimatedVisibility(
        visible = activeNotification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 48.dp, start = 16.dp, end = 16.dp)
      ) {
        if (activeNotification != null) {
          InAppNotificationBanner(
            notification = activeNotification,
            onDismiss = { repository.dismissNotification(activeNotification.id) }
          )
        }
      }
    }
  }

  // Full Screen Auth Dialog
  if (showAuthScreen) {
    Dialog(
      onDismissRequest = {
        showAuthScreen = false
        authReasonMessage = null
      },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      AuthScreen(
        repository = repository,
        reasonMessage = authReasonMessage,
        onDismiss = {
          showAuthScreen = false
          authReasonMessage = null
        },
        onAuthSuccess = {
          showAuthScreen = false
          authReasonMessage = null
        }
      )
    }
  }
}

@Composable
fun TopUserHeader(
  session: com.example.data.model.UserSession?,
  onLoginClick: () -> Unit,
  onLogoutClick: () -> Unit
) {
  Surface(
    color = Color(0xFFF9FAFB),
    border = androidx.compose.foundation.BorderStroke(0.5.dp, Gray200),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(if (session != null) GreenIcon else Color(0xFF9CA3AF), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (session != null) "Supabase Online" else "Modo Convidado",
          fontSize = 11.sp,
          color = Gray500,
          fontWeight = FontWeight.Medium
        )
      }

      if (session != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = session.email.substringBefore("@"),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BluePrimary
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = onLogoutClick,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair", tint = Gray500, modifier = Modifier.size(16.dp))
          }
        }
      } else {
        Surface(
          color = BluePrimary,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.clickable { onLoginClick() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Entrar / Cadastrar", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun InAppNotificationBanner(
  notification: com.example.data.model.InAppNotification,
  onDismiss: () -> Unit
) {
  val (bgColor, borderColor, iconColor) = when (notification.type) {
    NotificationType.SUCCESS -> Triple(Color(0xFFECFDF5), Color(0xFFA7F3D0), GreenIcon)
    NotificationType.WARNING -> Triple(Color(0xFFFEF2F2), Color(0xFFFECACA), Color(0xFFDC2626))
    NotificationType.INFO -> Triple(Color(0xFFEFF6FF), Color(0xFFBFDBFE), BluePrimary)
  }

  val icon = when (notification.type) {
    NotificationType.SUCCESS -> Icons.Default.CheckCircle
    NotificationType.WARNING -> Icons.Default.Warning
    NotificationType.INFO -> Icons.Default.Info
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor),
    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = notification.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Gray900
        )
        Text(
          text = notification.message,
          fontSize = 11.sp,
          color = Gray700,
          lineHeight = 15.sp
        )
      }
      IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Gray500, modifier = Modifier.size(16.dp))
      }
    }
  }
}
