package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.TrendingTopic
import com.example.data.model.User
import com.example.ui.components.AvatarBadge
import com.example.ui.components.PostCard
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.StatusRed
import com.example.ui.theme.Surface2Dark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    allPosts: List<Post>,
    users: List<User>,
    trendingTopics: List<TrendingTopic>,
    onToggleFollow: (Long) -> Unit,
    onLikePost: (Long) -> Unit,
    onCommentPost: (Post) -> Unit,
    onRepostPost: (Long) -> Unit,
    onBookmarkPost: (Long) -> Unit,
    onDeletePost: ((Long) -> Unit)? = null,
    onReportPost: ((Long) -> Unit)? = null,
    isAdmin: Boolean = false,
    modifier: Modifier = Modifier
) {
    val trimmedQuery = searchQuery.trim()
    val isSearching = trimmedQuery.isNotBlank()

    val matchedPosts = if (isSearching) {
        allPosts.filter {
            it.text.contains(trimmedQuery, ignoreCase = true) ||
                    (it.author?.name?.contains(trimmedQuery, ignoreCase = true) == true) ||
                    (it.author?.handle?.contains(trimmedQuery, ignoreCase = true) == true)
        }
    } else emptyList()

    val matchedUsers = if (isSearching) {
        users.filter {
            it.name.contains(trimmedQuery, ignoreCase = true) ||
                    it.handle.contains(trimmedQuery, ignoreCase = true)
        }
    } else emptyList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Search Input Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Search C, people, tags…",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Surface2Dark,
                        unfocusedContainerColor = Surface2Dark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Active Search Results
        if (isSearching) {
            if (matchedUsers.isNotEmpty()) {
                item {
                    Text(
                        text = "People",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(matchedUsers, key = { "user_${it.id}" }) { user ->
                    UserFollowItem(user = user, onToggleFollow = { onToggleFollow(user.id) })
                }
            }

            item {
                Text(
                    text = "Posts matching \"$searchQuery\"",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (matchedPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No matching posts found.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else {
                items(matchedPosts, key = { "post_${it.id}" }) { post ->
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post.id) },
                        onCommentClick = { onCommentPost(post) },
                        onRepostClick = { onRepostPost(post.id) },
                        onBookmarkClick = { onBookmarkPost(post.id) },
                        onDeleteClick = onDeletePost?.let { { it(post.id) } },
                        onReportClick = onReportPost?.let { { it(post.id) } },
                        isAdmin = isAdmin
                    )
                }
            }
        } else {
            // Trending Topics Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Trending on C",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        trendingTopics.forEach { topic ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSearchChange(topic.tag) }
                                    .padding(horizontal = 16.dp, vertical = 11.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = topic.category,
                                            color = TextMuted,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = topic.tag,
                                            color = TextPrimary,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = topic.postCount,
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(BorderSubtle)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Who To Follow Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderLight))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Who to follow",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )

                        // Show users other than current user (id = 0)
                        val suggestedUsers = users.filter { it.id != 0L }
                        suggestedUsers.forEach { user ->
                            UserFollowItem(
                                user = user,
                                onToggleFollow = { onToggleFollow(user.id) }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(BorderSubtle)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserFollowItem(
    user: User,
    onToggleFollow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarBadge(
            letter = user.avatarLetter,
            colorIndex = user.avatarColorIndex,
            size = 40.dp,
            fontSize = 15
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (user.verified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedBadge(size = 13.dp)
                }
            }
            Text(
                text = "@${user.handle}",
                color = TextMuted,
                fontSize = 12.sp
            )
            if (user.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.bio,
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onToggleFollow,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.following) Surface2Dark else AccentBlue,
                contentColor = if (user.following) TextSecondary else Color.Black
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            border = if (user.following) androidx.compose.foundation.BorderStroke(1.dp, BorderLight) else null
        ) {
            Text(
                text = if (user.following) "Following" else "Follow",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
