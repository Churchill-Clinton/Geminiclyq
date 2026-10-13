package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler
import com.example.ui.components.AddStoryDialog
import com.example.ui.components.AvatarBadge
import com.example.ui.components.CommentsSheet
import com.example.ui.components.ComposeDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusGold
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: SocialViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val feedFilter by viewModel.feedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val feedPosts by viewModel.feedPosts.collectAsState()
    val allPosts by viewModel.allPosts.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val messagesForActiveConversation by viewModel.messagesForActiveConversation.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs by viewModel.unreadNotifCount.collectAsState()
    val unreadMessages by viewModel.unreadMessagesCount.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val adminLogs by viewModel.adminLogs.collectAsState()
    val platformMetrics by viewModel.platformMetrics.collectAsState()
    val showAuthScreen by viewModel.showAuthScreen.collectAsState()

    // Dialog states
    val activeCommentPost by viewModel.activeCommentPost.collectAsState()
    val commentsForActivePost by viewModel.commentsForActivePost.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()
    val showComposeDialog by viewModel.showComposeDialog.collectAsState()
    val showAddStoryDialog by viewModel.showAddStoryDialog.collectAsState()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BgDark)) {
        val isWideScreen = maxWidth > 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BgDark,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                )
            },
            bottomBar = {
                if (!isWideScreen && currentTab != ScreenTab.ADMIN_PANEL) {
                    CBottomNavigationBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        onComposeClick = { viewModel.openComposeDialog() },
                        unreadNotifs = unreadNotifs,
                        unreadMessages = unreadMessages,
                        isAdmin = isAdmin,
                        currentUserLetter = currentUser?.avatarLetter ?: "C",
                        currentUserColorIndex = currentUser?.avatarColorIndex ?: 0
                    )
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isWideScreen) {
                    CNavigationRail(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        onComposeClick = { viewModel.openComposeDialog() },
                        unreadNotifs = unreadNotifs,
                        unreadMessages = unreadMessages,
                        isAdmin = isAdmin
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (currentTab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                posts = feedPosts,
                                stories = stories,
                                announcements = announcements,
                                currentFilter = feedFilter,
                                isAdmin = isAdmin,
                                onFilterChange = { viewModel.setFeedFilter(it) },
                                onLikePost = { viewModel.toggleLike(it) },
                                onCommentPost = { viewModel.openCommentsSheet(it) },
                                onRepostPost = { viewModel.toggleRepost(it) },
                                onBookmarkPost = { viewModel.toggleBookmark(it) },
                                onDeletePost = { viewModel.deletePost(it) },
                                onReportPost = { viewModel.reportPost(it) },
                                onAdminPinPost = { viewModel.adminTogglePinPost(it) },
                                onOpenStory = { viewModel.openStoryViewer(it) },
                                onAddStory = { viewModel.openAddStoryDialog() },
                                onOpenCompose = { viewModel.openComposeDialog() },
                                onOpenAdminPanel = { viewModel.setTab(ScreenTab.ADMIN_PANEL) },
                                onOpenAuth = { viewModel.openAuthScreen() },
                                onRefresh = { viewModel.setFeedFilter(feedFilter) }
                            )
                        }

                        ScreenTab.EXPLORE -> {
                            ExploreScreen(
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                allPosts = allPosts,
                                users = allUsers,
                                trendingTopics = viewModel.trendingTopics,
                                onToggleFollow = { viewModel.toggleFollow(it) },
                                onLikePost = { viewModel.toggleLike(it) },
                                onCommentPost = { viewModel.openCommentsSheet(it) },
                                onRepostPost = { viewModel.toggleRepost(it) },
                                onBookmarkPost = { viewModel.toggleBookmark(it) },
                                onDeletePost = { viewModel.deletePost(it) },
                                onReportPost = { viewModel.reportPost(it) },
                                isAdmin = isAdmin
                            )
                        }

                        ScreenTab.CHATS -> {
                            ChatsScreen(
                                conversations = conversations,
                                activeConversation = activeConversation,
                                messages = messagesForActiveConversation,
                                onSelectConversation = { viewModel.openConversation(it) },
                                onBackToList = { viewModel.closeConversation() },
                                onSendMessage = { viewModel.sendMessage(it) }
                            )
                        }

                        ScreenTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                onClearAll = { viewModel.clearAllNotifications() }
                            )
                        }

                        ScreenTab.PROFILE -> {
                            ProfileScreen(
                                user = currentUser,
                                allPosts = allPosts,
                                onOpenEditProfile = { viewModel.openEditProfileDialog() },
                                onOpenAdminPanel = { viewModel.setTab(ScreenTab.ADMIN_PANEL) },
                                onOpenAuth = { viewModel.openAuthScreen() },
                                onLogout = { viewModel.logout() },
                                onLikePost = { viewModel.toggleLike(it) },
                                onCommentPost = { viewModel.openCommentsSheet(it) },
                                onRepostPost = { viewModel.toggleRepost(it) },
                                onBookmarkPost = { viewModel.toggleBookmark(it) },
                                onDeletePost = { viewModel.deletePost(it) },
                                onReportPost = { viewModel.reportPost(it) },
                                onAdminPinPost = { viewModel.adminTogglePinPost(it) }
                            )
                        }

                        ScreenTab.ADMIN_PANEL -> {
                            BackHandler(enabled = true) {
                                viewModel.setTab(ScreenTab.PROFILE)
                            }
                            AdminPanelScreen(
                                currentUser = currentUser,
                                metrics = platformMetrics,
                                users = allUsers,
                                posts = allPosts,
                                logs = adminLogs,
                                announcements = announcements,
                                onBack = { viewModel.setTab(ScreenTab.PROFILE) },
                                onToggleBanUser = { viewModel.adminToggleBanUser(it) },
                                onToggleVerifyUser = { viewModel.adminToggleVerifyUser(it) },
                                onToggleUserRole = { viewModel.adminToggleUserRole(it) },
                                onDeleteUser = { viewModel.adminDeleteUser(it) },
                                onTogglePinPost = { viewModel.adminTogglePinPost(it) },
                                onDeletePost = { viewModel.adminDeletePost(it) },
                                onDismissReports = { viewModel.adminDismissReports(it) },
                                onBroadcast = { title, msg -> viewModel.adminBroadcast(title, msg) },
                                onDismissAnnouncement = { viewModel.adminDismissAnnouncement(it) },
                                onResetDatabase = { viewModel.adminResetDatabase() },
                                onRefreshMetrics = { viewModel.refreshMetrics() }
                            )
                        }
                    }
                }
            }
        }

        // Active Bottom Sheet & Dialog Overlays
        activeCommentPost?.let { post ->
            CommentsSheet(
                post = post,
                comments = commentsForActivePost,
                onDismiss = { viewModel.closeCommentsSheet() },
                onAddComment = { viewModel.addComment(it) },
                onLikeComment = { viewModel.toggleLikeComment(it) }
            )
        }

        activeStory?.let { story ->
            StoryViewerDialog(
                story = story,
                onDismiss = { viewModel.closeStoryViewer() },
                onReply = { authorId, msg -> viewModel.replyToStory(authorId, msg) },
                onNext = { viewModel.nextStory() },
                onPrev = { viewModel.prevStory() }
            )
        }

        if (showComposeDialog) {
            ComposeDialog(
                onDismiss = { viewModel.closeComposeDialog() },
                onSubmit = { text, imageUrl -> viewModel.createPost(text, imageUrl) }
            )
        }

        if (showAddStoryDialog) {
            AddStoryDialog(
                onDismiss = { viewModel.closeAddStoryDialog() },
                onSubmit = { text, gradientIndex -> viewModel.createStory(text, gradientIndex) }
            )
        }

        if (showEditProfileDialog && currentUser != null) {
            EditProfileDialog(
                user = currentUser!!,
                onDismiss = { viewModel.closeEditProfileDialog() },
                onSave = { name, handle, bio -> viewModel.updateProfile(name, handle, bio) }
            )
        }

        if (showAuthScreen) {
            AuthScreen(
                onDismiss = { viewModel.closeAuthScreen() },
                onLogin = { email, pass, phone, onResult ->
                    viewModel.login(email, pass, phone, onResult)
                },
                onRegister = { name, handle, email, phone, pass, onResult ->
                    viewModel.register(name, handle, email, phone, pass, onResult)
                },
                onQuickAdminLogin = {
                    viewModel.quickAdminLogin()
                }
            )
        }
    }
}

@Composable
fun CBottomNavigationBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    onComposeClick: () -> Unit,
    unreadNotifs: Int,
    unreadMessages: Int,
    isAdmin: Boolean = false,
    currentUserLetter: String = "C",
    currentUserColorIndex: Int = 0
) {
    NavigationBar(
        containerColor = SurfaceDark.copy(alpha = 0.98f),
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        windowInsets = NavigationBarDefaults.windowInsets,
        modifier = Modifier.border(width = 0.5.dp, color = BorderLight)
    ) {
        // Home
        NavigationBarItem(
            selected = currentTab == ScreenTab.HOME,
            onClick = { onTabSelected(ScreenTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = AccentBlue.copy(alpha = 0.15f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        // Explore
        NavigationBarItem(
            selected = currentTab == ScreenTab.EXPLORE,
            onClick = { onTabSelected(ScreenTab.EXPLORE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = "Explore"
                )
            },
            label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = AccentBlue.copy(alpha = 0.15f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        // Center Compose FAB Button
        NavigationBarItem(
            selected = false,
            onClick = onComposeClick,
            icon = {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AccentBlue, AccentPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Post",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            label = null,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        // Chats
        NavigationBarItem(
            selected = currentTab == ScreenTab.CHATS,
            onClick = { onTabSelected(ScreenTab.CHATS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadMessages > 0) {
                            Badge(containerColor = AccentBlue, contentColor = Color.Black) {
                                Text("$unreadMessages", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Chats"
                    )
                }
            },
            label = { Text("Chats", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = AccentBlue.copy(alpha = 0.15f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        // Profile / Admin
        NavigationBarItem(
            selected = currentTab == ScreenTab.PROFILE || currentTab == ScreenTab.ADMIN_PANEL,
            onClick = { onTabSelected(ScreenTab.PROFILE) },
            icon = {
                BadgedBox(
                    badge = {
                        if (isAdmin) {
                            Badge(containerColor = StatusGold, contentColor = Color.Black) {
                                Text("ROOT", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (unreadNotifs > 0) {
                            Badge(containerColor = StatusRed, contentColor = Color.White) {
                                Text("$unreadNotifs", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    AvatarBadge(
                        letter = currentUserLetter,
                        colorIndex = currentUserColorIndex,
                        size = 24.dp,
                        fontSize = 10
                    )
                }
            },
            label = { Text(if (isAdmin) "Admin" else "Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = if (isAdmin) StatusGold else AccentBlue,
                selectedTextColor = if (isAdmin) StatusGold else AccentBlue,
                indicatorColor = if (isAdmin) StatusGold.copy(alpha = 0.15f) else AccentBlue.copy(alpha = 0.15f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
    }
}

@Composable
fun CNavigationRail(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    onComposeClick: () -> Unit,
    unreadNotifs: Int,
    unreadMessages: Int,
    isAdmin: Boolean = false
) {
    NavigationRail(
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        header = {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(AccentBlue, AccentPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "C", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
            }
        },
        modifier = Modifier.border(width = 0.5.dp, color = BorderLight)
    ) {
        NavigationRailItem(
            selected = currentTab == ScreenTab.HOME,
            onClick = { onTabSelected(ScreenTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = AccentBlue, unselectedIconColor = TextMuted)
        )

        NavigationRailItem(
            selected = currentTab == ScreenTab.EXPLORE,
            onClick = { onTabSelected(ScreenTab.EXPLORE) },
            icon = { Icon(Icons.Filled.Explore, contentDescription = "Explore") },
            label = { Text("Explore", fontSize = 11.sp) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = AccentBlue, unselectedIconColor = TextMuted)
        )

        NavigationRailItem(
            selected = currentTab == ScreenTab.CHATS,
            onClick = { onTabSelected(ScreenTab.CHATS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadMessages > 0) {
                            Badge(containerColor = AccentBlue, contentColor = Color.Black) {
                                Text("$unreadMessages", fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Chats")
                }
            },
            label = { Text("Chats", fontSize = 11.sp) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = AccentBlue, unselectedIconColor = TextMuted)
        )

        NavigationRailItem(
            selected = currentTab == ScreenTab.NOTIFICATIONS,
            onClick = { onTabSelected(ScreenTab.NOTIFICATIONS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadNotifs > 0) {
                            Badge(containerColor = StatusRed, contentColor = Color.White) {
                                Text("$unreadNotifs", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                }
            },
            label = { Text("Alerts", fontSize = 11.sp) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = AccentBlue, unselectedIconColor = TextMuted)
        )

        NavigationRailItem(
            selected = currentTab == ScreenTab.PROFILE,
            onClick = { onTabSelected(ScreenTab.PROFILE) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = AccentBlue, unselectedIconColor = TextMuted)
        )

        if (isAdmin) {
            NavigationRailItem(
                selected = currentTab == ScreenTab.ADMIN_PANEL,
                onClick = { onTabSelected(ScreenTab.ADMIN_PANEL) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Console") },
                label = { Text("Admin", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(selectedIconColor = StatusGold, unselectedIconColor = StatusGold.copy(alpha = 0.7f))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(AccentBlue)
                .clickable(onClick = onComposeClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Post", tint = Color.Black)
        }
    }
}
