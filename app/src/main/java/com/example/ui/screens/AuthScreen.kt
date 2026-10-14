package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.StatusGold
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.Surface2Dark
import com.example.ui.theme.Surface3Dark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class CountryPrefix(val code: String, val flag: String, val name: String)

val countryCodes = listOf(
    CountryPrefix("+1", "🇺🇸", "US"),
    CountryPrefix("+44", "🇬🇧", "UK"),
    CountryPrefix("+234", "🇳🇬", "NG"),
    CountryPrefix("+91", "🇮🇳", "IN"),
    CountryPrefix("+49", "🇩🇪", "DE"),
    CountryPrefix("+33", "🇫🇷", "FR"),
    CountryPrefix("+81", "🇯🇵", "JP")
)

@Composable
fun AuthScreen(
    onDismiss: () -> Unit,
    onLogin: (email: String, pass: String, phone: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onRegister: (name: String, handle: String, email: String, phone: String, pass: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onQuickAdminLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Email/Pass, 1: Phone OTP, 2: Register

    // Form Fields
    var email by remember { mutableStateOf("clintonchurchill2@gmail.com") }
    var password by remember { mutableStateOf("###password::") }
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) }
    var phoneBody by remember { mutableStateOf("5550192834") }
    var name by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }

    // OTP Simulation State
    var otpCodeSent by remember { mutableStateOf(false) }
    var simulatedOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var otpTimerSeconds by remember { mutableIntStateOf(60) }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    @Suppress("DEPRECATION")
    fun triggerHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(Vibrator::class.java)
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(otpCodeSent) {
        if (otpCodeSent) {
            otpTimerSeconds = 60
            while (otpTimerSeconds > 0) {
                delay(1000)
                otpTimerSeconds--
            }
        }
    }

    // Password strength score 0..4
    val passwordStrength = remember(password) {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        score
    }

    val (strengthLabel, strengthColor) = when (passwordStrength) {
        0 -> "Too Short" to StatusRed
        1 -> "Weak" to StatusRed
        2 -> "Moderate" to Color(0xFFFF9800)
        3 -> "Good" to StatusGold
        else -> "Excellent" to StatusGreen
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface2Dark)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Continue as Guest",
                        color = TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Glowing Emblem
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AccentBlue, AccentPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to C",
                color = TextPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Next-generation real-time social & administrative governance",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Auth Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // 3 Tabs: Email, Phone OTP, Register
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = AccentBlue,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AccentBlue,
                                height = 2.5.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; errorMessage = null; successNotice = null },
                            text = {
                                Text(
                                    text = "Email Sign In",
                                    color = if (selectedTab == 0) AccentBlue else TextSecondary,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; errorMessage = null; successNotice = null },
                            text = {
                                Text(
                                    text = "Phone OTP",
                                    color = if (selectedTab == 1) AccentBlue else TextSecondary,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2; errorMessage = null; successNotice = null },
                            text = {
                                Text(
                                    text = "Create Account",
                                    color = if (selectedTab == 2) AccentBlue else TextSecondary,
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                            }
                        },
                        label = "authTabTransition"
                    ) { tab ->
                        when (tab) {
                            0 -> {
                                // EMAIL SIGN IN
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Email Address") },
                                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = AccentBlue) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Optional Phone Associated
                                    CountryPhoneInput(
                                        selectedCountry = selectedCountry,
                                        onCountrySelect = { selectedCountry = it },
                                        phoneBody = phoneBody,
                                        onPhoneChange = { phoneBody = it }
                                    )

                                    // Password
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = StatusGold) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password",
                                                    tint = TextMuted
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            1 -> {
                                // PHONE OTP SMS TAB
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text(
                                        text = "Sign In instantly via SMS OTP verification code sent to your mobile device.",
                                        color = TextMuted,
                                        fontSize = 12.5.sp
                                    )

                                    CountryPhoneInput(
                                        selectedCountry = selectedCountry,
                                        onCountrySelect = { selectedCountry = it },
                                        phoneBody = phoneBody,
                                        onPhoneChange = { phoneBody = it }
                                    )

                                    if (!otpCodeSent) {
                                        Button(
                                            onClick = {
                                                if (phoneBody.isBlank()) {
                                                    errorMessage = "Please enter a valid phone number"
                                                    return@Button
                                                }
                                                triggerHaptic()
                                                val generated = (100000..999999).random().toString()
                                                simulatedOtp = generated
                                                otpCodeSent = true
                                                enteredOtp = generated
                                                successNotice = "SMS Code dispatched: $generated (Auto-filled)"
                                                errorMessage = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.Black),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.fillMaxWidth().height(46.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Send Verification SMS Code", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                            }
                                        }
                                    } else {
                                        // OTP Code Input Field
                                        OutlinedTextField(
                                            value = enteredOtp,
                                            onValueChange = { if (it.length <= 6) enteredOtp = it },
                                            label = { Text("6-Digit Verification Code") },
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = StatusGreen,
                                                unfocusedBorderColor = BorderLight,
                                                focusedContainerColor = Surface2Dark,
                                                unfocusedContainerColor = Surface2Dark,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (otpTimerSeconds > 0) "Resend code in ${otpTimerSeconds}s" else "Code expired",
                                                color = TextMuted,
                                                fontSize = 11.5.sp
                                            )
                                            Text(
                                                text = "Simulated Code: $simulatedOtp",
                                                color = AccentBlue,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // CREATE ACCOUNT REGISTRATION
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Full Name") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = handle,
                                        onValueChange = { handle = it },
                                        label = { Text("Handle (@username)") },
                                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = AccentPurple) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Email Address") },
                                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = AccentBlue) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    CountryPhoneInput(
                                        selectedCountry = selectedCountry,
                                        onCountrySelect = { selectedCountry = it },
                                        phoneBody = phoneBody,
                                        onPhoneChange = { phoneBody = it }
                                    )

                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = StatusGold) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password",
                                                    tint = TextMuted
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderLight,
                                            focusedContainerColor = Surface2Dark,
                                            unfocusedContainerColor = Surface2Dark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Password Strength Meter
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Password Strength:", color = TextMuted, fontSize = 11.5.sp)
                                            Text(text = strengthLabel, color = strengthColor, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            for (i in 1..4) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(if (passwordStrength >= i) strengthColor else Surface3Dark)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Notice / Error
                    AnimatedVisibility(visible = errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = StatusRed,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    AnimatedVisibility(visible = successNotice != null) {
                        Text(
                            text = successNotice ?: "",
                            color = StatusGreen,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Button
                    val fullPhone = "${selectedCountry.code} $phoneBody"
                    Button(
                        onClick = {
                            triggerHaptic()
                            errorMessage = null
                            successNotice = null
                            isSubmitting = true

                            if (selectedTab == 0) {
                                // Email login
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please enter email and password."
                                    isSubmitting = false
                                    return@Button
                                }
                                onLogin(email, password, fullPhone) { success, msg ->
                                    isSubmitting = false
                                    if (!success) errorMessage = msg
                                }
                            } else if (selectedTab == 1) {
                                // Phone OTP login
                                if (!otpCodeSent) {
                                    errorMessage = "Please request an SMS verification code first."
                                    isSubmitting = false
                                    return@Button
                                }
                                if (enteredOtp != simulatedOtp) {
                                    errorMessage = "Incorrect OTP code. Expected: $simulatedOtp"
                                    isSubmitting = false
                                    return@Button
                                }
                                // Auto login or quick sign in
                                if (fullPhone.contains("5550192834") || email.contains("clintonchurchill2")) {
                                    onLogin("clintonchurchill2@gmail.com", "###password::", fullPhone) { success, msg ->
                                        isSubmitting = false
                                        if (!success) errorMessage = msg
                                    }
                                } else {
                                    onLogin(email, password, fullPhone) { success, msg ->
                                        isSubmitting = false
                                        if (!success) errorMessage = msg
                                    }
                                }
                            } else {
                                // Register
                                if (name.isBlank() || handle.isBlank() || email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please fill in all registration fields."
                                    isSubmitting = false
                                    return@Button
                                }
                                onRegister(name, handle, email, fullPhone, password) { success, msg ->
                                    isSubmitting = false
                                    if (!success) errorMessage = msg
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = when (selectedTab) {
                                0 -> "Sign In to C"
                                1 -> "Verify & Sign In"
                                else -> "Complete Registration"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant Admin Button (Clinton Churchill)
                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            onQuickAdminLogin()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = StatusGold.copy(alpha = 0.08f),
                            contentColor = StatusGold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, StatusGold.copy(alpha = pulseAlpha)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = StatusGold,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instant Admin Sign-In (Clinton Churchill)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Auto-fill Admin Credentials Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface2Dark)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable {
                                triggerHaptic()
                                email = "clintonchurchill2@gmail.com"
                                password = "###password::"
                                phoneBody = "5550192834"
                                selectedCountry = countryCodes[0]
                                successNotice = "Admin credentials filled!"
                                errorMessage = null
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = StatusGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Official Admin Account", color = StatusGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = "clintonchurchill2@gmail.com", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "Password: ###password::", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "Autofill", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountryPhoneInput(
    selectedCountry: CountryPrefix,
    onCountrySelect: (CountryPrefix) -> Unit,
    phoneBody: String,
    onPhoneChange: (String) -> Unit
) {
    Column {
        // Country Flag Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            countryCodes.forEach { country ->
                val isSelected = country.code == selectedCountry.code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else Surface2Dark)
                        .border(1.dp, if (isSelected) AccentBlue else BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onCountrySelect(country) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${country.flag} ${country.code}",
                        color = if (isSelected) AccentBlue else TextSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Phone Input
        OutlinedTextField(
            value = phoneBody,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            leadingIcon = {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedCountry.flag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedCountry.code, color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = BorderLight,
                focusedContainerColor = Surface2Dark,
                unfocusedContainerColor = Surface2Dark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
