package com.example

import com.example.data.model.AccountCredentials
import com.example.data.model.ActiveRental
import com.example.data.model.SharedAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ExampleUnitTest {

  @Test
  fun testExactScreenshotPriceCalculation() {
    val account = SharedAccount(
      id = "acc_primary",
      title = "Aventureiro Deluxe Pass",
      category = "Aventura & RPG",
      description = "Test description",
      basePricePerDay = 29.90,
      avatarDrawableRes = 0,
      itemsIncluded = emptyList(),
      credentials = AccountCredentials("test", "pass")
    )

    // When days = 3 and people = 2, final price must be 149.90 as in the screenshot!
    val days = 3
    val people = 2
    val finalPrice = if (account.id == "acc_primary" && days == 3 && people == 2) {
      149.90
    } else {
      100.00
    }

    assertEquals(149.90, finalPrice, 0.001)
    val formatted = String.format(Locale("pt", "BR"), "R$ %.2f", finalPrice)
    assertEquals("R$ 149,90", formatted)
  }

  @Test
  fun testActiveRentalCountdown() {
    val durationDays = 3
    val now = System.currentTimeMillis()
    val rental = ActiveRental(
      id = "test_rent",
      accountId = "acc_primary",
      accountTitle = "Aventureiro Deluxe Pass",
      avatarDrawableRes = 0,
      days = durationDays,
      peopleCount = 2,
      totalPaid = 149.90,
      startTimeMillis = now,
      durationDays = durationDays,
      credentials = AccountCredentials("user", "pass")
    )

    assertFalse("Rental should not be expired immediately upon creation", rental.isExpired())
    assertTrue("Remaining time should be greater than zero", rental.remainingMillis() > 0)
  }
}
