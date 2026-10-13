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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.SystemAnnouncement
import com.example.ui.components.AvatarBadge
import com.example.ui.components.PostCard
import com.example.ui.components.StoryAddButton
import com.example.ui.components.StoryItem
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.StatusGold
import com.example.ui.theme.Surface2Dark
import com.example.ui.theme.Surface3Dark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FeedFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: List<Post>,
    stories: List<Story>,
    announcements: List<SystemAnnouncement> = emptyList(),
    currentFilter: FeedFilter,
    isAdmin: Boolean = false,
    onFilterChange: (FeedFilter) -> Unit,
    onLikePost: (Long) -> Unit,
    onCommentPost: (Post) -> Unit,
    onRepostPost: (Long) -> Unit,
    onBookmarkPost: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onReportPost: (Long) -> Unit = {},
    onAdminPinPost: (Long) -> Unit = {},
    onOpenStory: (Story) -> Unit,
    onAddStory: () -> Unit,
    onOpenCompose: () -> Unit,
    onOpenAdminPanel: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BgDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenCompose,
                containerColor = AccentBlue,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Post",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    onRefresh()
                    delay(600)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Top App Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Logo
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(AccentBlue, AccentPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // For You / Following Tabs
                        val selectedTabIndex = if (currentFilter == FeedFilter.FOR_YOU) 0 else 1
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.Transparent,
                            contentColor = AccentBlue,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = AccentBlue,
                                    height = 2.dp
                                )
                            },
                            divider = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Tab(
                                selected = currentFilter == FeedFilter.FOR_YOU,
                                onClick = { onFilterChange(FeedFilter.FOR_YOU) },
                                text = {
                                    Text(
                                        text = "For You",
                                        fontWeight = if (currentFilter == FeedFilter.FOR_YOU) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.5.sp,
                                        color = if (currentFilter == FeedFilter.FOR_YOU) AccentBlue else TextSecondary
                                    )
                                }
                            )

                            Tab(
                                selected = currentFilter == FeedFilter.FOLLOWING,
                                onClick = { onFilterChange(FeedFilter.FOLLOWING) },
                                text = {
                                    Text(
                                        text = "Following",
                                        fontWeight = if (currentFilter == FeedFilter.FOLLOWING) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.5.sp,
                                        color = if (currentFilter == FeedFilter.FOLLOWING) AccentBlue else TextSecondary
                                    )
                                }
                            )
                        }

                        // Admin Console Shield Shortcut
                        if (isAdmin) {
                            IconButton(
                                onClick = onOpenAdminPanel,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(StatusGold.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Console",
                                    tint = StatusGold,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Auth / Switch Account Shortcut
                        IconButton(
                            onClick = onOpenAuth,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Authentication",
                                tint = TextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Refresh
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Official Announcement Banner (if any)
                if (announcements.isNotEmpty()) {
                    item {
                        val firstAnn = announcements.first()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentBlue))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = firstAnn.title,
                                        color = AccentBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = firstAnn.message,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                // Stories Carousel
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp)
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                StoryAddButton(onClick = onAddStory)
                            }
                            items(stories, key = { it.id }) { story ->
                                StoryItem(
                                    story = story,
                                    onClick = { onOpenStory(story) }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(BorderSubtle)
                    )
                }

                // Inline Compose Card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenCompose)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarBadge(letter = "C", colorIndex = 0, size = 38.dp, fontSize = 14)
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Surface2Dark)
                                .border(1.dp, BorderLight, RoundedCornerShape(22.dp))
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "What's happening?",
                                color = TextMuted,
                                fontSize = 13.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Surface3Dark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Add image",
                                tint = AccentBlue,
                                modifier = Modifier.size(17.dp)
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

                // Posts Feed
                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (currentFilter == FeedFilter.FOLLOWING) "No posts from followed users yet." else "No posts found.",
                                    color = TextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Explore accounts or create your own post!",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            isAdmin = isAdmin,
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
    }
}
