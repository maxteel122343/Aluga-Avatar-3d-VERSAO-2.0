package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("aluga_conta_auth", Context.MODE_PRIVATE)

  private val _currentSession = MutableStateFlow<UserSession?>(null)
  val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

  val isLoggedIn: Boolean
    get() = _currentSession.value != null

  init {
    loadSession()
  }

  private fun loadSession() {
    val userId = prefs.getString("user_id", null)
    val email = prefs.getString("email", null)
    val accessToken = prefs.getString("access_token", null)
    val refreshToken = prefs.getString("refresh_token", "") ?: ""
    val username = prefs.getString("username", "") ?: ""

    if (!userId.isNullOrBlank() && !accessToken.isNullOrBlank() && !email.isNullOrBlank()) {
      _currentSession.value = UserSession(
        userId = userId,
        email = email,
        accessToken = accessToken,
        refreshToken = refreshToken,
        username = username.ifBlank { email.substringBefore("@") }
      )
    }
  }

  fun saveSession(session: UserSession) {
    prefs.edit()
      .putString("user_id", session.userId)
      .putString("email", session.email)
      .putString("access_token", session.accessToken)
      .putString("refresh_token", session.refreshToken)
      .putString("username", session.username)
      .apply()

    _currentSession.value = session
  }

  fun clearSession() {
    prefs.edit().clear().apply()
    _currentSession.value = null
  }
}
