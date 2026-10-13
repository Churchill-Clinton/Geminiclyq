package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun AdminPanelScreen(
    currentUser: User?,
    metrics: PlatformMetrics,
    users: List<User>,
    posts: List<Post>,
    logs: List<AdminLog>,
    announcements: List<SystemAnnouncement>,
    onBack: () -> Unit,
    onToggleBanUser: (Long) -> Unit,
    onToggleVerifyUser: (Long) -> Unit,
    onToggleUserRole: (Long) -> Unit,
    onDeleteUser: (Long) -> Unit,
    onTogglePinPost: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onDismissReports: (Long) -> Unit,
    onBroadcast: (title: String, message: String) -> Unit,
    onDismissAnnouncement: (Long) -> Unit,
    onResetDatabase: () -> Unit,
    onRefreshMetrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var userSearchQuery by remember { mutableStateOf("") }
    var postSearchQuery by remember { mutableStateOf("") }
    var showOnlyReportedPosts by remember { mutableStateOf(false) }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

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
                                .background(StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "SYS_ONLINE • ${metrics.systemUptime} • ${metrics.serverLatencyMs}ms",
                            color = StatusGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(onClick = onRefreshMetrics, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentBlue)
                }
            }
        }

        // Navigation Tabs
        val tabs = listOf("Metrics", "Users (${users.size})", "Content (${posts.size})", "Broadcast")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Surface2Dark,
            contentColor = AccentBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentBlue,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) AccentBlue else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp
                        )
                    }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> MetricsTab(
                metrics = metrics,
                logs = logs,
                onResetDbClick = { showResetDialog = true },
                onBroadcastShortcut = { selectedTab = 3 }
            )
            1 -> UserGovernanceTab(
                users = users,
                searchQuery = userSearchQuery,
                onSearchChange = { userSearchQuery = it },
                onToggleBan = onToggleBanUser,
                onToggleVerify = onToggleVerifyUser,
                onToggleRole = onToggleUserRole,
                onDelete = onDeleteUser
            )
            2 -> ContentModerationTab(
                posts = posts,
                searchQuery = postSearchQuery,
                onSearchChange = { postSearchQuery = it },
                showOnlyReported = showOnlyReportedPosts,
                onToggleFilter = { showOnlyReportedPosts = !showOnlyReportedPosts },
                onTogglePin = onTogglePinPost,
                onDelete = onDeletePost,
                onDismissReports = onDismissReports
            )
            3 -> BroadcastTab(
                title = broadcastTitle,
                message = broadcastMessage,
                onTitleChange = { broadcastTitle = it },
                onMessageChange = { broadcastMessage = it },
                onSend = {
                    if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                        onBroadcast(broadcastTitle.trim(), broadcastMessage.trim())
                        broadcastTitle = ""
                        broadcastMessage = ""
                    }
                },
                announcements = announcements,
                onDismissAnnouncement = onDismissAnnouncement
            )
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Platform Database?", color = StatusRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This action will clear all user posts, comments, and reset the seed database back to factory specifications. Continue?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetDatabase()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White)
                ) {
                    Text("Confirm Reset")
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
}

@Composable
fun MetricsTab(
    metrics: PlatformMetrics,
    logs: List<AdminLog>,
    onResetDbClick: () -> Unit,
    onBroadcastShortcut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // KPI Grid
        item {
            Text(
                text = "REAL-TIME SYSTEM TELEMETRY",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(
                    title = "Total Users",
                    value = "${metrics.totalUsers}",
                    sub = "${metrics.bannedUsersCount} banned",
                    icon = Icons.Default.Person,
                    accent = AccentBlue,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Total Posts",
                    value = "${metrics.totalPosts}",
                    sub = "${metrics.reportedPostsCount} reported",
                    icon = Icons.Default.PinDrop,
                    accent = AccentPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(
                    title = "Total Likes",
                    value = "${metrics.totalLikes}",
                    sub = "High engagement",
                    icon = Icons.Default.Star,
                    accent = StatusGold,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Active Sessions",
                    value = "${metrics.activeSessions}",
                    sub = "Relay latency ${metrics.serverLatencyMs}ms",
                    icon = Icons.Default.Security,
                    accent = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Controls
        item {
            Text(
                text = "ADMIN CONTROLS",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onBroadcastShortcut,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onResetDbClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed.copy(alpha = 0.85f), contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Seed", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Live Audit Log Stream
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "REAL-TIME AUDIT LOG STREAM",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(logs, key = { it.id }) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${log.action} • ${log.target}",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Admin: ${log.adminName} • ${log.timestamp}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextMuted, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, color = accent, fontSize = 11.sp)
        }
    }
}

@Composable
fun UserGovernanceTab(
    users: List<User>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleBan: (Long) -> Unit,
    onToggleVerify: (Long) -> Unit,
    onToggleRole: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val filtered = if (searchQuery.isNotBlank()) {
        users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.handle.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
        }
    } else users

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search users by name, handle, email…", color = TextMuted, fontSize = 13.sp) },
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

        items(filtered, key = { it.id }) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (user.isBanned) StatusRed else if (user.isAdmin) StatusGold else BorderLight
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarBadge(
                            letter = user.avatarLetter,
                            colorIndex = user.avatarColorIndex,
                            size = 44.dp,
                            fontSize = 16
                        )

                        Spacer(modifier = Modifier.width(12.dp))

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

                            // Badges Row
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RoleBadge(role = user.role)
                                if (user.isBanned) {
                                    StatusPill(text = "BANNED", bg = StatusRed.copy(alpha = 0.2f), textColor = StatusRed)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Admin Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ban / Unban
                        OutlinedButton(
                            onClick = { onToggleBan(user.id) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (user.isBanned) StatusGreen else StatusRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (user.isBanned) StatusGreen else StatusRed
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (user.isBanned) "Unban" else "Ban", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // Verified Toggle
                        OutlinedButton(
                            onClick = { onToggleVerify(user.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Text(text = if (user.verified) "Unverify" else "Verify", fontSize = 11.5.sp)
                        }

                        // Role Toggle
                        OutlinedButton(
                            onClick = { onToggleRole(user.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(text = "Role: ${user.role}", fontSize = 11.sp)
                        }

                        // Delete
                        IconButton(
                            onClick = { onDelete(user.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Surface2Dark)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleBadge(role: String) {
    val (bg, textColor) = when (role) {
        "ADMIN" -> Pair(StatusGold.copy(alpha = 0.2f), StatusGold)
        "MODERATOR" -> Pair(AccentPurple.copy(alpha = 0.2f), AccentPurple)
        else -> Pair(Surface3Dark, TextMuted)
    }
    StatusPill(text = role, bg = bg, textColor = textColor)
}

@Composable
fun StatusPill(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ContentModerationTab(
    posts: List<Post>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    showOnlyReported: Boolean,
    onToggleFilter: () -> Unit,
    onTogglePin: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onDismissReports: (Long) -> Unit
) {
    val filtered = posts.filter {
        (!showOnlyReported || it.isFlagged || it.reportCount > 0) &&
                (searchQuery.isBlank() || it.text.contains(searchQuery, ignoreCase = true) || it.author?.name?.contains(searchQuery, ignoreCase = true) == true)
    }

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
                            text = "Flags Only",
                            color = if (showOnlyReported) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showOnlyReported) "No flagged posts! Platform is clean." else "No posts found.",
                        color = TextMuted,
                        fontSize = 14.sp
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Author header & Flags indicator
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
                                    Text(
                                        text = "Post #${post.id} • ${post.timestamp}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (post.isPinned) {
                                    StatusPill(text = "PINNED", bg = StatusGold.copy(alpha = 0.2f), textColor = StatusGold)
                                }
                                if (post.isFlagged || post.reportCount > 0) {
                                    StatusPill(text = "FLAGGED (${post.reportCount})", bg = StatusRed.copy(alpha = 0.2f), textColor = StatusRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = post.text,
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Admin Moderation Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pin / Unpin
                            OutlinedButton(
                                onClick = { onTogglePin(post.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (post.isPinned) "Unpin" else "Pin", fontSize = 11.5.sp)
                            }

                            // Dismiss reports
                            if (post.isFlagged || post.reportCount > 0) {
                                OutlinedButton(
                                    onClick = { onDismissReports(post.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGreen),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Flag", fontSize = 11.5.sp)
                                }
                            }

                            // Delete post
                            Button(
                                onClick = { onDelete(post.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BroadcastTab(
    title: String,
    message: String,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    announcements: List<SystemAnnouncement>,
    onDismissAnnouncement: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentBlue))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Global System Broadcast",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = { Text("Announcement Title") },
                        singleLine = true,
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

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = onMessageChange,
                        label = { Text("Announcement Message") },
                        minLines = 3,
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onSend,
                        enabled = title.isNotBlank() && message.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Broadcast to All Users", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ACTIVE PLATFORM ANNOUNCEMENTS",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        if (announcements.isEmpty()) {
            item {
                Text(text = "No active broadcasts.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            items(announcements, key = { it.id }) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = ann.title, color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ann.message, color = TextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ann.timestamp, color = TextMuted, fontSize = 11.sp)
                        }

                        IconButton(onClick = { onDismissAnnouncement(ann.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
