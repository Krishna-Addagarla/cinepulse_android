package com.partner.cinepulse.ui.screens.discussion

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.DarkSlate
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Colour tokens ──────────────────────────────────────────────────────────────
//private val BgDark        = Color(0xFF080C14)
//private val CardDark      = Color(0xFF0F1623)
//private val CardBorder    = Color(0xFF1C2333)
//private val AccentBlue    = Color(0xFF1A6BFF)
//private val AccentRed     = Color(0xFFE50914)
//private val AccentGreen   = Color(0xFF00C853)
//private val TextPrimary   = Color(0xFFFFFFFF)
//private val TextSecondary = Color(0xFF8A95A8)
//private val TabSelected   = Color(0xFF1A6BFF)
//private val TabUnselected = Color(0xFF141B27)

// ── Data Models ────────────────────────────────────────────────────────────────
data class Group(
    val id: String,
    val name: String,
    val emoji: String,
    val gradientColors: List<Color>,
    val memberCount: Int,
    val latestDiscussion: String,
    val timeAgo: String,
    val unreadCount: Int = 0,
    val isHot: Boolean = false
)

data class TrendingDiscussion(
    val id: String,
    val title: String,
    val groupName: String,
    val replyCount: Int,
    val likeCount: Float,
    val timeAgo: String
)

// ── Sample Data ────────────────────────────────────────────────────────────────
val myGroups = listOf(
    Group("1", "Nolan Universe",            "🔥", listOf(Color(0xFFB05C1A), Color(0xFF6B3A10)), 45200, "New discussion on Tenet's timeline",  "5m ago",  unreadCount = 3, isHot = true),
    Group("2", "Sci-Fi Cinema Club",        "🎬", listOf(Color(0xFF1A237E), Color(0xFF283593)), 32800, "Blade Runner 2049 vs Original...",      "1h ago"),
    Group("3", "A24 Film Lovers",           "🎭", listOf(Color(0xFF880E4F), Color(0xFF4A148C)), 28500, "Everything Everywhere reactions",        "2h ago",  unreadCount = 7),
    Group("4", "Classic Cinema Discussions","🎥", listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)), 19300, "Criterion Collection favorites",         "5h ago"),
)

val suggestedGroups = listOf(
    Group("5", "Horror Film Enthusiasts",   "👻", listOf(Color(0xFF4A148C), Color(0xFF311B92)), 23400, "Best horror films of the decade",        "15m ago"),
    Group("6", "Marvel Cinematic Universe", "🦸", listOf(Color(0xFFB71C1C), Color(0xFF880E4F)), 89300, "Endgame alternate endings discussion",   "10m ago", isHot = true),
    Group("7", "International Cinema",      "🌍", listOf(Color(0xFF006064), Color(0xFF00695C)), 45200, "Parasite vs. Oldboy - Korean cinema",    "25m ago"),
)

val trendingDiscussions = listOf(
    TrendingDiscussion("1", "What makes Villeneuve's cinematography so unique?", "Visual Storytelling", 234, 1.2f, "3h ago"),
    TrendingDiscussion("2", "Unpopular opinion: Inception is overrated",          "Hot Takes",           567, 890f, "5h ago"),
    TrendingDiscussion("3", "Best movie soundtracks of 2024",                     "Film Music",          189, 543f, "1d ago"),
)

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun DiscussionsScreen(
    onNavigateBack: () -> Unit,
    onGroupClick: (String) -> Unit = {},
    onDiscussionClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(
            title = "Discussions",
            showSearchIcon = true,
            showNotificationIcon = false,
            showProfileIcon = false,
            showBackButton = false,
            onBackClick = onNavigateBack,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = onProfileClick
        )

        // ── Tab row ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("My Groups", "Discover").forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedTab == index) AccentBlue else DarkSlate)
                        .border(
                            width = if (selectedTab == index) 0.dp else 1.dp,
                            color = if (selectedTab == index) Color.Transparent else CardBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (selectedTab == index) TextPrimary else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> MyGroupsContent(onGroupClick = onGroupClick, onDiscussionClick = onDiscussionClick)
            1 -> DiscoverContent(onGroupClick = onGroupClick, onDiscussionClick = onDiscussionClick)
        }
    }
}

// ── My Groups Tab ──────────────────────────────────────────────────────────────
@Composable
private fun MyGroupsContent(
    onGroupClick: (String) -> Unit,
    onDiscussionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues( start = 16.dp,
            end = 16.dp,
            bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "My Groups (${myGroups.size})",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(myGroups) { group ->
            GroupCard(group = group, showJoin = false, onClick = { onGroupClick(group.id) })
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "📈", fontSize = 15.sp)
                Text(
                    text = "Trending Discussions",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(trendingDiscussions) { discussion ->
            TrendingDiscussionCard(discussion = discussion, onClick = { onDiscussionClick(discussion.id) })
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Suggested Groups",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(suggestedGroups) { group ->
            GroupCard(group = group, showJoin = true, onClick = { onGroupClick(group.id) })
        }
    }
}

// ── Discover Tab ───────────────────────────────────────────────────────────────
@Composable
private fun DiscoverContent(
    onGroupClick: (String) -> Unit,
    onDiscussionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Suggested for You",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(suggestedGroups) { group ->
            GroupCard(group = group, showJoin = true, onClick = { onGroupClick(group.id) })
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "📈", fontSize = 15.sp)
                Text(
                    text = "Popular Discussions",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(trendingDiscussions) { discussion ->
            TrendingDiscussionCard(discussion = discussion, onClick = { onDiscussionClick(discussion.id) })
        }
    }
}

// ── Group Card ─────────────────────────────────────────────────────────────────
@Composable
private fun GroupCard(group: Group, showJoin: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Group avatar
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(group.gradientColors))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = group.emoji, fontSize = 22.sp)
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = group.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (group.isHot) {
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFFF6B6B), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "👥", fontSize = 11.sp)
                Text(text = "${formatMemberCount(group.memberCount)} members", color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "💬", fontSize = 11.sp)
                Text(
                    text = group.latestDiscussion,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = group.timeAgo, color = TextSecondary.copy(alpha = 0.6f), fontSize = 11.sp)
        }

        // Right side: either unread badge OR join button
        if (showJoin) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentBlue)
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "Join", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else if (group.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(AccentBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${group.unreadCount}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Trending Discussion Card ───────────────────────────────────────────────────
@Composable
private fun TrendingDiscussionCard(discussion: TrendingDiscussion, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(
            text = discussion.title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = discussion.groupName,
                color = AccentBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${discussion.replyCount} replies", color = TextSecondary, fontSize = 12.sp)
                Text(text = "•", color = TextSecondary, fontSize = 12.sp)
                val likesText = if (discussion.likeCount >= 1000) "${discussion.likeCount / 1000}K" else "${discussion.likeCount.toInt()}"
                Text(text = "$likesText likes", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
fun formatMemberCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000     -> "${count / 1_000}K"
    else               -> count.toString()
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun DiscussionsScreenPreview() {
    DiscussionsScreen(onNavigateBack = {})
}