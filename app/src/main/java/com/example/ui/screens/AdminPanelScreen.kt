package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminLog
import com.example.data.model.PlatformMetrics
import com.example.data.model.Post
import com.example.data.model.SystemAnnouncement
import com.example.data.model.User
import com.example.ui.components.AvatarBadge
import com.example.ui.components.VerifiedBadge
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
import kotlin.math.sin

@Composable
fun AdminPanelScreen(
    currentUser: User?,
    metrics: PlatformMetrics,
    users: List<User>,
    posts: List<Post>,
    logs: List<AdminLog>,
    announcements: List<SystemAnnouncement>,
    maintenanceMode: Boolean = false,
    registrationsOpen: Boolean = true,
    onBack: () -> Unit,
    onToggleBanUser: (Long) -> Unit,
    onToggleVerifyUser: (Long) -> Unit,
    onToggleUserRole: (Long) -> Unit,
    onSendWarningToUser: (Long, String) -> Unit = { _, _ -> },
    onDeleteUser: (Long) -> Unit,
    onTogglePinPost: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onDismissReports: (Long) -> Unit,
    onPurgeFlaggedPosts: () -> Unit = {},
    onToggleMaintenanceMode: () -> Unit = {},
    onToggleRegistrationsOpen: () -> Unit = {},
    onBroadcast: (title: String, message: String) -> Unit,
    onDismissAnnouncement: (Long) -> Unit,
    onResetDatabase: () -> Unit,
    onRefreshMetrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // User Governance State
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedUserFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Admins, 2: Verified, 3: Banned
    var warningUserTarget by remember { mutableStateOf<User?>(null) }
    var warningText by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    // Content Moderation State
    var postSearchQuery by remember { mutableStateOf("") }
    var showOnlyReportedPosts by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<Post?>(null) }
    var showPurgeConfirmDialog by remember { mutableStateOf(false) }

    // Broadcast State
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastPriority by remember { mutableIntStateOf(0) } // 0: Info, 1: Alert, 2: Critical

    // Safety Dialogs
    var showResetDialog by remember { mutableStateOf(false) }

    @Suppress("DEPRECATION")
    fun triggerHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(Vibrator::class.java)
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
    ) {
        // Top Cyber Command Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(width = 0.5.dp, color = BorderLight)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AccentBlue
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(StatusGold, Color(0xFFFF8C00)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "C ADMIN COMMAND",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ROOT",
                                color = StatusGold,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (maintenanceMode) StatusRed else StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (maintenanceMode) "SYS_MAINTENANCE_ACTIVE" else "SYS_ONLINE • ${metrics.systemUptime} • ${metrics.serverLatencyMs}ms",
                            color = if (maintenanceMode) StatusRed else StatusGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(
                    onClick = {
                        triggerHaptic()
                        onRefreshMetrics()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentBlue)
                }
            }
        }

        // 5 Command Tabs
        val tabs = listOf(
            "Radar",
            "Users (${users.size})",
            "Content (${posts.size})",
            "Broadcast",
            "Logs (${logs.size})"
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Surface2Dark,
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
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        triggerHaptic()
                        selectedTab = index
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) AccentBlue else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        // Active Tab Screen Content
        when (selectedTab) {
            0 -> TelemetryRadarTab(
                metrics = metrics,
                logs = logs,
                maintenanceMode = maintenanceMode,
                registrationsOpen = registrationsOpen,
                onToggleMaintenance = onToggleMaintenanceMode,
                onToggleRegistrations = onToggleRegistrationsOpen,
                onResetDb = { showResetDialog = true },
                onRefresh = onRefreshMetrics
            )

            1 -> UserGovernanceTab(
                users = users,
                searchQuery = userSearchQuery,
                onSearchChange = { userSearchQuery = it },
                filterIndex = selectedUserFilter,
                onFilterChange = { selectedUserFilter = it },
                onToggleBan = onToggleBanUser,
                onToggleVerify = onToggleVerifyUser,
                onToggleRole = onToggleUserRole,
                onOpenWarning = { warningUserTarget = it; warningText = "" },
                onConfirmDelete = { userToDelete = it }
            )

            2 -> ContentModerationTab(
                posts = posts,
                searchQuery = postSearchQuery,
                onSearchChange = { postSearchQuery = it },
                showOnlyReported = showOnlyReportedPosts,
                onToggleFilter = { showOnlyReportedPosts = !showOnlyReportedPosts },
                onTogglePin = onTogglePinPost,
                onDismissReports = onDismissReports,
                onPurgeFlagged = { showPurgeConfirmDialog = true },
                onConfirmDelete = { postToDelete = it }
            )

            3 -> BroadcastControlTab(
                announcements = announcements,
                title = broadcastTitle,
                message = broadcastMessage,
                priority = broadcastPriority,
                onTitleChange = { broadcastTitle = it },
                onMessageChange = { broadcastMessage = it },
                onPriorityChange = { broadcastPriority = it },
                onBroadcast = { t, m ->
                    triggerHaptic()
                    onBroadcast(t, m)
                    broadcastTitle = ""
                    broadcastMessage = ""
                },
                onDismissAnnouncement = onDismissAnnouncement
            )

            4 -> SecurityAuditTab(
                logs = logs,
                onResetDb = { showResetDialog = true }
            )
        }
    }

    // Safety Dialogs
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Factory Database Reset", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(
                    text = "This will wipe all active posts, created accounts, comments, and stories, and re-seed clean default records. This action is irreversible.",
                    color = TextSecondary,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerHaptic()
                        showResetDialog = false
                        onResetDatabase()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Text("Execute Factory Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // User Warning Dispatch Dialog
    warningUserTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { warningUserTarget = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = StatusGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Issue Official Notice", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Dispatches a high-priority system notice directly to @${target.handle}'s notifications inbox.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = warningText,
                        onValueChange = { warningText = it },
                        label = { Text("Reason or Notice Body") },
                        placeholder = { Text("e.g. Violation of community standards: hate speech.") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StatusGold,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (warningText.isNotBlank()) {
                            triggerHaptic()
                            onSendWarningToUser(target.id, warningText)
                            warningUserTarget = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGold, contentColor = Color.Black)
                ) {
                    Text("Dispatch Notice", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { warningUserTarget = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // User Deletion Safety Dialog
    userToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account permanently?", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete user @${target.handle} (${target.name})? All their activity will be purged.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerHaptic()
                        onDeleteUser(target.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Text("Delete User", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Post Deletion Safety Dialog
    postToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = {
                Text("Delete Post #${target.id}?", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(
                    text = "Remove this post from global visibility and database records?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerHaptic()
                        onDeletePost(target.id)
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Text("Delete Post", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { postToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Bulk Purge Flagged Posts Dialog
    if (showPurgeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = StatusRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Purge Flagged Content", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(
                    text = "This will permanently remove all posts that have been flagged by users or moderation filters in one operation.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerHaptic()
                        showPurgeConfirmDialog = false
                        onPurgeFlaggedPosts()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Text("Purge All Flagged", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPurgeConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

// ==========================================
// 1. TELEMETRY RADAR TAB
// ==========================================
@Composable
fun TelemetryRadarTab(
    metrics: PlatformMetrics,
    logs: List<AdminLog>,
    maintenanceMode: Boolean,
    registrationsOpen: Boolean,
    onToggleMaintenance: () -> Unit,
    onToggleRegistrations: () -> Unit,
    onResetDb: () -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Real-time Canvas Waveform Telemetry Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE THROUGHPUT RADAR",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "${metrics.serverLatencyMs}ms / 60 FPS",
                            color = StatusGreen,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Waveform Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface2Dark)
                    ) {
                        val path = Path()
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f

                        // Draw grid lines
                        drawLine(
                            color = Color(0x22FFFFFF),
                            start = Offset(0f, midY),
                            end = Offset(w, midY),
                            strokeWidth = 1f
                        )

                        // Draw sinusoidal throughput wave
                        val points = 30
                        for (i in 0..points) {
                            val x = (w / points) * i
                            val wave1 = sin((i * 0.4f).toDouble()).toFloat() * (h * 0.28f)
                            val y = midY + wave1
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(listOf(AccentBlue, AccentPurple, StatusGreen)),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-time socket pipeline: Optimal • Zero packet drops detected",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Metrics Grid (4 items)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(title = "Total Users", value = "${metrics.totalUsers}", iconColor = AccentBlue, modifier = Modifier.weight(1f))
                MetricCard(title = "Total Posts", value = "${metrics.totalPosts}", iconColor = AccentPurple, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(title = "Flagged Posts", value = "${metrics.reportedPostsCount}", iconColor = if (metrics.reportedPostsCount > 0) StatusRed else StatusGreen, modifier = Modifier.weight(1f))
                MetricCard(title = "Banned Users", value = "${metrics.bannedUsersCount}", iconColor = StatusGold, modifier = Modifier.weight(1f))
            }
        }

        // Global System Gateway Switches
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "SYSTEM CONTROLS & PROTOCOLS", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    // Maintenance Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Engineering, contentDescription = null, tint = if (maintenanceMode) StatusRed else TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Maintenance Mode", color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(text = "Broadcast alert and flag service maintenance to all clients", color = TextMuted, fontSize = 11.5.sp)
                        }
                        Switch(
                            checked = maintenanceMode,
                            onCheckedChange = { onToggleMaintenance() },
                            colors = SwitchDefaults.colors(checkedThumbColor = StatusRed, checkedTrackColor = StatusRed.copy(alpha = 0.3f))
                        )
                    }

                    // Registration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (registrationsOpen) Icons.Default.LockOpen else Icons.Default.Lock, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "New User Gateway", color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(text = if (registrationsOpen) "Public registration is currently allowed" else "Gateway closed: Invite-only", color = TextMuted, fontSize = 11.5.sp)
                        }
                        Switch(
                            checked = registrationsOpen,
                            onCheckedChange = { onToggleRegistrations() },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue, checkedTrackColor = AccentBlue.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }

        // Factory Reset Danger Action
        item {
            OutlinedButton(
                onClick = onResetDb,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = StatusRed.copy(alpha = 0.08f), contentColor = StatusRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset & Re-seed Entire Database", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = TextMuted, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = iconColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
        }
    }
}

// ==========================================
// 2. USER GOVERNANCE TAB
// ==========================================
@Composable
fun UserGovernanceTab(
    users: List<User>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filterIndex: Int,
    onFilterChange: (Int) -> Unit,
    onToggleBan: (Long) -> Unit,
    onToggleVerify: (Long) -> Unit,
    onToggleRole: (Long) -> Unit,
    onOpenWarning: (User) -> Unit,
    onConfirmDelete: (User) -> Unit
) {
    val filteredUsers = users.filter { user ->
        val matchesQuery = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.handle.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterIndex) {
            1 -> user.isAdmin
            2 -> user.verified
            3 -> user.isBanned
            else -> true
        }
        matchesQuery && matchesFilter
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search users by name, @handle, or email…", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Surface2Dark,
                        unfocusedContainerColor = Surface2Dark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Filter Chips
                val filterLabels = listOf("All (${users.size})", "Admins", "Verified", "Banned")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterLabels.forEachIndexed { idx, label ->
                        val isSelected = filterIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else Surface2Dark)
                                .border(1.dp, if (isSelected) AccentBlue else BorderSubtle, RoundedCornerShape(12.dp))
                                .clickable { onFilterChange(idx) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) AccentBlue else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No users matching criteria.", color = TextMuted, fontSize = 13.5.sp)
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { user ->
                UserAdminCard(
                    user = user,
                    onToggleBan = { onToggleBan(user.id) },
                    onToggleVerify = { onToggleVerify(user.id) },
                    onToggleRole = { onToggleRole(user.id) },
                    onOpenWarning = { onOpenWarning(user) },
                    onConfirmDelete = { onConfirmDelete(user) }
                )
            }
        }
    }
}

@Composable
fun UserAdminCard(
    user: User,
    onToggleBan: () -> Unit,
    onToggleVerify: () -> Unit,
    onToggleRole: () -> Unit,
    onOpenWarning: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (user.isBanned) StatusRed else if (user.isAdmin) StatusGold else BorderLight
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AvatarBadge(
                    letter = user.avatarLetter,
                    colorIndex = user.avatarColorIndex,
                    size = 42.dp,
                    fontSize = 15,
                    hasGlowRing = user.isAdmin || user.verified
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                        if (user.verified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(size = 14.dp)
                        }
                    }

                    Text(
                        text = "@${user.handle} • ${user.email.ifBlank { "No email" }}",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (user.isAdmin) StatusGold.copy(alpha = 0.2f) else Surface3Dark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = user.role,
                                color = if (user.isAdmin) StatusGold else AccentBlue,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (user.isBanned) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusRed.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "BANNED", color = StatusRed, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Delete Action Icon
                IconButton(onClick = onConfirmDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleBan,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (user.isBanned) StatusGreen else StatusRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isBanned) StatusGreen else StatusRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (user.isBanned) "Unban" else "Ban", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onToggleVerify,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (user.verified) "Unverify" else "Verify", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onToggleRole,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Text(text = "Role: ${user.role}", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onOpenWarning,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9800)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Warn", fontSize = 11.sp)
                }
            }
        }
    }
}

// ==========================================
// 3. CONTENT MODERATION TAB
// ==========================================
@Composable
fun ContentModerationTab(
    posts: List<Post>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    showOnlyReported: Boolean,
    onToggleFilter: () -> Unit,
    onTogglePin: (Long) -> Unit,
    onDismissReports: (Long) -> Unit,
    onPurgeFlagged: () -> Unit,
    onConfirmDelete: (Post) -> Unit
) {
    val filtered = posts.filter { post ->
        val matchesQuery = searchQuery.isBlank() ||
                post.text.contains(searchQuery, ignoreCase = true) ||
                (post.author?.name?.contains(searchQuery, ignoreCase = true) ?: false)
        val matchesFilter = if (showOnlyReported) post.isFlagged else true
        matchesQuery && matchesFilter
    }

    val flaggedCount = posts.count { it.isFlagged }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Filter posts…", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentBlue) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Surface2Dark,
                        unfocusedContainerColor = Surface2Dark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )

                // Flags Filter Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (showOnlyReported) StatusRed else Surface2Dark)
                        .border(1.dp, if (showOnlyReported) StatusRed else BorderLight, RoundedCornerShape(14.dp))
                        .clickable(onClick = onToggleFilter)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = if (showOnlyReported) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Flags ($flaggedCount)",
                            color = if (showOnlyReported) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bulk Purge Button if flags exist
        if (flaggedCount > 0) {
            item {
                Button(
                    onClick = onPurgeFlagged,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Bulk Purge $flaggedCount Flagged Posts", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (showOnlyReported) "No flagged posts! Feed is clean." else "No posts found.",
                        color = TextMuted,
                        fontSize = 13.5.sp
                    )
                }
            }
        } else {
            items(filtered, key = { it.id }) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (post.isFlagged) StatusRed else if (post.isPinned) StatusGold else BorderLight
                        )
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarBadge(
                                    letter = post.author?.avatarLetter ?: "U",
                                    colorIndex = post.author?.avatarColorIndex ?: 0,
                                    size = 32.dp,
                                    fontSize = 12
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = post.author?.name ?: "Unknown",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(text = "Post #${post.id} • ${post.timestamp}", color = TextMuted, fontSize = 11.sp)
                                }
                            }

                            if (post.isFlagged) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "FLAGGED (${post.reportCount})", color = StatusRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = post.text, color = TextPrimary, fontSize = 13.5.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Admin Post Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onTogglePin(post.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (post.isPinned) "Unpin" else "Pin", fontSize = 11.5.sp)
                                }
                            }

                            if (post.isFlagged) {
                                OutlinedButton(
                                    onClick = { onDismissReports(post.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGreen),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Text(text = "Clear Flags", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { onConfirmDelete(post) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Delete", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. BROADCAST CONTROL TAB
// ==========================================
@Composable
fun BroadcastControlTab(
    announcements: List<SystemAnnouncement>,
    title: String,
    message: String,
    priority: Int,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onBroadcast: (String, String) -> Unit,
    onDismissAnnouncement: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = AccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "BROADCAST TO ALL USERS", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    // Priority Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Normal (Info)", "Priority (Alert)", "Emergency (Critical)").forEachIndexed { idx, pLabel ->
                            val isSelected = priority == idx
                            val pColor = when (idx) {
                                0 -> AccentBlue
                                1 -> StatusGold
                                else -> StatusRed
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) pColor.copy(alpha = 0.2f) else Surface2Dark)
                                    .border(1.dp, if (isSelected) pColor else BorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { onPriorityChange(idx) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pLabel,
                                    color = if (isSelected) pColor else TextSecondary,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = { Text("Announcement Headline") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = onMessageChange,
                        label = { Text("Announcement Content") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (title.isNotBlank() && message.isNotBlank()) {
                                onBroadcast(title, message)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Broadcast Instantly 📢", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(text = "ACTIVE LIVE ANNOUNCEMENTS (${announcements.size})", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        if (announcements.isEmpty()) {
            item {
                Text(text = "No active broadcasts running.", color = TextMuted, fontSize = 12.5.sp)
            }
        } else {
            items(announcements, key = { it.id }) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentBlue))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = ann.title, color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text(text = ann.message, color = TextPrimary, fontSize = 12.sp)
                            Text(text = "Broadcasted at ${ann.timestamp}", color = TextMuted, fontSize = 10.5.sp)
                        }
                        IconButton(onClick = { onDismissAnnouncement(ann.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. SECURITY AUDIT TAB
// ==========================================
@Composable
fun SecurityAuditTab(
    logs: List<AdminLog>,
    onResetDb: () -> Unit
) {
    var logSearch by remember { mutableStateOf("") }
    val filteredLogs = logs.filter {
        logSearch.isBlank() ||
                it.action.contains(logSearch, ignoreCase = true) ||
                it.adminName.contains(logSearch, ignoreCase = true) ||
                it.target.contains(logSearch, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = logSearch,
                onValueChange = { logSearch = it },
                placeholder = { Text("Filter audit trail by action or operator…", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentBlue) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = Surface2Dark,
                    unfocusedContainerColor = Surface2Dark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (filteredLogs.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text(text = "No audit log entries found.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StatusGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = StatusGold, modifier = Modifier.size(15.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = log.action, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                Text(text = log.timestamp, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(text = "${log.adminName} ➔ ${log.target}", color = TextSecondary, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }
    }
}
