package com.example.ui.components

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.IncludedItem
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Gray900
import com.example.ui.theme.White

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PedestalView(
  avatarRes: Int = R.drawable.avatar_character_pedestal,
  avatarUrl: String = "",
  model3dUrl: String = "",
  selectedItem: IncludedItem?,
  isOccupied: Boolean = false,
  currentUsers: Int = 1,
  onRentClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var rotationAngle by remember { mutableFloatStateOf(0f) }
  var isDragging by remember { mutableStateOf(false) }

  // Toggle between 3D model (if available) and 2D character
  var show3DMode by remember(model3dUrl) {
    mutableStateOf(model3dUrl.isNotBlank())
  }

  val has3DModel = model3dUrl.isNotBlank()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Soft Pedestal Base Shadow & Circular White Pedestal
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 12.dp)
        .width(220.dp)
        .height(36.dp)
        .shadow(elevation = 14.dp, shape = CircleShape, spotColor = Color(0x33000000))
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFE5E7EB))
          ),
          shape = CircleShape
        )
        .border(width = 1.dp, color = Color(0xFFD1D5DB), shape = CircleShape)
    )

    // Secondary subtle rim for 3D depth
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 26.dp)
        .width(180.dp)
        .height(20.dp)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFAFAFA), Color(0xFFF3F4F6))
          ),
          shape = CircleShape
        )
    )

    // Character or 3D Model Display
    if (show3DMode && has3DModel) {
      // 3D Model Viewer using WebGL / Google model-viewer inside transparent WebView
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 24.dp)
          .width(280.dp)
          .height(300.dp),
        contentAlignment = Alignment.Center
      ) {
        AndroidView(
          factory = { ctx ->
            WebView(ctx).apply {
              setBackgroundColor(0) // Transparent
              setLayerType(View.LAYER_TYPE_HARDWARE, null)
              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
                allowFileAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
              }
              webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  view?.setBackgroundColor(0)
                }
              }

              val htmlData = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                  <script type="module" src="https://ajax.googleapis.com/ajax/libs/model-viewer/3.4.0/model-viewer.min.js"></script>
                  <style>
                    html, body {
                      margin: 0;
                      padding: 0;
                      width: 100%;
                      height: 100%;
                      overflow: hidden;
                      background-color: transparent !important;
                    }
                    model-viewer {
                      width: 100%;
                      height: 100%;
                      --poster-color: transparent;
                      background-color: transparent !important;
                    }
                  </style>
                </head>
                <body>
                  <model-viewer
                    src="$model3dUrl"
                    alt="Modelo 3D Pedestal"
                    auto-rotate
                    rotation-per-second="25deg"
                    camera-controls
                    touch-action="pan-y"
                    shadow-intensity="1.2"
                    exposure="1.05"
                    loading="eager"
                  >
                  </model-viewer>
                </body>
                </html>
              """.trimIndent()

              loadDataWithBaseURL("https://modelviewer.dev", htmlData, "text/html", "UTF-8", null)
            }
          },
          update = { webView ->
            // If model changed
            val htmlData = """
              <!DOCTYPE html>
              <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <script type="module" src="https://ajax.googleapis.com/ajax/libs/model-viewer/3.4.0/model-viewer.min.js"></script>
                <style>
                  html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: transparent; }
                  model-viewer { width: 100%; height: 100%; --poster-color: transparent; }
                </style>
              </head>
              <body>
                <model-viewer
                  src="$model3dUrl"
                  alt="Modelo 3D Pedestal"
                  auto-rotate
                  rotation-per-second="25deg"
                  camera-controls
                  touch-action="pan-y"
                  shadow-intensity="1.2"
                  exposure="1.05"
                  loading="eager"
                >
                </model-viewer>
              </body>
              </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://modelviewer.dev", htmlData, "text/html", "UTF-8", null)
          },
          modifier = Modifier.fillMaxSize()
        )
      }
    } else {
      // 2D Character Display with 3D Perspective Rotation
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 22.dp)
          .width(230.dp)
          .height(310.dp)
          .pointerInput(Unit) {
            detectHorizontalDragGestures(
              onDragStart = { isDragging = true },
              onDragEnd = { isDragging = false },
              onDragCancel = { isDragging = false },
              onHorizontalDrag = { _, dragAmount ->
                rotationAngle = (rotationAngle + dragAmount * 0.45f)
              }
            )
          }
          .graphicsLayer {
            rotationY = rotationAngle
            cameraDistance = 16f * density
          },
        contentAlignment = Alignment.BottomCenter
      ) {
        if (avatarUrl.isNotBlank()) {
          AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar no pedestal",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Image(
            painter = painterResource(id = if (avatarRes != 0) avatarRes else R.drawable.avatar_character_pedestal),
            contentDescription = "Avatar no pedestal",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }

    // Top Left: 3D / 2D Interactive Rotation or Toggle Hint
    Surface(
      color = Color(0xCC111827),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 16.dp, top = 12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
      ) {
        Icon(
          imageVector = if (show3DMode) Icons.Default.ViewInAr else Icons.Default.Refresh,
          contentDescription = null,
          tint = White,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (show3DMode) "3D Interativo Ativo" else if (isDragging) "Girando 3D..." else "Arraste para girar",
          color = White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Top Right: Mode switcher if 3D model exists
    if (has3DModel) {
      Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BluePrimary),
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(end = 16.dp, top = 12.dp)
          .clickable { show3DMode = !show3DMode }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = if (show3DMode) Icons.Default.Image else Icons.Default.ViewInAr,
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (show3DMode) "Ver Imagem 2D" else "Ver Modelo 3D",
            color = BluePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Item Equipped Floating Feedback Badge
    AnimatedVisibility(
      visible = selectedItem != null,
      enter = scaleIn() + fadeIn(),
      exit = scaleOut() + fadeOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 8.dp)
    ) {
      Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BluePrimary),
        shadowElevation = 4.dp
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${selectedItem?.name} equipado!",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BluePrimary
          )
        }
      }
    }

    // Floating "Alugar" Button on the right of the character
    Button(
      onClick = onRentClick,
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isOccupied) Color(0xFFEF4444) else BluePrimary,
        contentColor = White
      ),
      shape = RoundedCornerShape(14.dp),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 16.dp, top = 20.dp)
        .height(48.dp)
        .testTag("pedestal_rent_button")
    ) {
      Icon(
        imageVector = Icons.Default.ShoppingBag,
        contentDescription = "Alugar",
        tint = White,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (isOccupied) "Lotada" else "Alugar",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = White
      )
    }
  }
}
