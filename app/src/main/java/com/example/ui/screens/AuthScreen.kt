package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AccountRepository
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray100
import com.example.ui.theme.Gray200
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray900
import com.example.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  repository: AccountRepository,
  reasonMessage: String? = null,
  onDismiss: (() -> Unit)? = null,
  onAuthSuccess: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current

  var isLoginTab by remember { mutableStateOf(true) }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun performSubmit() {
    focusManager.clearFocus()
    errorMessage = null

    if (email.isBlank() || !email.contains("@")) {
      errorMessage = "Informe um e-mail válido."
      return
    }

    if (password.length < 6) {
      errorMessage = "A senha deve ter pelo menos 6 caracteres."
      return
    }

    if (!isLoginTab && password != confirmPassword) {
      errorMessage = "As senhas não coincidem."
      return
    }

    isLoading = true
    scope.launch {
      val result = if (isLoginTab) {
        repository.login(email.trim(), password)
      } else {
        repository.register(email.trim(), password)
      }

      isLoading = false
      if (result.isSuccess) {
        onAuthSuccess()
      } else {
        val err = result.exceptionOrNull()?.message ?: "Falha na autenticação"
        errorMessage = when {
          err.contains("Invalid login credentials", ignoreCase = true) -> "E-mail ou senha incorretos."
          err.contains("User already registered", ignoreCase = true) -> "Este e-mail já está cadastrado. Faça login."
          err.contains("Password should be", ignoreCase = true) -> "A senha deve ter pelo menos 6 caracteres."
          else -> err
        }
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isLoginTab) "Entrar na Conta" else "Criar Conta",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Gray900
          )
        },
        navigationIcon = {
          if (onDismiss != null) {
            IconButton(onClick = onDismiss) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Gray900
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
      )
    },
    containerColor = Color(0xFFF9FAFB)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Reason Header if present
      if (!reasonMessage.isNullOrBlank()) {
        Surface(
          color = Color(0xFFEFF6FF),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = BluePrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = reasonMessage,
              color = BluePrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // App Brand Avatar
      Box(
        modifier = Modifier
          .size(72.dp)
          .background(Color(0xFFEFF6FF), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Segurança",
          tint = BluePrimary,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "AlugaConta",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Gray900
      )
      Text(
        text = "Autenticação em nuvem protegida por Supabase",
        fontSize = 13.sp,
        color = Gray500,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Tab switcher
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          TabRow(
            selectedTabIndex = if (isLoginTab) 0 else 1,
            containerColor = Color(0xFFF3F4F6),
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[if (isLoginTab) 0 else 1]),
                color = BluePrimary
              )
            },
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
          ) {
            Tab(
              selected = isLoginTab,
              onClick = {
                isLoginTab = true
                errorMessage = null
              },
              text = {
                Text(
                  "Entrar",
                  fontWeight = if (isLoginTab) FontWeight.Bold else FontWeight.Normal,
                  color = if (isLoginTab) BluePrimary else Gray500
                )
              },
              modifier = Modifier.testTag("tab_login")
            )
            Tab(
              selected = !isLoginTab,
              onClick = {
                isLoginTab = false
                errorMessage = null
              },
              text = {
                Text(
                  "Cadastrar",
                  fontWeight = if (!isLoginTab) FontWeight.Bold else FontWeight.Normal,
                  color = if (!isLoginTab) BluePrimary else Gray500
                )
              },
              modifier = Modifier.testTag("tab_register")
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Error Message
          AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Surface(
              color = Color(0xFFFEE2E2),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
            ) {
              Text(
                text = errorMessage ?: "",
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          // Email Field
          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              errorMessage = null
            },
            label = { Text("E-mail") },
            placeholder = { Text("seuemail@gmail.com") },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = Gray500)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BluePrimary,
              unfocusedBorderColor = Gray200
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_email_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password Field
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              errorMessage = null
            },
            label = { Text("Senha") },
            placeholder = { Text("••••••••") },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Gray500)
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Ocultar senha" else "Ver senha",
                  tint = Gray500
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = if (isLoginTab) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onDone = { if (isLoginTab) performSubmit() },
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BluePrimary,
              unfocusedBorderColor = Gray200
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input")
          )

          // Confirm Password (Only in Register tab)
          if (!isLoginTab) {
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
              value = confirmPassword,
              onValueChange = {
                confirmPassword = it
                errorMessage = null
              },
              label = { Text("Confirmar Senha") },
              placeholder = { Text("••••••••") },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Gray500)
              },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(
                onDone = { performSubmit() }
              ),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Gray200
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_confirm_password_input")
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Submit Button
          Button(
            onClick = { performSubmit() },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
              containerColor = BluePrimary,
              contentColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("auth_submit_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                color = White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
              )
            } else {
              Text(
                text = if (isLoginTab) "Entrar" else "Criar Conta Grátis",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      if (onDismiss != null) {
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onDismiss) {
          Text(
            "Continuar como visitante",
            color = Gray500,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
