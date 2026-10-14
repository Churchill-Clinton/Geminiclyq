package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // USERS
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: Long): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET following = :following WHERE id = :userId")
    suspend fun updateUserFollowing(userId: Long, following: Boolean)

    @Query("UPDATE users SET verified = :verified WHERE id = :userId")
    suspend fun updateUserVerified(userId: Long, verified: Boolean)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun updateUserBanStatus(userId: Long, isBanned: Boolean)

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: Long, role: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    // POSTS
    @Query("SELECT * FROM posts ORDER BY isPinned DESC, createdAtEpoch DESC")
    fun getAllPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    @Query("UPDATE posts SET liked = :liked, likes = :likes WHERE id = :postId")
    suspend fun updatePostLike(postId: Long, liked: Boolean, likes: Int)

    @Query("UPDATE posts SET reposted = :reposted, reposts = :reposts WHERE id = :postId")
    suspend fun updatePostRepost(postId: Long, reposted: Boolean, reposts: Int)

    @Query("UPDATE posts SET bookmarked = :bookmarked WHERE id = :postId")
    suspend fun updatePostBookmark(postId: Long, bookmarked: Boolean)

    @Query("UPDATE posts SET comments = comments + 1 WHERE id = :postId")
    suspend fun incrementPostCommentCount(postId: Long)

    @Query("UPDATE posts SET isPinned = :isPinned WHERE id = :postId")
    suspend fun updatePostPinned(postId: Long, isPinned: Boolean)

    @Query("UPDATE posts SET isFlagged = :isFlagged, reportCount = reportCount + 1 WHERE id = :postId")
    suspend fun reportPost(postId: Long, isFlagged: Boolean = true)

    @Query("UPDATE posts SET isFlagged = 0, reportCount = 0 WHERE id = :postId")
    suspend fun dismissPostReports(postId: Long)

    // COMMENTS
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAtEpoch ASC")
    fun getCommentsForPostFlow(postId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET liked = :liked, likes = :likes WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: Long, liked: Boolean, likes: Int)

    // STORIES
    @Query("SELECT * FROM stories ORDER BY createdAtEpoch DESC")
    fun getAllStoriesFlow(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Query("UPDATE stories SET seen = 1 WHERE id = :storyId")
    suspend fun markStorySeen(storyId: Long)

    // CONVERSATIONS
    @Query("SELECT * FROM conversations ORDER BY lastUpdatedEpoch DESC")
    fun getAllConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    suspend fun getConversationById(conversationId: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun markConversationRead(conversationId: Long)

    // MESSAGES
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY sentAtEpoch ASC")
    fun getMessagesForConversationFlow(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications ORDER BY createdAtEpoch DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // ADMIN LOGS
    @Query("SELECT * FROM admin_logs ORDER BY createdAtEpoch DESC LIMIT 50")
    fun getAllAdminLogsFlow(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminLog(log: AdminLogEntity)

    // ANNOUNCEMENTS
    @Query("SELECT * FROM announcements WHERE isActive = 1 ORDER BY createdAtEpoch DESC LIMIT 5")
    fun getActiveAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Query("UPDATE announcements SET isActive = 0 WHERE id = :announcementId")
    suspend fun dismissAnnouncement(announcementId: Long)

    // METRICS
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUsersCount(): Int

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Query("SELECT COUNT(*) FROM comments")
    suspend fun getTotalCommentsCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE isBanned = 1")
    suspend fun getBannedUsersCount(): Int

    @Query("SELECT COUNT(*) FROM posts WHERE isFlagged = 1")
    suspend fun getReportedPostsCount(): Int

    @Query("DELETE FROM posts WHERE isFlagged = 1")
    suspend fun purgeFlaggedPosts(): Int

    // RESET DB
    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("DELETE FROM comments")
    suspend fun clearAllComments()

    @Query("DELETE FROM stories")
    suspend fun clearAllStories()
}
