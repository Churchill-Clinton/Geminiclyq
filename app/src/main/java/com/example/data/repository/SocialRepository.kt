package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SocialRepository(private val dao: SocialDao) {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            ensureInitialized()
        }
    }

    suspend fun ensureInitialized() {
        if (dao.getPostCount() == 0) {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        val users = listOf(
            UserEntity(
                id = 0L,
                name = "Clinton Churchill",
                handle = "clinton_admin",
                email = "clintonchurchill2@gmail.com",
                phone = "+1 (555) 019-2834",
                password = "###password::",
                avatarLetter = "C",
                avatarColorIndex = 0,
                verified = true,
                following = false,
                bio = "Founder & Lead System Administrator @ C. Overseeing core architecture.",
                followersCount = 54200,
                followingCount = 120,
                role = "ADMIN",
                isBanned = false
            ),
            UserEntity(
                id = 1L,
                name = "Alex Rivera",
                handle = "alexrivera",
                email = "alex@c.social",
                phone = "+1 (555) 234-5678",
                password = "password123",
                avatarLetter = "A",
                avatarColorIndex = 1,
                verified = true,
                following = false,
                bio = "Designer & builder. Obsessed with interfaces that feel alive.",
                followersCount = 28400,
                followingCount = 612,
                role = "MODERATOR",
                isBanned = false
            ),
            UserEntity(
                id = 2L,
                name = "Jordan Kim",
                handle = "jordankim",
                email = "jordan@c.social",
                phone = "+1 (555) 345-6789",
                password = "password123",
                avatarLetter = "J",
                avatarColorIndex = 2,
                verified = false,
                following = true,
                bio = "Dev & coffee addict. Shipping daily on Android.",
                followersCount = 9820,
                followingCount = 421,
                role = "USER",
                isBanned = false
            ),
            UserEntity(
                id = 3L,
                name = "Mia Okafor",
                handle = "miaokafor",
                email = "mia@c.social",
                phone = "+1 (555) 456-7890",
                password = "password123",
                avatarLetter = "M",
                avatarColorIndex = 3,
                verified = true,
                following = false,
                bio = "Co-founder @ C. Connecting global creator networks.",
                followersCount = 89400,
                followingCount = 310,
                role = "MODERATOR",
                isBanned = false
            ),
            UserEntity(
                id = 4L,
                name = "Luca Brandt",
                handle = "lucabrandt",
                email = "luca@c.social",
                phone = "+1 (555) 567-8901",
                password = "password123",
                avatarLetter = "L",
                avatarColorIndex = 4,
                verified = false,
                following = false,
                bio = "Photographer & traveller. Chasing sunrise tones.",
                followersCount = 15300,
                followingCount = 590,
                role = "USER",
                isBanned = false
            ),
            UserEntity(
                id = 5L,
                name = "Sana Park",
                handle = "sanapark",
                email = "sana@c.social",
                phone = "+1 (555) 678-9012",
                password = "password123",
                avatarLetter = "S",
                avatarColorIndex = 5,
                verified = true,
                following = true,
                bio = "AI researcher & engineer. Working on cognitive models.",
                followersCount = 42100,
                followingCount = 188,
                role = "USER",
                isBanned = false
            ),
            UserEntity(
                id = 6L,
                name = "Omar Hassan",
                handle = "omarhassan",
                email = "omar@c.social",
                phone = "+1 (555) 789-0123",
                password = "password123",
                avatarLetter = "O",
                avatarColorIndex = 6,
                verified = false,
                following = false,
                bio = "Entrepreneur. Bootstrapped to $2M ARR. Always sharing learnings.",
                followersCount = 31500,
                followingCount = 740,
                role = "USER",
                isBanned = false
            )
        )
        dao.insertUsers(users)

        val now = System.currentTimeMillis()
        val posts = listOf(
            PostEntity(
                id = 100L,
                userId = 3L,
                text = "Just launched the new C platform 🚀\n\nThis is where the world comes to connect. Real conversations. Real people. No noise.\n\n#C #SocialMedia #Launch",
                likes = 2841,
                comments = 312,
                reposts = 847,
                liked = false,
                reposted = false,
                bookmarked = false,
                timestamp = "2m",
                imageUrl = null,
                isUserCreated = false,
                isPinned = true,
                createdAtEpoch = now - 120_000
            ),
            PostEntity(
                id = 101L,
                userId = 1L,
                text = "UI tip: Your bottom nav icons should feel *familiar* but carry your brand's personality.\n\nHome = filled house for active state\nChat = speech bubble with presence dot\n\nSmall details = big trust. #Design #UX",
                likes = 1204,
                comments = 89,
                reposts = 331,
                liked = false,
                reposted = false,
                bookmarked = false,
                timestamp = "18m",
                imageUrl = null,
                isUserCreated = false,
                createdAtEpoch = now - 1_080_000
            ),
            PostEntity(
                id = 102L,
                userId = 5L,
                text = "The next wave of AI won't replace you — it'll amplify you.\n\nBut only if you learn to use it like a tool, not a crutch.\n\n#AI #FutureOfWork",
                likes = 5722,
                comments = 634,
                reposts = 1890,
                liked = true,
                reposted = false,
                bookmarked = true,
                timestamp = "47m",
                imageUrl = null,
                isUserCreated = false,
                createdAtEpoch = now - 2_820_000
            ),
            PostEntity(
                id = 103L,
                userId = 2L,
                text = "Morning routine update:\n\n☑ Cold shower\n☑ 30 min code\n☑ Post on C\n\nConsistency over intensity every single time 🧠 #Productivity",
                likes = 418,
                comments = 52,
                reposts = 97,
                liked = false,
                reposted = false,
                bookmarked = false,
                timestamp = "1h",
                imageUrl = null,
                isUserCreated = false,
                createdAtEpoch = now - 3_600_000
            ),
            PostEntity(
                id = 104L,
                userId = 4L,
                text = "Shot this at 5:43 AM. Worth every second of lost sleep. 📸\n\n#Photography #GoldenHour #Atmosphere",
                likes = 9301,
                comments = 441,
                reposts = 2103,
                liked = true,
                reposted = true,
                bookmarked = true,
                timestamp = "2h",
                imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&q=80",
                isUserCreated = false,
                createdAtEpoch = now - 7_200_000
            ),
            PostEntity(
                id = 105L,
                userId = 6L,
                text = "Unpopular opinion: Most startups fail not because of bad products — but because founders stop talking to customers after month 3.\n\nStay close to the problem. Always. #Startups",
                likes = 3388,
                comments = 287,
                reposts = 1022,
                liked = false,
                reposted = false,
                bookmarked = false,
                timestamp = "3h",
                imageUrl = null,
                isUserCreated = false,
                createdAtEpoch = now - 10_800_000
            ),
            PostEntity(
                id = 106L,
                userId = 1L,
                text = "Side project update: 6 months in, 0 marketing budget, 12k users.\n\nHow? Just built something people genuinely wanted and let them tell others.\n\nWord of mouth > any ad spend. #Startup #BuildInPublic",
                likes = 7140,
                comments = 523,
                reposts = 1874,
                liked = false,
                reposted = false,
                bookmarked = false,
                timestamp = "5h",
                imageUrl = null,
                isUserCreated = false,
                createdAtEpoch = now - 18_000_000
            )
        )
        dao.insertPosts(posts)

        // Seed Comments
        val comments = listOf(
            CommentEntity(postId = 100L, userId = 1L, text = "Phenomenal release! The UX feels incredibly fluid.", timestamp = "1m", likes = 24),
            CommentEntity(postId = 100L, userId = 5L, text = "Congratulations team! Honored to be part of the launch community.", timestamp = "1m", likes = 18),
            CommentEntity(postId = 101L, userId = 2L, text = "100% agree. Consistent micro-interactions create an unmatched rhythm.", timestamp = "15m", likes = 12),
            CommentEntity(postId = 102L, userId = 6L, text = "Spot on Sana. The future belongs to those who leverage leverage.", timestamp = "35m", likes = 32),
            CommentEntity(postId = 104L, userId = 3L, text = "Those light streaks are majestic Luca!", timestamp = "1h", likes = 45)
        )
        dao.insertComments(comments)

        // Seed Stories
        val stories = listOf(
            StoryEntity(userId = 3L, text = "Behind the scenes of C's architecture meeting! 🚀", gradientIndex = 0, seen = false, timestamp = "10m"),
            StoryEntity(userId = 1L, text = "Designing new micro-animations today ✨", gradientIndex = 1, seen = false, timestamp = "45m"),
            StoryEntity(userId = 5L, text = "Reading new papers on diffusion distillation 🧠", gradientIndex = 2, seen = false, timestamp = "1h"),
            StoryEntity(userId = 4L, text = "Alpine lakes at sunrise. Pure magic 🏔️", gradientIndex = 3, seen = true, timestamp = "3h"),
            StoryEntity(userId = 2L, text = "Coffee batch brewed, code flowing smoothly ☕", gradientIndex = 4, seen = true, timestamp = "4h"),
            StoryEntity(userId = 6L, text = "Founder dinner in SF tonight! Who's in town?", gradientIndex = 5, seen = true, timestamp = "6h")
        )
        dao.insertStories(stories)

        // Seed Conversations & Messages
        val conversations = listOf(
            ConversationEntity(id = 1L, participantId = 3L, lastMessage = "Welcome to C! Let me know if you need any feedback.", timestamp = "now", unreadCount = 1, isOnline = true),
            ConversationEntity(id = 2L, participantId = 1L, lastMessage = "Hey, love your latest post! Want to collaborate on a design system?", timestamp = "3m", unreadCount = 1, isOnline = true),
            ConversationEntity(id = 3L, participantId = 5L, lastMessage = "Did you see the AI benchmark released this morning?", timestamp = "22m", unreadCount = 0, isOnline = false),
            ConversationEntity(id = 4L, participantId = 2L, lastMessage = "Let's build that prototype tomorrow 🔥", timestamp = "1h", unreadCount = 0, isOnline = true),
            ConversationEntity(id = 5L, participantId = 4L, lastMessage = "Thanks for the feedback on that sunrise shot!", timestamp = "2h", unreadCount = 0, isOnline = false)
        )
        dao.insertConversations(conversations)

        val messages = listOf(
            ChatMessageEntity(conversationId = 1L, senderId = 3L, text = "Hey there! Welcome to the C platform 🎉", timestamp = "10:15 AM", isMe = false),
            ChatMessageEntity(conversationId = 1L, senderId = 0L, text = "Thanks Mia! The UI aesthetic is stunning.", timestamp = "10:16 AM", isMe = true),
            ChatMessageEntity(conversationId = 1L, senderId = 3L, text = "Welcome to C! Let me know if you need any feedback.", timestamp = "10:18 AM", isMe = false),

            ChatMessageEntity(conversationId = 2L, senderId = 1L, text = "Hey! Saw your recent thoughts on interface design.", timestamp = "10:00 AM", isMe = false),
            ChatMessageEntity(conversationId = 2L, senderId = 0L, text = "Hey Alex! Big fan of your work.", timestamp = "10:05 AM", isMe = true),
            ChatMessageEntity(conversationId = 2L, senderId = 1L, text = "Hey, love your latest post! Want to collaborate on a design system?", timestamp = "10:10 AM", isMe = false),

            ChatMessageEntity(conversationId = 3L, senderId = 5L, text = "Did you see the AI benchmark released this morning?", timestamp = "9:30 AM", isMe = false),
            ChatMessageEntity(conversationId = 4L, senderId = 2L, text = "Let's build that prototype tomorrow 🔥", timestamp = "Yesterday", isMe = false),
            ChatMessageEntity(conversationId = 5L, senderId = 4L, text = "Thanks for the feedback on that sunrise shot!", timestamp = "Yesterday", isMe = false)
        )
        dao.insertMessages(messages)

        // Seed Notifications
        val notifications = listOf(
            NotificationEntity(type = "like", userId = 1L, text = "liked your post", subText = "\"UI tip: Your bottom nav...\"", timestamp = "just now", isRead = false),
            NotificationEntity(type = "follow", userId = 5L, text = "started following you", subText = "", timestamp = "4m", isRead = false),
            NotificationEntity(type = "comment", userId = 3L, text = "commented on your post", subText = "\"This is exactly right! 💯\"", timestamp = "12m", isRead = false),
            NotificationEntity(type = "repost", userId = 6L, text = "reposted your post", subText = "\"Consistency over intensity...\"", timestamp = "34m", isRead = false),
            NotificationEntity(type = "system", userId = 0L, text = "Security clearance verified. Full Admin console unlocked.", subText = "", timestamp = "5m", isRead = false)
        )
        dao.insertNotifications(notifications)

        // Seed Admin Logs
        val logs = listOf(
            AdminLogEntity(adminName = "Clinton Churchill", action = "System Initialized", target = "Global Node 01", timestamp = "Just now"),
            AdminLogEntity(adminName = "Clinton Churchill", action = "Security Protocol", target = "2FA & Auth Active", timestamp = "1h ago"),
            AdminLogEntity(adminName = "System Sentinel", action = "Moderation Engine", target = "Heuristics V2 Loaded", timestamp = "2h ago")
        )
        logs.forEach { dao.insertAdminLog(it) }

        // Seed System Announcement
        dao.insertAnnouncement(
            AnnouncementEntity(
                title = "C Global Release 2.0 • Admin Command Online",
                message = "The C platform is operating at peak performance with full administrative supervision, real-time message relays, and encrypted auth.",
                timestamp = "Official",
                isActive = true
            )
        )
    }

    // USER FLOWS
    fun getAllPostsFlow(): Flow<List<Post>> {
        return combine(dao.getAllPostsFlow(), dao.getAllUsersFlow()) { postEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            postEntities.map { entity ->
                val authorEntity = userMap[entity.userId]
                val author = authorEntity?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        handle = it.handle,
                        email = it.email,
                        phone = it.phone,
                        avatarLetter = it.avatarLetter,
                        avatarColorIndex = it.avatarColorIndex,
                        verified = it.verified,
                        following = it.following,
                        bio = it.bio,
                        followersCount = it.followersCount,
                        followingCount = it.followingCount,
                        role = it.role,
                        isBanned = it.isBanned
                    )
                }
                Post(
                    id = entity.id,
                    userId = entity.userId,
                    text = entity.text,
                    likes = entity.likes,
                    comments = entity.comments,
                    reposts = entity.reposts,
                    liked = entity.liked,
                    reposted = entity.reposted,
                    bookmarked = entity.bookmarked,
                    timestamp = entity.timestamp,
                    imageUrl = entity.imageUrl,
                    isUserCreated = entity.isUserCreated,
                    isPinned = entity.isPinned,
                    isFlagged = entity.isFlagged,
                    reportCount = entity.reportCount,
                    author = author
                )
            }
        }
    }

    fun getAllUsersFlow(): Flow<List<User>> {
        return dao.getAllUsersFlow().map { list ->
            list.map {
                User(
                    id = it.id,
                    name = it.name,
                    handle = it.handle,
                    email = it.email,
                    phone = it.phone,
                    avatarLetter = it.avatarLetter,
                    avatarColorIndex = it.avatarColorIndex,
                    verified = it.verified,
                    following = it.following,
                    bio = it.bio,
                    followersCount = it.followersCount,
                    followingCount = it.followingCount,
                    role = it.role,
                    isBanned = it.isBanned
                )
            }
        }
    }

    fun getUserByIdFlow(userId: Long): Flow<User?> {
        return dao.getUserFlow(userId).map {
            it?.let {
                User(
                    id = it.id,
                    name = it.name,
                    handle = it.handle,
                    email = it.email,
                    phone = it.phone,
                    avatarLetter = it.avatarLetter,
                    avatarColorIndex = it.avatarColorIndex,
                    verified = it.verified,
                    following = it.following,
                    bio = it.bio,
                    followersCount = it.followersCount,
                    followingCount = it.followingCount,
                    role = it.role,
                    isBanned = it.isBanned
                )
            }
        }
    }

    fun getStoriesFlow(): Flow<List<Story>> {
        return combine(dao.getAllStoriesFlow(), dao.getAllUsersFlow()) { storyEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            storyEntities.map { entity ->
                val authorEntity = userMap[entity.userId]
                val author = authorEntity?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        handle = it.handle,
                        email = it.email,
                        phone = it.phone,
                        avatarLetter = it.avatarLetter,
                        avatarColorIndex = it.avatarColorIndex,
                        verified = it.verified,
                        following = it.following,
                        bio = it.bio,
                        followersCount = it.followersCount,
                        followingCount = it.followingCount,
                        role = it.role,
                        isBanned = it.isBanned
                    )
                }
                Story(
                    id = entity.id,
                    userId = entity.userId,
                    text = entity.text,
                    gradientIndex = entity.gradientIndex,
                    seen = entity.seen,
                    timestamp = entity.timestamp,
                    author = author
                )
            }
        }
    }

    fun getCommentsForPostFlow(postId: Long): Flow<List<Comment>> {
        return combine(dao.getCommentsForPostFlow(postId), dao.getAllUsersFlow()) { commentEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            commentEntities.map { entity ->
                val authorEntity = userMap[entity.userId]
                val author = authorEntity?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        handle = it.handle,
                        email = it.email,
                        phone = it.phone,
                        avatarLetter = it.avatarLetter,
                        avatarColorIndex = it.avatarColorIndex,
                        verified = it.verified,
                        following = it.following,
                        bio = it.bio,
                        followersCount = it.followersCount,
                        followingCount = it.followingCount,
                        role = it.role,
                        isBanned = it.isBanned
                    )
                }
                Comment(
                    id = entity.id,
                    postId = entity.postId,
                    userId = entity.userId,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    likes = entity.likes,
                    liked = entity.liked,
                    author = author
                )
            }
        }
    }

    fun getConversationsFlow(): Flow<List<Conversation>> {
        return combine(dao.getAllConversationsFlow(), dao.getAllUsersFlow()) { conversationEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            conversationEntities.map { entity ->
                val participantEntity = userMap[entity.participantId]
                val participant = participantEntity?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        handle = it.handle,
                        email = it.email,
                        phone = it.phone,
                        avatarLetter = it.avatarLetter,
                        avatarColorIndex = it.avatarColorIndex,
                        verified = it.verified,
                        following = it.following,
                        bio = it.bio,
                        followersCount = it.followersCount,
                        followingCount = it.followingCount,
                        role = it.role,
                        isBanned = it.isBanned
                    )
                }
                Conversation(
                    id = entity.id,
                    participantId = entity.participantId,
                    lastMessage = entity.lastMessage,
                    timestamp = entity.timestamp,
                    unreadCount = entity.unreadCount,
                    isOnline = entity.isOnline,
                    participant = participant
                )
            }
        }
    }

    fun getMessagesForConversationFlow(conversationId: Long): Flow<List<ChatMessage>> {
        return dao.getMessagesForConversationFlow(conversationId).map { entities ->
            entities.map {
                ChatMessage(
                    id = it.id,
                    conversationId = it.conversationId,
                    senderId = it.senderId,
                    text = it.text,
                    timestamp = it.timestamp,
                    isMe = it.isMe
                )
            }
        }
    }

    fun getNotificationsFlow(): Flow<List<NotificationItem>> {
        return combine(dao.getAllNotificationsFlow(), dao.getAllUsersFlow()) { notifEntities, userEntities ->
            val userMap = userEntities.associateBy { it.id }
            notifEntities.map { entity ->
                val userEntity = userMap[entity.userId]
                val user = userEntity?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        handle = it.handle,
                        email = it.email,
                        phone = it.phone,
                        avatarLetter = it.avatarLetter,
                        avatarColorIndex = it.avatarColorIndex,
                        verified = it.verified,
                        following = it.following,
                        bio = it.bio,
                        followersCount = it.followersCount,
                        followingCount = it.followingCount,
                        role = it.role,
                        isBanned = it.isBanned
                    )
                }
                val type = when (entity.type.lowercase(Locale.ROOT)) {
                    "like" -> NotificationType.LIKE
                    "follow" -> NotificationType.FOLLOW
                    "comment" -> NotificationType.COMMENT
                    "repost" -> NotificationType.REPOST
                    "system" -> NotificationType.SYSTEM
                    else -> NotificationType.LIKE
                }
                NotificationItem(
                    id = entity.id,
                    type = type,
                    userId = entity.userId,
                    text = entity.text,
                    subText = entity.subText,
                    timestamp = entity.timestamp,
                    isRead = entity.isRead,
                    user = user
                )
            }
        }
    }

    fun getAdminLogsFlow(): Flow<List<AdminLog>> {
        return dao.getAllAdminLogsFlow().map { entities ->
            entities.map {
                AdminLog(
                    id = it.id,
                    adminName = it.adminName,
                    action = it.action,
                    target = it.target,
                    timestamp = it.timestamp
                )
            }
        }
    }

    fun getAnnouncementsFlow(): Flow<List<SystemAnnouncement>> {
        return dao.getActiveAnnouncementsFlow().map { entities ->
            entities.map {
                SystemAnnouncement(
                    id = it.id,
                    title = it.title,
                    message = it.message,
                    timestamp = it.timestamp,
                    isActive = it.isActive
                )
            }
        }
    }

    suspend fun getPlatformMetrics(): PlatformMetrics {
        val totalUsers = dao.getTotalUsersCount()
        val totalPosts = dao.getPostCount()
        val totalComments = dao.getTotalCommentsCount()
        val bannedUsers = dao.getBannedUsersCount()
        val reportedPosts = dao.getReportedPostsCount()
        return PlatformMetrics(
            totalUsers = totalUsers,
            totalPosts = totalPosts,
            totalLikes = totalPosts * 14 + 890,
            totalComments = totalComments,
            bannedUsersCount = bannedUsers,
            reportedPostsCount = reportedPosts,
            systemUptime = "99.98%",
            serverLatencyMs = 16,
            activeSessions = totalUsers.coerceAtLeast(1)
        )
    }

    // AUTH ACTIONS
    suspend fun authenticate(email: String, password: String): User? {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        // Admin check: clintonchurchill2@gmail.com with ###password::
        if (trimmedEmail.equals("clintonchurchill2@gmail.com", ignoreCase = true) && trimmedPassword == "###password::") {
            var adminUser = dao.getUserByEmail(trimmedEmail)
            if (adminUser == null) {
                adminUser = UserEntity(
                    id = 0L,
                    name = "Clinton Churchill",
                    handle = "clinton_admin",
                    email = "clintonchurchill2@gmail.com",
                    phone = "+1 (555) 019-2834",
                    password = "###password::",
                    avatarLetter = "C",
                    avatarColorIndex = 0,
                    verified = true,
                    following = false,
                    bio = "System Administrator & Founder @ C.",
                    role = "ADMIN",
                    isBanned = false
                )
                dao.insertUser(adminUser)
            }
            logAdminAction("Clinton Churchill", "Admin Login", "Console Access Granted")
            return User(
                id = adminUser.id,
                name = adminUser.name,
                handle = adminUser.handle,
                email = adminUser.email,
                phone = adminUser.phone,
                avatarLetter = adminUser.avatarLetter,
                avatarColorIndex = adminUser.avatarColorIndex,
                verified = adminUser.verified,
                following = adminUser.following,
                bio = adminUser.bio,
                followersCount = adminUser.followersCount,
                followingCount = adminUser.followingCount,
                role = "ADMIN",
                isBanned = false
            )
        }

        // Generic account lookup
        val userEntity = dao.getUserByEmail(trimmedEmail)
        if (userEntity != null && userEntity.password == trimmedPassword && !userEntity.isBanned) {
            return User(
                id = userEntity.id,
                name = userEntity.name,
                handle = userEntity.handle,
                email = userEntity.email,
                phone = userEntity.phone,
                avatarLetter = userEntity.avatarLetter,
                avatarColorIndex = userEntity.avatarColorIndex,
                verified = userEntity.verified,
                following = userEntity.following,
                bio = userEntity.bio,
                followersCount = userEntity.followersCount,
                followingCount = userEntity.followingCount,
                role = userEntity.role,
                isBanned = userEntity.isBanned
            )
        }
        return null
    }

    suspend fun register(
        name: String,
        handle: String,
        email: String,
        phone: String,
        password: String
    ): User? {
        val existingEmail = dao.getUserByEmail(email.trim())
        if (existingEmail != null) return null

        val newId = System.currentTimeMillis()
        val isAdmin = email.trim().equals("clintonchurchill2@gmail.com", ignoreCase = true)
        val role = if (isAdmin) "ADMIN" else "USER"
        val letter = name.take(1).uppercase(Locale.ROOT).ifBlank { "U" }

        val entity = UserEntity(
            id = newId,
            name = name.trim(),
            handle = handle.trim().removePrefix("@"),
            email = email.trim(),
            phone = phone.trim(),
            password = password.trim(),
            avatarLetter = letter,
            avatarColorIndex = (0..6).random(),
            verified = isAdmin,
            following = false,
            bio = "New explorer on C ✦",
            followersCount = 0,
            followingCount = 0,
            role = role,
            isBanned = false
        )
        dao.insertUser(entity)

        return User(
            id = entity.id,
            name = entity.name,
            handle = entity.handle,
            email = entity.email,
            phone = entity.phone,
            avatarLetter = entity.avatarLetter,
            avatarColorIndex = entity.avatarColorIndex,
            verified = entity.verified,
            following = entity.following,
            bio = entity.bio,
            followersCount = 0,
            followingCount = 0,
            role = role,
            isBanned = false
        )
    }

    // ACTIONS
    suspend fun toggleLikePost(postId: Long) {
        val post = dao.getPostById(postId) ?: return
        val newLiked = !post.liked
        val newCount = if (newLiked) post.likes + 1 else (post.likes - 1).coerceAtLeast(0)
        dao.updatePostLike(postId, newLiked, newCount)
    }

    suspend fun toggleRepostPost(postId: Long) {
        val post = dao.getPostById(postId) ?: return
        val newReposted = !post.reposted
        val newCount = if (newReposted) post.reposts + 1 else (post.reposts - 1).coerceAtLeast(0)
        dao.updatePostRepost(postId, newReposted, newCount)
    }

    suspend fun toggleBookmarkPost(postId: Long) {
        val post = dao.getPostById(postId) ?: return
        dao.updatePostBookmark(postId, !post.bookmarked)
    }

    suspend fun createPost(userId: Long, text: String, imageUrl: String?): Long {
        val newPost = PostEntity(
            userId = userId,
            text = text,
            likes = 0,
            comments = 0,
            reposts = 0,
            liked = false,
            reposted = false,
            bookmarked = false,
            timestamp = "just now",
            imageUrl = imageUrl,
            isUserCreated = true,
            createdAtEpoch = System.currentTimeMillis()
        )
        return dao.insertPost(newPost)
    }

    suspend fun deletePost(postId: Long) {
        dao.deletePost(postId)
    }

    suspend fun reportPost(postId: Long) {
        dao.reportPost(postId, isFlagged = true)
    }

    suspend fun toggleFollowUser(userId: Long) {
        val user = dao.getUserById(userId) ?: return
        val newFollowing = !user.following
        val delta = if (newFollowing) 1 else -1
        dao.updateUser(user.copy(following = newFollowing, followersCount = (user.followersCount + delta).coerceAtLeast(0)))
    }

    suspend fun addComment(userId: Long, postId: Long, text: String) {
        val comment = CommentEntity(
            postId = postId,
            userId = userId,
            text = text,
            timestamp = "just now",
            likes = 0,
            liked = false,
            createdAtEpoch = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        dao.incrementPostCommentCount(postId)
    }

    suspend fun toggleLikeComment(commentId: Long, currentLiked: Boolean, currentLikes: Int) {
        val newLiked = !currentLiked
        val newCount = if (newLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)
        dao.updateCommentLike(commentId, newLiked, newCount)
    }

    suspend fun createStory(userId: Long, text: String, gradientIndex: Int) {
        val story = StoryEntity(
            userId = userId,
            text = text,
            gradientIndex = gradientIndex,
            seen = false,
            timestamp = "just now",
            createdAtEpoch = System.currentTimeMillis()
        )
        dao.insertStory(story)
    }

    suspend fun markStorySeen(storyId: Long) {
        dao.markStorySeen(storyId)
    }

    suspend fun markConversationRead(conversationId: Long) {
        dao.markConversationRead(conversationId)
    }

    suspend fun sendMessage(senderId: Long, conversationId: Long, text: String) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val msg = ChatMessageEntity(
            conversationId = conversationId,
            senderId = senderId,
            text = text,
            timestamp = timeStr,
            isMe = true,
            sentAtEpoch = System.currentTimeMillis()
        )
        dao.insertMessage(msg)

        val conv = dao.getConversationById(conversationId)
        if (conv != null) {
            dao.updateConversation(
                conv.copy(
                    lastMessage = text,
                    timestamp = timeStr,
                    lastUpdatedEpoch = System.currentTimeMillis()
                )
            )
        }

        // Realistic Simulated Real-time Bot Response
        scope.launch {
            delay(1200)
            val replies = listOf(
                "That's super interesting! Let's explore that further 🔥",
                "Totally agree! Have you checked out the new C components?",
                "Love the energy! Shipping this feels so refreshing.",
                "100%! Let's connect on that soon.",
                "Awesome insight! Definitely saving this."
            )
            val replyText = replies.random()
            val replyTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val replyMsg = ChatMessageEntity(
                conversationId = conversationId,
                senderId = conv?.participantId ?: 1L,
                text = replyText,
                timestamp = replyTime,
                isMe = false,
                sentAtEpoch = System.currentTimeMillis()
            )
            dao.insertMessage(replyMsg)
            if (conv != null) {
                dao.updateConversation(
                    conv.copy(
                        lastMessage = replyText,
                        timestamp = replyTime,
                        lastUpdatedEpoch = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun clearAllNotifications() {
        dao.clearAllNotifications()
    }

    suspend fun updateCurrentUser(userId: Long, name: String, handle: String, bio: String) {
        val user = dao.getUserById(userId)
        if (user != null) {
            dao.updateUser(user.copy(name = name, handle = handle, bio = bio))
        }
    }

    // ADMIN PRIVILEGED METHODS
    suspend fun logAdminAction(adminName: String, action: String, target: String) {
        val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
        dao.insertAdminLog(
            AdminLogEntity(
                adminName = adminName,
                action = action,
                target = target,
                timestamp = timeStr
            )
        )
    }

    suspend fun adminToggleBan(adminName: String, userId: Long): Boolean {
        val user = dao.getUserById(userId) ?: return false
        val newStatus = !user.isBanned
        dao.updateUserBanStatus(userId, newStatus)
        logAdminAction(adminName, if (newStatus) "Banned User" else "Unbanned User", "@${user.handle}")
        return newStatus
    }

    suspend fun adminToggleVerify(adminName: String, userId: Long): Boolean {
        val user = dao.getUserById(userId) ?: return false
        val newStatus = !user.verified
        dao.updateUserVerified(userId, newStatus)
        logAdminAction(adminName, if (newStatus) "Granted Verified Badge" else "Revoked Verified Badge", "@${user.handle}")
        return newStatus
    }

    suspend fun adminToggleRole(adminName: String, userId: Long): String {
        val user = dao.getUserById(userId) ?: return "USER"
        val newRole = when (user.role) {
            "ADMIN" -> "USER"
            "MODERATOR" -> "ADMIN"
            else -> "MODERATOR"
        }
        dao.updateUserRole(userId, newRole)
        logAdminAction(adminName, "Promoted Role to $newRole", "@${user.handle}")
        return newRole
    }

    suspend fun adminDeleteUser(adminName: String, userId: Long) {
        val user = dao.getUserById(userId)
        dao.deleteUser(userId)
        logAdminAction(adminName, "Deleted User Account", "@${user?.handle ?: userId}")
    }

    suspend fun adminTogglePinPost(adminName: String, postId: Long): Boolean {
        val post = dao.getPostById(postId) ?: return false
        val newPinned = !post.isPinned
        dao.updatePostPinned(postId, newPinned)
        logAdminAction(adminName, if (newPinned) "Pinned Post" else "Unpinned Post", "Post #$postId")
        return newPinned
    }

    suspend fun adminDeletePost(adminName: String, postId: Long) {
        dao.deletePost(postId)
        logAdminAction(adminName, "Removed Post (Moderation)", "Post #$postId")
    }

    suspend fun adminDismissReports(adminName: String, postId: Long) {
        dao.dismissPostReports(postId)
        logAdminAction(adminName, "Dismissed Reports", "Post #$postId")
    }

    suspend fun adminBroadcast(adminName: String, title: String, message: String) {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        dao.insertAnnouncement(
            AnnouncementEntity(
                title = title,
                message = message,
                timestamp = timeStr,
                isActive = true
            )
        )
        // Also add system notification to all
        dao.insertNotification(
            NotificationEntity(
                type = "system",
                userId = 0L,
                text = "Official Broadcast: $title",
                subText = message.take(60),
                timestamp = "Just now",
                isRead = false
            )
        )
        logAdminAction(adminName, "Global Announcement Broadcast", title)
    }

    suspend fun adminDismissAnnouncement(announcementId: Long) {
        dao.dismissAnnouncement(announcementId)
    }

    suspend fun adminResetDatabase(adminName: String) {
        dao.clearAllPosts()
        dao.clearAllUsers()
        dao.clearAllComments()
        dao.clearAllStories()
        seedInitialData()
        logAdminAction(adminName, "System Re-seeded", "Database Initialized")
    }
}
