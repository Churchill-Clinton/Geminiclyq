package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.User
import com.example.ui.components.AvatarBadge
import com.example.ui.components.PostCard
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.StatusGold
import com.example.ui.theme.StatusRed
import com.example.ui.theme.Surface2Dark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    user: User?,
    allPosts: List<Post>,
    onOpenEditProfile: () -> Unit,
    onOpenAdminPanel: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onLogout: () -> Unit = {},
    onLikePost: (Long) -> Unit,
    onCommentPost: (Post) -> Unit,
    onRepostPost: (Long) -> Unit,
    onBookmarkPost: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onReportPost: (Long) -> Unit = {},
    onAdminPinPost: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val safeUser = user ?: User(
        id = 0L,
        name = "Guest User",
        handle = "guest",
        email = "",
        phone = "",
        avatarLetter = "G",
        avatarColorIndex = 0,
        verified = false,
        bio = "Exploring C in guest mode.",
        followersCount = 0,
        followingCount = 0,
        role = "USER"
    )

    val myPosts = allPosts.filter { it.userId == safeUser.id || (safeUser.id == 0L && it.isUserCreated) }
    val likedPosts = allPosts.filter { it.liked }
    val savedPosts = allPosts.filter { it.bookmarked }

    val displayedPosts = when (selectedTab) {
        0 -> myPosts
        1 -> likedPosts
        2 -> savedPosts
        else -> myPosts
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Banner and Header Profile
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Cover Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.linearGradient(
                                if (safeUser.isAdmin) listOf(StatusGold.copy(alpha = 0.5f), AccentPurple.copy(alpha = 0.7f), Color(0xFF11162A))
                                else listOf(AccentBlue.copy(alpha = 0.5f), AccentPurple.copy(alpha = 0.7f), Color(0xFF11162A))
                            )
                        )
                )

                // Avatar overlapping banner
                AvatarBadge(
                    letter = safeUser.avatarLetter,
                    colorIndex = safeUser.avatarColorIndex,
                    size = 72.dp,
                    fontSize = 26,
                    hasGlowRing = true,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .offset(y = 90.dp)
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // User Info & Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = safeUser.name,
                                color = TextPrimary,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (safeUser.verified) {
                                Spacer(modifier = Modifier.width(5.dp))
                                VerifiedBadge(size = 15.dp)
                            }
                            if (safeUser.isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        color = StatusGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                        Text(
                            text = "@${safeUser.handle}",
                            color = TextMuted,
                            fontSize = 13.5.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onOpenEditProfile,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.Black),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Edit", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { shareProfile(context, safeUser) },
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                if (safeUser.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = safeUser.bio,
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp
                    )
                }

                // Admin Console button
                if (safeUser.isAdmin) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onOpenAdminPanel,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Launch Admin Command Console", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatColumn(label = "Posts", value = "${myPosts.size}")
                    StatColumn(label = "Followers", value = "${safeUser.followersCount}")
                    StatColumn(label = "Following", value = "${safeUser.followingCount}")
                    StatColumn(label = "Likes", value = "${likedPosts.size}")
                }

                // Account Credentials & Session Actions
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (safeUser.email.isNotBlank()) safeUser.email else "Guest Account",
                                color = TextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (safeUser.phone.isNotBlank()) {
                                Text(
                                    text = safeUser.phone,
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onOpenAuth,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch", fontSize = 11.5.sp, color = AccentBlue)
                            }

                            OutlinedButton(
                                onClick = onLogout,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = StatusRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Logout", fontSize = 11.5.sp, color = StatusRed)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Tabs: My Posts, Likes, Saved
            val tabTitles = listOf("My Posts", "Likes", "Saved 🔖")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = AccentBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentBlue,
                        height = 2.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(BorderSubtle)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) AccentBlue else TextSecondary,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp
                            )
                        }
                    )
                }
            }
        }

        // Post List
        if (displayedPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val emptyMessage = when (selectedTab) {
                        0 -> "You haven't posted yet. Tap + to share your first post!"
                        1 -> "You haven't liked any posts yet."
                        2 -> "No saved bookmarks. Save interesting posts to read later!"
                        else -> "No posts found."
                    }
                    Text(
                        text = emptyMessage,
                        color = TextMuted,
                        fontSize = 13.5.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            items(displayedPosts, key = { "prof_post_${it.id}" }) { post ->
                PostCard(
                    post = post,
                    isAdmin = safeUser.isAdmin,
                    onLikeClick = { onLikePost(post.id) },
                    onCommentClick = { onCommentPost(post) },
                    onRepostClick = { onRepostPost(post.id) },
                    onBookmarkClick = { onBookmarkPost(post.id) },
                    onDeleteClick = { onDeletePost(post.id) },
                    onReportClick = { onReportPost(post.id) },
                    onAdminPinClick = { onAdminPinPost(post.id) }
                )
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.5.sp
        )
    }
}

fun shareProfile(context: Context, user: User) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out ${user.name} (@${user.handle}) on C — The real-time social platform: https://c.social/@${user.handle}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share profile via")
    context.startActivity(shareIntent)
}
