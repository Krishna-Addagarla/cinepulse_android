package com.partner.cinepulse.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.ui.theme.*
import java.util.Calendar
import java.util.Date

// 1. The Entry Point (Used in Navigation)
@Composable
fun AuthScreen(
    onSignInSuccess: () -> Unit = {},
    onSignUpSuccess: (String) -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContent(
        uiState = uiState,
        onLogin = { viewModel.loginUser(it) },
        onRegister = { viewModel.registerUser(it) },
        onClearError = { viewModel.clearError() },
        onResetVerify = { viewModel.resetVerifyResponse() },
        onResetReg = { viewModel.resetRegistrationState() },
        onSignInSuccess = onSignInSuccess,
        onSignUpSuccess = onSignUpSuccess
    )
}

// 2. The UI Content (Used for Preview and App)
@Composable
fun AuthScreenContent(
    uiState: AuthUiState, // Replace with your actual State class name
    onLogin: (loginRequest) -> Unit,
    onRegister: (registrationRequest) -> Unit,
    onClearError: () -> Unit,
    onResetVerify: () -> Unit,
    onResetReg: () -> Unit,
    onSignInSuccess: () -> Unit,
    onSignUpSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    var isSignIn by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf<Date?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var passwordMismatchError by remember { mutableStateOf(false) }
    var dobError by remember { mutableStateOf(false) }

    // Avoid initializing Dialogs during Preview to prevent Render Issues
    val datePickerDialog = if (!isPreview) {
        val calendar = Calendar.getInstance()
        remember {
            android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, day, 0, 0, 0)
                    selectedCalendar.set(Calendar.MILLISECOND, 0)
                    dateOfBirth = selectedCalendar.time
                    dobError = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = calendar.timeInMillis
            }
        }
    } else null

    LaunchedEffect(uiState.verifyResponse) {
        if (uiState.verifyResponse != null) {
            onSignInSuccess()
            onResetVerify()
        }
    }

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onSignUpSuccess(email)
            onResetReg()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        // Glow Effect
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue.copy(alpha = 0.07f), Color.Transparent)
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
            AppLogo()
            Spacer(modifier = Modifier.height(36.dp))

            ModeToggle(isSignIn = isSignIn, onToggle = {
                isSignIn = it
                onClearError()
                passwordMismatchError = false
                dobError = false
            })

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = isSignIn,
                transitionSpec = { (fadeIn() + expandVertically()) togetherWith (fadeOut() + shrinkVertically()) },
                label = "header"
            ) { signIn ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (signIn) "Welcome Back" else "Create Account",
                        color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (signIn) "Sign in to continue your cinematic journey"
                        else "Join CinePulse and discover great cinema",
                        color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            AuthInputField(
                value = email, onValueChange = { email = it },
                placeholder = "Email", icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(14.dp))

            AuthInputField(
                value = password, onValueChange = {
                    password = it
                    passwordMismatchError = false
                },
                placeholder = "Password", icon = Icons.Default.Lock,
                isPassword = true, showPassword = showPassword,
                onToggleVisibility = { showPassword = !showPassword }
            )

            AnimatedVisibility(
                visible = !isSignIn,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    AuthInputField(
                        value = confirmPassword, onValueChange = {
                            confirmPassword = it
                            passwordMismatchError = false
                        },
                        placeholder = "Confirm Password", icon = Icons.Default.Lock,
                        isPassword = true, showPassword = showConfirmPassword,
                        onToggleVisibility = { showConfirmPassword = !showConfirmPassword }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    DateOfBirthField(
                        value = dateOfBirth,
                        hasError = dobError,
                        onClick = { datePickerDialog?.show() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (passwordMismatchError) {
                ErrorBanner(message = "Passwords do not match")
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (dobError) {
                ErrorBanner(message = "Please select your date of birth")
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.errorMessage?.let { msg ->
                ErrorBanner(message = msg)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (uiState.isLoading) CardDark else AccentBlue)
                    .clickable(enabled = !uiState.isLoading) {
                        if (isSignIn) {
                            onLogin(loginRequest(email = email, password = password))
                        } else {
                            passwordMismatchError = password != confirmPassword
                            dobError = dateOfBirth == null
                            if (!passwordMismatchError && !dobError) {
                                onRegister(registrationRequest(
                                    email = email,
                                    password = password,
                                    date_of_birth = formatDateForApi(dateOfBirth!!)
                                ))
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = TextPrimary, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isSignIn) "Sign In" else "Create Account",
                        color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
            if (isSignIn) {
                Text(
                    text = "Forgot Password?",
                    color = TextSecondary, fontSize = 13.sp,
                    modifier = Modifier.clickable { }
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append("Already have an account? ") }
                        withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.Bold)) { append("Sign In") }
                    },
                    modifier = Modifier.clickable { isSignIn = true }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Supporting UI Components (Kept same as your original) ──────────────────────

@Composable
private fun DateOfBirthField(value: Date?, hasError: Boolean, onClick: () -> Unit) {
    val borderColor = if (hasError) AccentRed else CardBorder
    val dateString = value?.let { formatDate(it) } ?: ""
    Row(
        modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(12.dp))
            .background(CardDark).border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.DateRange, null, tint = if (hasError) AccentRed else TextSecondary, modifier = Modifier.size(18.dp))
        Text(if (dateString.isEmpty()) "Date of Birth (DD/MM/YYYY)" else dateString, color = if (dateString.isEmpty()) TextSecondary else TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowDropDown, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
}

private fun formatDate(date: Date): String = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(date)
private fun formatDateForApi(date: Date): String = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(date)

@Composable
private fun ErrorBanner(message: String) {
    Box(modifier = Modifier
        .fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(AccentRed.copy(alpha = 0.12f))
        .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(14.dp)) {
        Text(text = message, color = AccentRed, fontSize = 13.sp)
    }
}

@Composable
private fun AppLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))).border(1.5.dp, AccentRed.copy(alpha = 0.6f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            Text(text = "🎬", fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Cine", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "Pulse", color = AccentRed, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ModeToggle(isSignIn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        .background(CardDark)
        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        .padding(4.dp)) {
        listOf(true to "Sign In", false to "Sign Up").forEach { (mode, label) ->
            Box(
                modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(9.dp))
                    .background(if (isSignIn == mode) Brush.linearGradient(listOf(AccentBlue, Color(0xFF0D47A1))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
                    .clickable { onToggle(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (isSignIn == mode) TextPrimary else TextSecondary, fontSize = 14.sp, fontWeight = if (isSignIn == mode) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun AuthInputField(value: String, onValueChange: (String) -> Unit, placeholder: String, icon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text, isPassword: Boolean = false, showPassword: Boolean = false, onToggleVisibility: (() -> Unit)? = null) {
    val visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None
    Row(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(12.dp)).background(CardDark).border(1.dp, CardBorder, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        BasicTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f),
            textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp), singleLine = true,
            visualTransformation = visualTransformation, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(AccentBlue),
            decorationBox = { inner -> if (value.isEmpty()) Text(placeholder, color = TextSecondary, fontSize = 14.sp); inner() }
        )
        if (isPassword && onToggleVisibility != null) {
            Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = TextSecondary, modifier = Modifier.size(18.dp).clickable { onToggleVisibility() })
        }
    }
}

// ── FIXED PREVIEW ──────────────────────────────────────────────────────────────
@Composable
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0B)
fun PreviewAuthScreen() {
    // Provide dummy state
    AuthScreenContent(
        uiState = AuthUiState(isLoading = false, errorMessage = "Sample Error"),
        onLogin = {},
        onRegister = {},
        onClearError = {},
        onResetVerify = {},
        onResetReg = {},
        onSignInSuccess = {},
        onSignUpSuccess = {}
    )
}