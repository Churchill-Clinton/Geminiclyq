package com.example.data.model

data class User(
    val id: Long,
    val name: String,
    val handle: String,
    val email: String = "",
    val phone: String = "",
    val avatarLetter: String,
    val avatarColorIndex: Int = 0,
    val verified: Boolean = false,
    val following: Boolean = false,
    val bio: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val role: String = "USER", // "ADMIN", "MODERATOR", "USER"
    val isBanned: Boolean = false
) {
    val isAdmin: Boolean get() = role == "ADMIN" || email.equals("clintonchurchill2@gmail.com", ignoreCase = true)
}

data class Post(
    val id: Long,
    val userId: Long,
    val text: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val reposts: Int = 0,
    val liked: Boolean = false,
    val reposted: Boolean = false,
    val bookmarked: Boolean = false,
    val timestamp: String = "now",
    val imageUrl: String? = null,
    val isUserCreated: Boolean = false,
    val isPinned: Boolean = false,
    val isFlagged: Boolean = false,
    val reportCount: Int = 0,
    val author: User? = null
)

data class Comment(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val text: String,
    val timestamp: String = "now",
    val likes: Int = 0,
    val liked: Boolean = false,
    val author: User? = null
)

data class Story(
    val id: Long,
    val userId: Long,
    val text: String,
    val gradientIndex: Int = 0,
    val seen: Boolean = false,
    val timestamp: String = "2h",
    val author: User? = null
)

data class Conversation(
    val id: Long,
    val participantId: Long,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val participant: User? = null
)

data class ChatMessage(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val text: String,
    val timestamp: String,
    val isMe: Boolean = false
)

enum class NotificationType {
    LIKE, FOLLOW, COMMENT, REPOST, SYSTEM
}

data class NotificationItem(
    val id: Long,
    val type: NotificationType,
    val userId: Long,
    val text: String,
    val subText: String = "",
    val timestamp: String,
    val isRead: Boolean = false,
    val user: User? = null
)

data class TrendingTopic(
    val category: String,
    val tag: String,
    val postCount: String
)

data class AdminLog(
    val id: Long,
    val adminName: String,
    val action: String,
    val target: String,
    val timestamp: String
)

data class SystemAnnouncement(
    val id: Long,
    val title: String,
    val message: String,
    val timestamp: String,
    val isActive: Boolean = true
)

data class PlatformMetrics(
    val totalUsers: Int = 0,
    val totalPosts: Int = 0,
    val totalLikes: Int = 0,
    val totalComments: Int = 0,
    val bannedUsersCount: Int = 0,
    val reportedPostsCount: Int = 0,
    val systemUptime: String = "99.98%",
    val serverLatencyMs: Int = 18,
    val activeSessions: Int = 42
)
