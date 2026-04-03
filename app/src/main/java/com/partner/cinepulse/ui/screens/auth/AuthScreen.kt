package com.partner.cinepulse.ui.screens.auth
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.AccentGreen
import com.partner.cinepulse.ui.theme.AccentRed
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark        = Color(0xFF080C14)
//private val CardDark      = Color(0xFF0F1623)
//private val CardBorder    = Color(0xFF1C2333)
////private val AccentBlue    = Color(0xFF1A6BFF)
//private val AccentRed     = Color(0xFFE50914)
//private val AccentGreen   = Color(0xFF1DB954)
//private val TextPrimary   = Color(0xFFFFFFFF)
//private val TextSecondary = Color(0xFF8A95A8)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun AuthScreen(
    onSignInSuccess: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    var isSignIn by remember { mutableStateOf(true) }

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword        by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Subtle radial glow behind the form
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(72.dp))

            // ── App logo ──────────────────────────────────────────────────────
            AppLogo()

            Spacer(modifier = Modifier.height(36.dp))

            // ── Mode toggle tabs ──────────────────────────────────────────────
            ModeToggle(isSignIn = isSignIn, onToggle = { isSignIn = it })

            Spacer(modifier = Modifier.height(32.dp))

            // ── Animated header ───────────────────────────────────────────────
            AnimatedContent(
                targetState = isSignIn,
                transitionSpec = {
                    (fadeIn() + expandVertically()) togetherWith (fadeOut() + shrinkVertically())
                },
                label = "header"
            ) { signIn ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (signIn) "Welcome Back" else "Create Account",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (signIn) "Sign in to continue your cinematic journey"
                        else "Join CinePulse and discover great cinema",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Input fields ──────────────────────────────────────────────────
            AuthInputField(
                value       = email,
                onValueChange = { email = it },
                placeholder = "Gmail",
                icon        = Icons.Default.Email,
                keyboardType= KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthInputField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = "Password",
                icon          = Icons.Default.Lock,
                isPassword    = true,
                showPassword  = showPassword,
                onToggleVisibility = { showPassword = !showPassword }
            )

            // Confirm Password — only visible in Sign Up mode
            AnimatedVisibility(
                visible = !isSignIn,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    AuthInputField(
                        value         = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder   = "Confirm Password",
                        icon          = Icons.Default.Lock,
                        isPassword    = true,
                        showPassword  = showConfirmPassword,
                        onToggleVisibility = { showConfirmPassword = !showConfirmPassword }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Primary CTA button ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentBlue)
                    .clickable { if (isSignIn) onSignInSuccess() else onSignUpSuccess() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSignIn) "Sign In" else "Create Account",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Footer links ──────────────────────────────────────────────────
            AnimatedContent(
                targetState = isSignIn,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label        = "footer"
            ) { signIn ->
                if (signIn) {
                    // Sign In footer
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Forgot Password?",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = TextSecondary, fontSize = 13.sp)) {
                                    append("Don't have an account? ")
                                }
                                withStyle(SpanStyle(color = AccentGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)) {
                                    append("Sign Up")
                                }
                            },
                            modifier = Modifier.clickable { isSignIn = false }
                        )
                    }
                } else {
                    // Sign Up footer
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = TextSecondary, fontSize = 13.sp)) {
                                append("Already have an account? ")
                            }
                            withStyle(SpanStyle(color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)) {
                                append("Sign In")
                            }
                        },
                        modifier = Modifier.clickable { isSignIn = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── App Logo ───────────────────────────────────────────────────────────────────
@Composable
private fun AppLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                    )
                )
                .border(1.5.dp, AccentRed.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🎬", fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Cine",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Pulse",
                color = AccentRed,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "Your cinematic companion",
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

// ── Mode toggle ────────────────────────────────────────────────────────────────
@Composable
private fun ModeToggle(isSignIn: Boolean, onToggle: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(true to "Sign In", false to "Sign Up").forEach { (mode, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (isSignIn == mode)
                                Brush.linearGradient(listOf(AccentBlue, Color(0xFF0D47A1)))
                            else
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onToggle(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSignIn == mode) TextPrimary else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (isSignIn == mode) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ── Input field ────────────────────────────────────────────────────────────────
@Composable
private fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onToggleVisibility: (() -> Unit)? = null
) {
    val visualTransformation = if (isPassword && !showPassword)
        PasswordVisualTransformation() else VisualTransformation.None

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(AccentBlue),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(text = placeholder, color = TextSecondary, fontSize = 14.sp)
                }
                inner()
            }
        )

        if (isPassword && onToggleVisibility != null) {
            Icon(
                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle password",
                tint = TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onToggleVisibility() }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}