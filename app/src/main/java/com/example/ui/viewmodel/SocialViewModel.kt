package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME, EXPLORE, CHATS, NOTIFICATIONS, PROFILE, ADMIN_PANEL
}

enum class FeedFilter {
    FOR_YOU, FOLLOWING
}

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SocialRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = SocialRepository(database.socialDao())
    }

    // Active Screen & Navigation
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _feedFilter = MutableStateFlow(FeedFilter.FOR_YOU)
    val feedFilter: StateFlow<FeedFilter> = _feedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Current Session: default to Admin Clinton Churchill
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = 0L,
            name = "Clinton Churchill",
            handle = "clinton_admin",
            email = "clintonchurchill2@gmail.com",
            phone = "+1 (555) 019-2834",
            avatarLetter = "C",
            avatarColorIndex = 0,
            verified = true,
            following = false,
            bio = "Founder & Lead System Administrator @ C. Overseeing core architecture.",
            followersCount = 54200,
            followingCount = 120,
            role = "ADMIN",
            isBanned = false
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isAdmin: StateFlow<Boolean> = _currentUser.map {
        it?.isAdmin == true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isAuthenticated: StateFlow<Boolean> = _currentUser.map {
        it != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Dialog & Overlay States
    private val _showAuthScreen = MutableStateFlow(false)
    val showAuthScreen: StateFlow<Boolean> = _showAuthScreen.asStateFlow()

    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    private val _activeConversation = MutableStateFlow<Conversation?>(null)
    val activeConversation: StateFlow<Conversation?> = _activeConversation.asStateFlow()

    private val _showComposeDialog = MutableStateFlow(false)
    val showComposeDialog: StateFlow<Boolean> = _showComposeDialog.asStateFlow()

    private val _showAddStoryDialog = MutableStateFlow(false)
    val showAddStoryDialog: StateFlow<Boolean> = _showAddStoryDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    // SnackBar / Toast feedback
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Repository Flows
    val allPosts: StateFlow<List<Post>> = repository.getAllPostsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<Story>> = repository.getStoriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<Conversation>> = repository.getConversationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.getNotificationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unreadMessagesCount: StateFlow<Int> = conversations.map { list ->
        list.sumOf { it.unreadCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminLogs: StateFlow<List<AdminLog>> = repository.getAdminLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<SystemAnnouncement>> = repository.getAnnouncementsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceMode: StateFlow<Boolean> = repository.maintenanceMode
    val registrationsOpen: StateFlow<Boolean> = repository.registrationsOpen

    private val _platformMetrics = MutableStateFlow(PlatformMetrics())
    val platformMetrics: StateFlow<PlatformMetrics> = _platformMetrics.asStateFlow()

    init {
        refreshMetrics()
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            _platformMetrics.value = repository.getPlatformMetrics()
        }
    }

    // Filtered Feed
    val feedPosts: StateFlow<List<Post>> = combine(allPosts, feedFilter) { posts, filter ->
        when (filter) {
            FeedFilter.FOR_YOU -> posts
            FeedFilter.FOLLOWING -> posts.filter { it.author?.following == true }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Comments Flow for bottom sheet
    private val _commentsForActivePost = MutableStateFlow<List<Comment>>(emptyList())
    val commentsForActivePost: StateFlow<List<Comment>> = _commentsForActivePost.asStateFlow()

    // Active Messages Flow for chat screen
    private val _messagesForActiveConversation = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messagesForActiveConversation: StateFlow<List<ChatMessage>> = _messagesForActiveConversation.asStateFlow()

    // Trending list
    val trendingTopics = listOf(
        TrendingTopic("Technology", "#AIRevolution", "142K posts"),
        TrendingTopic("Design", "#UIInspiration", "89.4K posts"),
        TrendingTopic("Trending", "#BuildInPublic", "67.1K posts"),
        TrendingTopic("Security", "#CyberShield", "62.4K posts"),
        TrendingTopic("Finance", "#Web3", "54.2K posts"),
        TrendingTopic("Culture", "#CreatorEconomy", "41.8K posts")
    )

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
        if (tab == ScreenTab.ADMIN_PANEL) {
            refreshMetrics()
        }
    }

    fun setFeedFilter(filter: FeedFilter) {
        _feedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // AUTH CONTROLS
    fun openAuthScreen() {
        _showAuthScreen.value = true
    }

    fun closeAuthScreen() {
        _showAuthScreen.value = false
    }

    fun login(email: String, password: String, phone: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.authenticate(email, password)
            if (user != null) {
                _currentUser.value = user
                _showAuthScreen.value = false
                refreshMetrics()
                val roleMsg = if (user.isAdmin) "Welcome back Admin Clinton! Shield active." else "Signed in successfully!"
                _toastEvent.emit(roleMsg)
                onResult(true, roleMsg)
            } else {
                onResult(false, "Invalid credentials or account suspended.")
            }
        }
    }

    fun quickAdminLogin() {
        login("clintonchurchill2@gmail.com", "###password::", "+1 (555) 019-2834") { success, _ ->
            if (success) {
                setTab(ScreenTab.ADMIN_PANEL)
            }
        }
    }

    fun register(
        name: String,
        handle: String,
        email: String,
        phone: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.register(name, handle, email, phone, password)
            if (user != null) {
                _currentUser.value = user
                _showAuthScreen.value = false
                refreshMetrics()
                _toastEvent.emit("Account created! Welcome to C, ${user.name}.")
                onResult(true, "Registration complete!")
            } else {
                onResult(false, "Email already in use.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentTab.value = ScreenTab.HOME
        _showAuthScreen.value = true
        viewModelScope.launch {
            _toastEvent.emit("Logged out. Guest mode.")
        }
    }

    // DIALOG CONTROLS
    fun openComposeDialog() {
        _showComposeDialog.value = true
    }

    fun closeComposeDialog() {
        _showComposeDialog.value = false
    }

    fun openAddStoryDialog() {
        _showAddStoryDialog.value = true
    }

    fun closeAddStoryDialog() {
        _showAddStoryDialog.value = false
    }

    fun openEditProfileDialog() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfileDialog() {
        _showEditProfileDialog.value = false
    }

    fun openCommentsSheet(post: Post) {
        _activeCommentPost.value = post
        viewModelScope.launch {
            repository.getCommentsForPostFlow(post.id).collect {
                _commentsForActivePost.value = it
            }
        }
    }

    fun closeCommentsSheet() {
        _activeCommentPost.value = null
        _commentsForActivePost.value = emptyList()
    }

    fun openStoryViewer(story: Story) {
        _activeStory.value = story
        viewModelScope.launch {
            repository.markStorySeen(story.id)
        }
    }

    fun closeStoryViewer() {
        _activeStory.value = null
    }

    fun nextStory() {
        val current = _activeStory.value ?: return
        val list = stories.value
        val idx = list.indexOfFirst { it.id == current.id }
        if (idx in 0 until list.size - 1) {
            openStoryViewer(list[idx + 1])
        } else {
            closeStoryViewer()
        }
    }

    fun prevStory() {
        val current = _activeStory.value ?: return
        val list = stories.value
        val idx = list.indexOfFirst { it.id == current.id }
        if (idx > 0) {
            openStoryViewer(list[idx - 1])
        }
    }

    fun openConversation(conversation: Conversation) {
        _activeConversation.value = conversation
        viewModelScope.launch {
            repository.markConversationRead(conversation.id)
            repository.getMessagesForConversationFlow(conversation.id).collect {
                _messagesForActiveConversation.value = it
            }
        }
    }

    fun closeConversation() {
        _activeConversation.value = null
        _messagesForActiveConversation.value = emptyList()
    }

    // POST & SOCIAL ACTIONS
    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
            refreshMetrics()
        }
    }

    fun toggleRepost(postId: Long) {
        viewModelScope.launch {
            val post = allPosts.value.find { it.id == postId }
            repository.toggleRepostPost(postId)
            val isNowReposted = !(post?.reposted ?: false)
            _toastEvent.emit(if (isNowReposted) "Reposted ✓" else "Repost removed")
            refreshMetrics()
        }
    }

    fun toggleBookmark(postId: Long) {
        viewModelScope.launch {
            val post = allPosts.value.find { it.id == postId }
            repository.toggleBookmarkPost(postId)
            val isNowSaved = !(post?.bookmarked ?: false)
            _toastEvent.emit(if (isNowSaved) "Saved to Bookmarks 🔖" else "Removed from Bookmarks")
        }
    }

    fun createPost(text: String, imageUrl: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(user.id, text, imageUrl)
            _toastEvent.emit("Posted! 🚀")
            refreshMetrics()
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            _toastEvent.emit("Post deleted")
            refreshMetrics()
        }
    }

    fun reportPost(postId: Long) {
        viewModelScope.launch {
            repository.reportPost(postId)
            _toastEvent.emit("Post flagged for moderation review 🛡️")
            refreshMetrics()
        }
    }

    fun toggleFollow(userId: Long) {
        viewModelScope.launch {
            val user = allUsers.value.find { it.id == userId }
            repository.toggleFollowUser(userId)
            val isFollowing = !(user?.following ?: false)
            _toastEvent.emit(if (isFollowing) "Following @${user?.handle}" else "Unfollowed @${user?.handle}")
        }
    }

    fun addComment(text: String) {
        val post = _activeCommentPost.value ?: return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addComment(user.id, post.id, text)
            _toastEvent.emit("Reply posted! 💬")
            refreshMetrics()
        }
    }

    fun toggleLikeComment(comment: Comment) {
        viewModelScope.launch {
            repository.toggleLikeComment(comment.id, comment.liked, comment.likes)
        }
    }

    fun createStory(text: String, gradientIndex: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createStory(user.id, text, gradientIndex)
            _toastEvent.emit("Story shared! ✨")
        }
    }

    fun sendMessage(text: String) {
        val conv = _activeConversation.value ?: return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage(user.id, conv.id, text)
        }
    }

    fun replyToStory(authorId: Long, message: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val conv = conversations.value.find { it.participantId == authorId }
            if (conv != null) {
                repository.sendMessage(user.id, conv.id, message)
            } else {
                _toastEvent.emit("Sent: $message")
            }
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            _toastEvent.emit("All notifications marked as read")
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _toastEvent.emit("Notifications cleared")
        }
    }

    fun updateProfile(name: String, handle: String, bio: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateCurrentUser(user.id, name, handle, bio)
            _currentUser.value = user.copy(name = name, handle = handle, bio = bio)
            _toastEvent.emit("Profile updated! ✓")
        }
    }

    // ==========================================
    // ADMIN EXCLUSIVE ACTIONS
    // ==========================================
    fun adminToggleBanUser(userId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val isBanned = repository.adminToggleBan(admin.name, userId)
            _toastEvent.emit(if (isBanned) "User has been banned 🚫" else "User unbanned ✓")
            refreshMetrics()
        }
    }

    fun adminToggleVerifyUser(userId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val isVerified = repository.adminToggleVerify(admin.name, userId)
            _toastEvent.emit(if (isVerified) "Granted Verified Badge ⭐" else "Revoked Verified Badge")
        }
    }

    fun adminToggleUserRole(userId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val newRole = repository.adminToggleRole(admin.name, userId)
            _toastEvent.emit("Role updated to $newRole")
        }
    }

    fun adminDeleteUser(userId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminDeleteUser(admin.name, userId)
            _toastEvent.emit("User permanently deleted 🗑️")
            refreshMetrics()
        }
    }

    fun adminTogglePinPost(postId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val isPinned = repository.adminTogglePinPost(admin.name, postId)
            _toastEvent.emit(if (isPinned) "Post Pinned to Global Feed 📌" else "Post Unpinned")
        }
    }

    fun adminDeletePost(postId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminDeletePost(admin.name, postId)
            _toastEvent.emit("Post removed by Admin Moderation 🛡️")
            refreshMetrics()
        }
    }

    fun adminDismissReports(postId: Long) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminDismissReports(admin.name, postId)
            _toastEvent.emit("Reports cleared for post #$postId")
            refreshMetrics()
        }
    }

    fun adminBroadcast(title: String, message: String) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminBroadcast(admin.name, title, message)
            _toastEvent.emit("Global Announcement Broadcasted! 📢")
        }
    }

    fun adminDismissAnnouncement(announcementId: Long) {
        viewModelScope.launch {
            repository.adminDismissAnnouncement(announcementId)
            _toastEvent.emit("Announcement dismissed")
        }
    }

    fun adminSendWarning(userId: Long, warningText: String) {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminSendWarning(admin.name, userId, warningText)
            _toastEvent.emit("Official Warning Dispatched! ⚠️")
        }
    }

    fun adminPurgeFlaggedPosts() {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val count = repository.adminPurgeFlaggedPosts(admin.name)
            _toastEvent.emit("Purged $count flagged posts 🧹")
            refreshMetrics()
        }
    }

    fun adminToggleMaintenanceMode() {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val active = repository.toggleMaintenanceMode(admin.name)
            _toastEvent.emit(if (active) "Maintenance Mode ACTIVATED 🚨" else "System Normal: Maintenance OFF ✓")
        }
    }

    fun adminToggleRegistrationsOpen() {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            val open = repository.toggleRegistrationsOpen(admin.name)
            _toastEvent.emit(if (open) "User Registrations OPEN ✓" else "User Registrations CLOSED 🔒")
        }
    }

    fun adminResetDatabase() {
        val admin = _currentUser.value ?: return
        if (!admin.isAdmin) return
        viewModelScope.launch {
            repository.adminResetDatabase(admin.name)
            refreshMetrics()
            _toastEvent.emit("System database reset and re-seeded! 🔄")
        }
    }
}
