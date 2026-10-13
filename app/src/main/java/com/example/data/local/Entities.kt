package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val handle: String,
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val avatarLetter: String,
    val avatarColorIndex: Int = 0,
    val verified: Boolean = false,
    val following: Boolean = false,
    val bio: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val role: String = "USER", // "ADMIN", "MODERATOR", "USER"
    val isBanned: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val userId: Long,
    val text: String,
    val timestamp: String = "now",
    val likes: Int = 0,
    val liked: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val text: String,
    val gradientIndex: Int = 0,
    val seen: Boolean = false,
    val timestamp: String = "2h",
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: Long,
    val participantId: Long,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val lastUpdatedEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val senderId: Long,
    val text: String,
    val timestamp: String,
    val isMe: Boolean = false,
    val sentAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val userId: Long,
    val text: String,
    val subText: String = "",
    val timestamp: String,
    val isRead: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminName: String,
    val action: String,
    val target: String,
    val timestamp: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: String,
    val isActive: Boolean = true,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
