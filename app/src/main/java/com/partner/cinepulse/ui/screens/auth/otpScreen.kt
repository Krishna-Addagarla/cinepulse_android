package com.partner.cinepulse.ui.screens.auth


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.ui.theme.*
import kotlinx.coroutines.delay

private const val OTP_LENGTH = 6
private const val RESEND_COUNTDOWN_SECONDS = 60

@Composable
fun OtpVerificationScreen(
    email: String,                          // passed from previous screen
    onVerificationSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── OTP digit slots ───────────────────────────────────────────────────────
    val digits = remember { Array(OTP_LENGTH) { mutableStateOf("") } }
    val focusRequesters = remember { Array(OTP_LENGTH) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    // ── Resend countdown timer ────────────────────────────────────────────────
    var secondsLeft by remember { mutableStateOf(RESEND_COUNTDOWN_SECONDS) }
    var timerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            secondsLeft = RESEND_COUNTDOWN_SECONDS
            while (secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
            }
            timerRunning = false
        }
    }

    // ── Auto-focus first box on entry ─────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(300L)
        focusRequesters[0].requestFocus()
    }

    // ── Navigate on success ───────────────────────────────────────────────────
    LaunchedEffect(uiState.verifyResponse) {
        if (uiState.verifyResponse != null) {
            onVerificationSuccess()
            viewModel.resetVerifyResponse()
        }
    }

    // ── Root ──────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Radial glow — centred lower to sit behind the OTP card
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.Center)
                .offset(y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            // ── Back button ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Shield / lock icon ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0D1B3E), Color(0xFF0A1628))
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.8f), AccentBlue.copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔐", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Heading ───────────────────────────────────────────────────────
            Text(
                text = "Verify Your Email",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Masked email display
            Text(
                text = "We sent a 6-digit code to",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = maskEmail(email),
                color = AccentBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── OTP boxes ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                repeat(OTP_LENGTH) { index ->
                    OtpDigitBox(
                        digit = digits[index].value,
                        isFocused = false,          // visual focus handled by border colour inside
                        hasError = uiState.errorMessage != null,
                        focusRequester = focusRequesters[index],
                        onValueChange = { input ->
                            when {
                                // User typed a digit
                                input.length == 1 && input.all { it.isDigit() } -> {
                                    digits[index].value = input
                                    if (index < OTP_LENGTH - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                        // Auto-submit when last digit filled
                                        val otp = digits.joinToString("") { it.value }
                                        if (otp.length == OTP_LENGTH) {
                                            viewModel.verifyOTP(
                                                otpVerificationRequest(email = email, otp = otp)
                                            )
                                        }
                                    }
                                }
                                // User pasted full OTP
                                input.length == OTP_LENGTH && input.all { it.isDigit() } -> {
                                    input.forEachIndexed { i, ch -> digits[i].value = ch.toString() }
                                    focusManager.clearFocus()
                                    viewModel.verifyOTP(
                                        otpVerificationRequest(email = email, otp = input)
                                    )
                                }
                                // Cleared / backspace
                                input.isEmpty() -> {
                                    digits[index].value = ""
                                    viewModel.clearError()
                                }
                            }
                        },
                        onBackspace = {
                            if (digits[index].value.isEmpty() && index > 0) {
                                digits[index - 1].value = ""
                                focusRequesters[index - 1].requestFocus()
                            } else {
                                digits[index].value = ""
                            }
                            viewModel.clearError()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Error banner ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                uiState.errorMessage?.let {
                    ErrorBanner(message = it)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Verify button ─────────────────────────────────────────────────
            val otp = digits.joinToString("") { it.value }
            val isOtpComplete = otp.length == OTP_LENGTH

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            uiState.isLoading -> CardDark
                            isOtpComplete     -> AccentBlue
                            else              -> AccentBlue.copy(alpha = 0.35f)
                        }
                    )
                    .clickable(enabled = isOtpComplete && !uiState.isLoading) {
                        viewModel.verifyOTP(
                            otpVerificationRequest(email = email, otp = otp)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Verify Code",
                        color = if (isOtpComplete) TextPrimary else TextPrimary.copy(alpha = 0.4f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Resend section ────────────────────────────────────────────────
            if (timerRunning) {
                // Countdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resend code in",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    // Countdown pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60),
                            color = AccentBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Resend active
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Didn't receive the code?",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Resend",
                        color = AccentBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            // Clear digits and restart timer
                            digits.forEach { it.value = "" }
                            focusRequesters[0].requestFocus()
                            viewModel.clearError()
                            timerRunning = true
                            viewModel.resendOTP(
                                email
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AccentRed.copy(alpha = 0.12f))
            .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = message, color = AccentRed, fontSize = 13.sp)
    }
}

// ── Single OTP digit box ───────────────────────────────────────────────────────
@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    hasError: Boolean,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val borderColor = when {
        hasError        -> AccentRed
        digit.isNotEmpty() -> AccentBlue.copy(alpha = 0.7f)
        else            -> CardBorder
    }

    val bgColor = when {
        hasError        -> AccentRed.copy(alpha = 0.08f)
        digit.isNotEmpty() -> AccentBlue.copy(alpha = 0.06f)
        else            -> CardDark
    }

    BasicTextField(
        value = digit,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.key == Key.Backspace && event.type == KeyEventType.KeyDown) {
                    onBackspace()
                    true
                } else false
            },
        textStyle = TextStyle(
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(Color.Transparent),   // hide cursor in digit boxes
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                inner()
                // Underscore hint when empty
                if (digit.isEmpty()) {
                    Text(text = "–", color = TextSecondary.copy(alpha = 0.4f), fontSize = 18.sp)
                }
            }
        }
    )
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun maskEmail(email: String): String {
    val atIndex = email.indexOf('@')
    if (atIndex <= 1) return email
    val visible = email.take(2)
    val masked  = "*".repeat((atIndex - 2).coerceAtLeast(0))
    return "$visible$masked${email.substring(atIndex)}"
}

@Composable
@Preview(showBackground = true)
fun OtpVerificationScreenPreview(){
    OtpVerificationScreen(
        email = "james.s.sherman@example-pet-store.com",
        onVerificationSuccess = {},
    )
}
