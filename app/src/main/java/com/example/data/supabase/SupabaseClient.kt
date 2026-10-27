package com.example.data.supabase

import android.util.Log
import com.example.BuildConfig
import com.example.R
import com.example.data.model.AccountCredentials
import com.example.data.model.ActiveRental
import com.example.data.model.IncludedItem
import com.example.data.model.SharedAccount
import com.example.data.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient {

  private val tag = "SupabaseClient"
  private val baseUrl: String = BuildConfig.SUPABASE_URL.trimEnd('/')
  private val anonKey: String = BuildConfig.SUPABASE_ANON_KEY

  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(20, TimeUnit.SECONDS)
    .build()

  private val jsonMedia = "application/json; charset=utf-8".toMediaType()

  // -------------------------------------------------------------
  // AUTH
  // -------------------------------------------------------------

  suspend fun signUp(email: String, pass: String): Result<UserSession> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/auth/v1/signup"
      val bodyJson = JSONObject().apply {
        put("email", email)
        put("password", pass)
      }
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Content-Type", "application/json")
        .post(bodyJson.toString().toRequestBody(jsonMedia))
        .build()

      val response = client.newCall(request).execute()
      val respBody = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errObj = runCatching { JSONObject(respBody) }.getOrNull()
        val msg = errObj?.optString("msg")
          ?: errObj?.optString("message")
          ?: "Erro ao cadastrar (${response.code})"
        return@withContext Result.failure(Exception(msg))
      }

      val json = JSONObject(respBody)
      val userObj = json.optJSONObject("user") ?: json
      val userId = userObj.optString("id")
      val userEmail = userObj.optString("email", email)
      val accessToken = json.optString("access_token")
      val refreshToken = json.optString("refresh_token")

      // If access_token was not returned directly (due to email confirmation setting),
      // we attempt immediate sign in because we enabled the auto_confirm trigger.
      if (accessToken.isBlank()) {
        return@withContext signIn(email, pass)
      }

      Result.success(
        UserSession(
          userId = userId,
          email = userEmail,
          accessToken = accessToken,
          refreshToken = refreshToken,
          username = userEmail.substringBefore("@")
        )
      )
    } catch (e: Exception) {
      Log.e(tag, "signUp failed", e)
      Result.failure(e)
    }
  }

  suspend fun signIn(email: String, pass: String): Result<UserSession> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/auth/v1/token?grant_type=password"
      val bodyJson = JSONObject().apply {
        put("email", email)
        put("password", pass)
      }
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Content-Type", "application/json")
        .post(bodyJson.toString().toRequestBody(jsonMedia))
        .build()

      val response = client.newCall(request).execute()
      val respBody = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errObj = runCatching { JSONObject(respBody) }.getOrNull()
        val desc = errObj?.optString("error_description")
          ?: errObj?.optString("message")
          ?: "Credenciais inválidas"
        return@withContext Result.failure(Exception(desc))
      }

      val json = JSONObject(respBody)
      val accessToken = json.getString("access_token")
      val refreshToken = json.optString("refresh_token")
      val userObj = json.getJSONObject("user")
      val userId = userObj.getString("id")
      val userEmail = userObj.optString("email", email)

      Result.success(
        UserSession(
          userId = userId,
          email = userEmail,
          accessToken = accessToken,
          refreshToken = refreshToken,
          username = userEmail.substringBefore("@")
        )
      )
    } catch (e: Exception) {
      Log.e(tag, "signIn failed", e)
      Result.failure(e)
    }
  }

  // -------------------------------------------------------------
  // POSTGREST: ACCOUNTS
  // -------------------------------------------------------------

  suspend fun fetchAccounts(): Result<List<SharedAccount>> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/rest/v1/accounts?select=*&order=created_at.desc"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .get()
        .build()

      val response = client.newCall(request).execute()
      val respBody = response.body?.string() ?: "[]"

      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}: $respBody"))
      }

      val jsonArray = JSONArray(respBody)
      val list = mutableListOf<SharedAccount>()

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val accId = obj.getString("id")
        val avatarResName = obj.optString("avatar_res_name", "avatar_character_pedestal")
        val avatarRes = resolveDrawableRes(avatarResName)

        val creds = AccountCredentials(
          username = obj.optString("credentials_username", "user_shared"),
          password = obj.optString("credentials_password", "SafeRentPass@2026"),
          pin = obj.optString("credentials_pin", "4892"),
          server = obj.optString("credentials_server", "América do Sul (BR-01)"),
          profileAssigned = obj.optString("credentials_profile_assigned", "Perfil #1 (VIP)"),
          rules = obj.optString("credentials_rules", "Não trocar senha nem transferir perfil.")
        )

        // Fetch included items for this account
        val items = fetchIncludedItemsSync(accId)

        list.add(
          SharedAccount(
            id = accId,
            title = obj.getString("title"),
            category = obj.optString("category", "Geral"),
            description = obj.optString("description", ""),
            basePricePerDay = obj.optDouble("base_price_per_day", 29.90),
            currentUsers = obj.optInt("current_users", 1),
            maxUsers = obj.optInt("max_users", 4),
            rating = obj.optDouble("rating", 4.8),
            viewsCount = obj.optString("views_count", "1.2k"),
            likesCount = obj.optInt("likes_count", 0),
            isLiked = false,
            rentedCount = obj.optInt("rented_count", 0),
            last24hRentalsText = obj.optString("last_24h_rentals_text", "+2.345 alugaram nas últimas 24h"),
            avatarDrawableRes = avatarRes,
            avatarUrl = obj.optString("avatar_url", ""),
            model3dUrl = obj.optString("model_3d_url", ""),
            itemsIncluded = items,
            credentials = creds,
            isOccupied = obj.optBoolean("is_occupied", false),
            createdBy = if (obj.isNull("created_by")) null else obj.optString("created_by")
          )
        )
      }

      Result.success(list)
    } catch (e: Exception) {
      Log.e(tag, "fetchAccounts failed", e)
      Result.failure(e)
    }
  }

  private fun fetchIncludedItemsSync(accountId: String): List<IncludedItem> {
    return try {
      val url = "$baseUrl/rest/v1/included_items?account_id=eq.$accountId"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .get()
        .build()

      val response = client.newCall(request).execute()
      val respBody = response.body?.string() ?: "[]"
      if (!response.isSuccessful) return defaultItems()

      val arr = JSONArray(respBody)
      if (arr.length() == 0) return defaultItems()

      val result = mutableListOf<IncludedItem>()
      for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val iconResName = o.optString("icon_res_name", "item_hat")
        val iconRes = resolveItemDrawableRes(iconResName)
        result.add(
          IncludedItem(
            id = o.getString("id"),
            name = o.getString("name"),
            iconRes = iconRes,
            iconUrl = o.optString("icon_url", ""),
            description = o.optString("description", ""),
            poseName = o.optString("pose_name", "")
          )
        )
      }
      result
    } catch (e: Exception) {
      defaultItems()
    }
  }

  suspend fun insertAccount(account: SharedAccount, authToken: String? = null): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        val url = "$baseUrl/rest/v1/accounts"
        val body = JSONObject().apply {
          put("id", account.id)
          put("title", account.title)
          put("category", account.category)
          put("description", account.description)
          put("base_price_per_day", account.basePricePerDay)
          put("current_users", account.currentUsers)
          put("max_users", account.maxUsers)
          put("rating", account.rating)
          put("views_count", account.viewsCount)
          put("likes_count", account.likesCount)
          put("rented_count", account.rentedCount)
          put("last_24h_rentals_text", account.last24hRentalsText)
          put("avatar_url", account.avatarUrl)
          put("avatar_res_name", resolveDrawableName(account.avatarDrawableRes))
          put("model_3d_url", account.model3dUrl)
          put("is_occupied", account.isOccupied)
          put("created_by", account.createdBy)
          put("credentials_username", account.credentials.username)
          put("credentials_password", account.credentials.password)
          put("credentials_pin", account.credentials.pin)
          put("credentials_server", account.credentials.server)
          put("credentials_profile_assigned", account.credentials.profileAssigned)
          put("credentials_rules", account.credentials.rules)
        }

        val authHeader = if (!authToken.isNullOrBlank()) "Bearer $authToken" else "Bearer $anonKey"
        val request = Request.Builder()
          .url(url)
          .addHeader("apikey", anonKey)
          .addHeader("Authorization", authHeader)
          .addHeader("Content-Type", "application/json")
          .addHeader("Prefer", "return=representation")
          .post(body.toString().toRequestBody(jsonMedia))
          .build()

        val resp = client.newCall(request).execute()
        if (!resp.isSuccessful) {
          val errorText = resp.body?.string() ?: ""
          return@withContext Result.failure(Exception("Failed to insert account: $errorText"))
        }

        // Insert items for this account
        if (account.itemsIncluded.isNotEmpty()) {
          insertIncludedItems(account.id, account.itemsIncluded, authToken)
        }

        Result.success(true)
      } catch (e: Exception) {
        Log.e(tag, "insertAccount failed", e)
        Result.failure(e)
      }
    }

  suspend fun insertIncludedItems(accountId: String, items: List<IncludedItem>, authToken: String? = null): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        val url = "$baseUrl/rest/v1/included_items"
        val arr = JSONArray()
        for (item in items) {
          val obj = JSONObject().apply {
            put("id", item.id)
            put("account_id", accountId)
            put("name", item.name)
            put("icon_url", item.iconUrl)
            put("icon_res_name", resolveItemDrawableName(item.iconRes))
            put("description", item.description)
            put("pose_name", item.poseName)
          }
          arr.put(obj)
        }

        val authHeader = if (!authToken.isNullOrBlank()) "Bearer $authToken" else "Bearer $anonKey"
        val request = Request.Builder()
          .url(url)
          .addHeader("apikey", anonKey)
          .addHeader("Authorization", authHeader)
          .addHeader("Content-Type", "application/json")
          .post(arr.toString().toRequestBody(jsonMedia))
          .build()

        val resp = client.newCall(request).execute()
        Result.success(resp.isSuccessful)
      } catch (e: Exception) {
        Log.e(tag, "insertIncludedItems failed", e)
        Result.failure(e)
      }
    }

  suspend fun updateAccountUsage(
    accountId: String,
    currentUsers: Int,
    rentedCount: Int,
    isOccupied: Boolean
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/rest/v1/accounts?id=eq.$accountId"
      val body = JSONObject().apply {
        put("current_users", currentUsers)
        put("rented_count", rentedCount)
        put("is_occupied", isOccupied)
      }

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", "application/json")
        .patch(body.toString().toRequestBody(jsonMedia))
        .build()

      val resp = client.newCall(request).execute()
      Result.success(resp.isSuccessful)
    } catch (e: Exception) {
      Log.e(tag, "updateAccountUsage failed", e)
      Result.failure(e)
    }
  }

  suspend fun toggleLike(accountId: String, newLikes: Int): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/rest/v1/accounts?id=eq.$accountId"
      val body = JSONObject().apply {
        put("likes_count", newLikes)
      }
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", "application/json")
        .patch(body.toString().toRequestBody(jsonMedia))
        .build()

      val resp = client.newCall(request).execute()
      Result.success(resp.isSuccessful)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun incrementViews(accountId: String, newViewsCount: String): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/rest/v1/accounts?id=eq.$accountId"
      val body = JSONObject().apply {
        put("views_count", newViewsCount)
      }
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", "application/json")
        .patch(body.toString().toRequestBody(jsonMedia))
        .build()

      val resp = client.newCall(request).execute()
      Result.success(resp.isSuccessful)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // -------------------------------------------------------------
  // POSTGREST: RENTALS
  // -------------------------------------------------------------

  suspend fun fetchRentals(userId: String?): Result<List<ActiveRental>> = withContext(Dispatchers.IO) {
    try {
      val url = if (userId.isNullOrBlank()) {
        "$baseUrl/rest/v1/rentals?select=*&order=created_at.desc"
      } else {
        "$baseUrl/rest/v1/rentals?user_id=eq.$userId&order=created_at.desc"
      }

      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .get()
        .build()

      val response = client.newCall(request).execute()
      val respBody = response.body?.string() ?: "[]"
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP ${response.code}: $respBody"))
      }

      val arr = JSONArray(respBody)
      val list = mutableListOf<ActiveRental>()
      for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val avatarResName = o.optString("avatar_res_name", "avatar_character_pedestal")
        val avatarRes = resolveDrawableRes(avatarResName)

        val creds = AccountCredentials(
          username = o.optString("credentials_username", "user_active"),
          password = o.optString("credentials_password", "Password@123"),
          pin = o.optString("credentials_pin", "1234"),
          server = o.optString("credentials_server", "BR-SUL"),
          profileAssigned = o.optString("credentials_profile_assigned", "Perfil #1"),
          rules = o.optString("credentials_rules", "Regras padrão.")
        )

        list.add(
          ActiveRental(
            id = o.getString("id"),
            accountId = o.getString("account_id"),
            userId = o.optString("user_id", ""),
            accountTitle = o.optString("account_title", "Conta Alugada"),
            avatarDrawableRes = avatarRes,
            avatarUrl = o.optString("avatar_url", ""),
            days = o.getInt("days"),
            peopleCount = o.optInt("people_count", 1),
            totalPaid = o.optDouble("total_paid", 0.0),
            startTimeMillis = o.getLong("start_time"),
            durationDays = o.getInt("duration_days"),
            credentials = creds,
            status = o.optString("status", "ACTIVE")
          )
        )
      }

      Result.success(list)
    } catch (e: Exception) {
      Log.e(tag, "fetchRentals failed", e)
      Result.failure(e)
    }
  }

  suspend fun insertRental(rental: ActiveRental, authToken: String? = null): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        val url = "$baseUrl/rest/v1/rentals"
        val body = JSONObject().apply {
          put("id", rental.id)
          put("account_id", rental.accountId)
          put("user_id", rental.userId)
          put("account_title", rental.accountTitle)
          put("avatar_res_name", resolveDrawableName(rental.avatarDrawableRes))
          put("avatar_url", rental.avatarUrl)
          put("days", rental.days)
          put("people_count", rental.peopleCount)
          put("total_paid", rental.totalPaid)
          put("start_time", rental.startTimeMillis)
          put("duration_days", rental.durationDays)
          put("credentials_username", rental.credentials.username)
          put("credentials_password", rental.credentials.password)
          put("credentials_pin", rental.credentials.pin)
          put("credentials_server", rental.credentials.server)
          put("credentials_profile_assigned", rental.credentials.profileAssigned)
          put("credentials_rules", rental.credentials.rules)
          put("status", rental.status)
        }

        val authHeader = if (!authToken.isNullOrBlank()) "Bearer $authToken" else "Bearer $anonKey"
        val request = Request.Builder()
          .url(url)
          .addHeader("apikey", anonKey)
          .addHeader("Authorization", authHeader)
          .addHeader("Content-Type", "application/json")
          .post(body.toString().toRequestBody(jsonMedia))
          .build()

        val resp = client.newCall(request).execute()
        Result.success(resp.isSuccessful)
      } catch (e: Exception) {
        Log.e(tag, "insertRental failed", e)
        Result.failure(e)
      }
    }

  suspend fun updateRentalStatus(rentalId: String, status: String): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/rest/v1/rentals?id=eq.$rentalId"
      val body = JSONObject().apply {
        put("status", status)
      }
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", "application/json")
        .patch(body.toString().toRequestBody(jsonMedia))
        .build()

      val resp = client.newCall(request).execute()
      Result.success(resp.isSuccessful)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun updateRentalDuration(rentalId: String, extraDays: Int): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      // First fetch current rental to compute new duration
      val getUrl = "$baseUrl/rest/v1/rentals?id=eq.$rentalId"
      val getReq = Request.Builder().url(getUrl).addHeader("apikey", anonKey).get().build()
      val getResp = client.newCall(getReq).execute()
      val bodyStr = getResp.body?.string() ?: "[]"
      val arr = JSONArray(bodyStr)
      if (arr.length() == 0) return@withContext Result.failure(Exception("Rental not found"))

      val rObj = arr.getJSONObject(0)
      val newDays = rObj.getInt("days") + extraDays
      val newDuration = rObj.getInt("duration_days") + extraDays

      val patchBody = JSONObject().apply {
        put("days", newDays)
        put("duration_days", newDuration)
        put("status", "ACTIVE")
      }

      val patchReq = Request.Builder()
        .url(getUrl)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", "application/json")
        .patch(patchBody.toString().toRequestBody(jsonMedia))
        .build()

      val patchResp = client.newCall(patchReq).execute()
      Result.success(patchResp.isSuccessful)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // -------------------------------------------------------------
  // SUPABASE STORAGE
  // -------------------------------------------------------------

  suspend fun uploadFile(
    bucket: String,
    fileName: String,
    bytes: ByteArray,
    contentType: String
  ): Result<String> = withContext(Dispatchers.IO) {
    try {
      val cleanFileName = fileName.replace(" ", "_")
      val uploadUrl = "$baseUrl/storage/v1/object/$bucket/$cleanFileName"
      val mediaType = contentType.toMediaType()

      val request = Request.Builder()
        .url(uploadUrl)
        .addHeader("apikey", anonKey)
        .addHeader("Authorization", "Bearer $anonKey")
        .addHeader("Content-Type", contentType)
        .addHeader("x-upsert", "true")
        .post(bytes.toRequestBody(mediaType))
        .build()

      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        val err = response.body?.string() ?: ""
        return@withContext Result.failure(Exception("Upload failed ($bucket): $err"))
      }

      val publicUrl = "$baseUrl/storage/v1/object/public/$bucket/$cleanFileName"
      Result.success(publicUrl)
    } catch (e: Exception) {
      Log.e(tag, "uploadFile failed", e)
      Result.failure(e)
    }
  }

  // -------------------------------------------------------------
  // RESOURCE RESOLUTION HELPERS
  // -------------------------------------------------------------

  private fun resolveDrawableRes(name: String): Int {
    return when (name) {
      "char_gamer" -> R.drawable.char_gamer
      "char_cyber" -> R.drawable.char_cyber
      else -> R.drawable.avatar_character_pedestal
    }
  }

  private fun resolveDrawableName(resId: Int): String {
    return when (resId) {
      R.drawable.char_gamer -> "char_gamer"
      R.drawable.char_cyber -> "char_cyber"
      else -> "avatar_character_pedestal"
    }
  }

  private fun resolveItemDrawableRes(name: String): Int {
    return when (name) {
      "item_backpack" -> R.drawable.item_backpack
      "item_boots" -> R.drawable.item_boots
      "item_map" -> R.drawable.item_map
      else -> R.drawable.item_hat
    }
  }

  private fun resolveItemDrawableName(resId: Int): String {
    return when (resId) {
      R.drawable.item_backpack -> "item_backpack"
      R.drawable.item_boots -> "item_boots"
      R.drawable.item_map -> "item_map"
      else -> "item_hat"
    }
  }

  private fun defaultItems(): List<IncludedItem> {
    return listOf(
      IncludedItem("item_hat", "Chapéu", R.drawable.item_hat, "", "Chapéu de safari explorador", "Pose Aventura"),
      IncludedItem("item_backpack", "Mochila", R.drawable.item_backpack, "", "Mochila táctica azul", "Pose Viagem"),
      IncludedItem("item_boots", "Botas", R.drawable.item_boots, "", "Botas todo-terreno de couro", "Pose Caminhada"),
      IncludedItem("item_map", "Mapa", R.drawable.item_map, "", "Mapa de navegação detalhado", "Pose Exploração")
    )
  }
}
