package com.example.data.model

data class UserSession(
  val userId: String,
  val email: String,
  val accessToken: String,
  val refreshToken: String = "",
  val username: String = ""
)

enum class NotificationType {
  INFO,
  SUCCESS,
  WARNING
}

data class InAppNotification(
  val id: String,
  val title: String,
  val message: String,
  val type: NotificationType = NotificationType.INFO,
  val timestamp: Long = System.currentTimeMillis()
)

data class IncludedItem(
  val id: String,
  val name: String,
  val iconRes: Int = 0,
  val iconUrl: String = "",
  val description: String = "",
  val poseName: String = ""
)

data class AccountCredentials(
  val username: String,
  val password: String,
  val pin: String = "4892",
  val server: String = "BR-SUL-01",
  val profileAssigned: String = "Perfil #2 (Privado)",
  val rules: String = "Compartilhar a senha com terceiros resulta em banimento permanente."
)

data class CredentialHistoryItem(
  val id: String,
  val accountId: String,
  val username: String,
  val password: String,
  val changedAt: String
)

data class SharedAccount(
  val id: String,
  val title: String,
  val category: String,
  val description: String,
  val basePricePerDay: Double = 29.90,
  val currentUsers: Int = 2,
  val maxUsers: Int = 4,
  val rating: Double = 4.8,
  val viewsCount: String = "1.2k",
  var likesCount: Int = 342,
  var isLiked: Boolean = false,
  val rentedCount: Int = 12,
  val last24hRentalsText: String = "+2.345 alugaram nas últimas 24h",
  val avatarDrawableRes: Int = 0,
  val avatarUrl: String = "",
  val model3dUrl: String = "",
  val itemsIncluded: List<IncludedItem> = emptyList(),
  val credentials: AccountCredentials,
  val isOccupied: Boolean = false,
  val createdBy: String? = null,
  val isActive: Boolean = true,
  val credentialUpdatedAt: Long = 0L,
  val customDiscountPercent: Int = 0
) {
  val isAlmostFull: Boolean
    get() = !isOccupied && currentUsers == (maxUsers - 1) && maxUsers > 1

  val spotsLeft: Int
    get() = (maxUsers - currentUsers).coerceAtLeast(0)
}

data class RentalCalculation(
  val days: Int,
  val people: Int,
  val rawTotal: Double,
  val discountPercent: Int,
  val discountAmount: Double,
  val finalPrice: Double,
  val formattedPrice: String
)

data class ActiveRental(
  val id: String,
  val accountId: String,
  val userId: String = "",
  val accountTitle: String,
  val avatarDrawableRes: Int = 0,
  val avatarUrl: String = "",
  val days: Int,
  val peopleCount: Int,
  val totalPaid: Double,
  val startTimeMillis: Long,
  val durationDays: Int,
  val credentials: AccountCredentials,
  val status: String = "ACTIVE"
) {
  fun isExpired(): Boolean {
    if (status.equals("EXPIRED", ignoreCase = true) || status.equals("CANCELLED", ignoreCase = true)) {
      return true
    }
    val totalDurationMillis = durationDays.toLong() * 24 * 60 * 60 * 1000L
    return (System.currentTimeMillis() - startTimeMillis) >= totalDurationMillis
  }

  fun remainingMillis(): Long {
    if (isExpired()) return 0L
    val totalDurationMillis = durationDays.toLong() * 24 * 60 * 60 * 1000L
    val diff = totalDurationMillis - (System.currentTimeMillis() - startTimeMillis)
    return if (diff > 0) diff else 0L
  }
}
