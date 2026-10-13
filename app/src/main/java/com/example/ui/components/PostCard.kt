package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.StatusGold
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.Surface2Dark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onReportClick: (() -> Unit)? = null,
    onAdminPinClick: (() -> Unit)? = null,
    isAdmin: Boolean = false,
    onTagClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Heart bounce animation
    var likedTrigger by remember { mutableStateOf(post.liked) }
    val heartScale by animateFloatAsState(
        targetValue = if (post.liked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "heartScale"
    )

    @Suppress("DEPRECATION")
    fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(Vibrator::class.java)
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    var showBurstHeart by remember { mutableStateOf(false) }
    val burstScale by animateFloatAsState(
        targetValue = if (showBurstHeart) 1.5f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "burstScale"
    )

    LaunchedEffect(showBurstHeart) {
        if (showBurstHeart) {
            delay(650)
            showBurstHeart = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = BorderSubtle)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        vibrate()
                        if (!post.liked) {
                            onLikeClick()
                        }
                        showBurstHeart = true
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
        if (post.isPinned) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = StatusGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "PINNED ANNOUNCEMENT",
                    color = StatusGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            val author = post.author
            AvatarBadge(
                letter = author?.avatarLetter ?: "C",
                colorIndex = author?.avatarColorIndex ?: 0,
                size = 42.dp,
                fontSize = 15,
                hasGlowRing = author?.verified == true
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header (Author, handle, time)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = author?.name ?: "User",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (author?.verified == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 14.dp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "@${author?.handle ?: "user"}",
                        fontSize = 13.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = " · ${post.timestamp}",
                        fontSize = 12.5.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Surface2Dark)
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (post.bookmarked) "Remove Bookmark" else "Bookmark Post", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        if (post.bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = StatusGold
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onBookmarkClick()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Share Post", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = AccentBlue)
                                },
                                onClick = {
                                    showMenu = false
                                    sharePostText(context, post)
                                }
                            )

                            if (onReportClick != null) {
                                DropdownMenuItem(
                                    text = { Text("Report Post", color = StatusRed) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Report, contentDescription = null, tint = StatusRed)
                                    },
                                    onClick = {
                                        showMenu = false
                                        onReportClick()
                                    }
                                )
                            }

                            if (isAdmin && onAdminPinClick != null) {
                                DropdownMenuItem(
                                    text = { Text(if (post.isPinned) "Unpin from Feed" else "Pin to Feed", color = StatusGold) },
                                    leadingIcon = {
                                        Icon(Icons.Default.PushPin, contentDescription = null, tint = StatusGold)
                                    },
                                    onClick = {
                                        showMenu = false
                                        onAdminPinClick()
                                    }
                                )
                            }

                            if ((post.isUserCreated || isAdmin) && onDeleteClick != null) {
                                DropdownMenuItem(
                                    text = { Text(if (isAdmin && !post.isUserCreated) "Moderate / Delete" else "Delete Post", color = StatusRed) },
                                    leadingIcon = {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = StatusRed)
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Post Content with colored hashtags & mentions
                val annotatedText = remember(post.text) {
                    buildAnnotatedString {
                        val words = post.text.split(" ", "\n")
                        var currentIdx = 0
                        for (token in post.text.split("(?<=\\s)|(?=\\s)".toRegex())) {
                            if (token.startsWith("#") && token.length > 1) {
                                withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.Medium)) {
                                    append(token)
                                }
                            } else if (token.startsWith("@") && token.length > 1) {
                                withStyle(SpanStyle(color = AccentPurple, fontWeight = FontWeight.Medium)) {
                                    append(token)
                                }
                            } else {
                                withStyle(SpanStyle(color = TextPrimary)) {
                                    append(token)
                                }
                            }
                        }
                    }
                }

                Text(
                    text = annotatedText,
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Optional attached media image
                if (!post.imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = post.imageUrl,
                            contentDescription = "Post Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactions Bar: Like, Comment, Repost, Bookmark, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Action
                    val likeColor by animateColorAsState(
                        targetValue = if (post.liked) StatusRed else TextMuted,
                        label = "likeColor"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate()
                                onLikeClick()
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (post.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = likeColor,
                            modifier = Modifier
                                .size(17.dp)
                                .scale(heartScale)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatStat(post.likes),
                            color = likeColor,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Comment Action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onCommentClick()
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatStat(post.comments),
                            color = TextMuted,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Repost Action
                    val repostColor by animateColorAsState(
                        targetValue = if (post.reposted) StatusGreen else TextMuted,
                        label = "repostColor"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate()
                                onRepostClick()
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repost",
                            tint = repostColor,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatStat(post.reposts),
                            color = repostColor,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Bookmark Action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate()
                                onBookmarkClick()
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (post.bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.bookmarked) StatusGold else TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Share Action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                sharePostText(context, post)
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showBurstHeart) {
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(burstScale)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(StatusRed.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = StatusRed,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}
}

fun formatStat(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

fun sharePostText(context: Context, post: Post) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "${post.author?.name ?: "Someone"} on C:\n\n${post.text}\n\nShared via C — The next-gen social network.")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share post via")
    context.startActivity(shareIntent)
}
