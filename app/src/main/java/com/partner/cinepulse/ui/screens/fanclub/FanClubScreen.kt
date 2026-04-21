package com.partner.cinepulse.ui.screens.fanclub

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.AccentBlue
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.CardBorder
import com.partner.cinepulse.ui.theme.CardDark
import com.partner.cinepulse.ui.theme.DarkSlate
import com.partner.cinepulse.ui.theme.TextPrimary
import com.partner.cinepulse.ui.theme.TextSecondary

// ── Data Models ────────────────────────────────────────────────────────────────

data class TrendingDiscussion(
    val id: String,
    val title: String,
    val groupName: String,
    val replyCount: Int,
    val likeCount: Float,
    val timeAgo: String
)

// ── Sample / Static Data ───────────────────────────────────────────────────────

private val sampleTrendingDiscussions = listOf(
    TrendingDiscussion("1", "What makes Villeneuve's cinematography so unique?", "Visual Storytelling", 234, 1200f, "3h ago"),
    TrendingDiscussion("2", "Unpopular opinion: Inception is overrated",          "Hot Takes",          567,  890f, "5h ago"),
    TrendingDiscussion("3", "Best movie soundtracks of 2024",                     "Film Music",         189,  543f, "1d ago"),
)

private val sampleSuggestedGroups = listOf(
    FanClubResponse(
        id = 1L,
        name = "RamCharan Fan Club",
        description = "Group for Ram Charan fans",
        photo_url = "https://example.com/ramcharan.jpg",
        cover_url = "",
        members_count = 12000,
        posts_count = 230,
        is_private = false,
        is_member = true,
        is_admin = true,
        created_at = "2026-04-20T06:22:07.177776Z",
        created_by = 1L,
        creator_name = "Admin",
        has_pending_request = false,
        linked_actor = null,
        linked_movie = null,
        linked_tvshow = null,
        linked_crew = null
    ),
    FanClubResponse(
        id = 2L,
        name = "Horror Film Enthusiasts",
        description = "Best horror films of the decade",
        photo_url = "https://example.com/horror.jpg",
        cover_url = "",
        members_count = 23400,
        posts_count = 540,
        is_private = false,
        is_member = false,
        is_admin = false,
        created_at = "2026-04-20T06:22:07.177776Z",
        created_by = 2L,
        creator_name = "Admin",
        has_pending_request = false,
        linked_actor = null,
        linked_movie = null,
        linked_tvshow = null,
        linked_crew = null
    ),
    FanClubResponse(
        id = 3L,
        name = "Marvel Cinematic Universe",
        description = "Endgame alternate endings discussion",
        photo_url = "https://example.com/marvel.jpg",
        cover_url = "",
        members_count = 89300,
        posts_count = 1200,
        is_private = false,
        is_member = true,
        is_admin = false,
        created_at = "2026-04-20T06:22:07.177776Z",
        created_by = 3L,
        creator_name = "Admin",
        has_pending_request = false,
        linked_actor = null,
        linked_movie = null,
        linked_tvshow = null,
        linked_crew = null
    ),
    FanClubResponse(
        id = 4L,
        name = "International Cinema",
        description = "Parasite vs. Oldboy - Korean cinema",
        photo_url = "https://example.com/international.jpg",
        cover_url = "",
        members_count = 45200,
        posts_count = 800,
        is_private = false,
        is_member = false,
        is_admin = false,
        created_at = "2026-04-20T07:25:00Z",
        created_by = 4L,
        creator_name = "Admin",
        has_pending_request = false,
        linked_actor = null,
        linked_movie = null,
        linked_tvshow = null,
        linked_crew = null
    ),
    FanClubResponse(
        id = 5L,
        name = "Tollywood Superstars",
        description = "All about Telugu cinema stars",
        photo_url = "https://example.com/tollywood.jpg",
        cover_url = "",
        members_count = 67000,
        posts_count = 950,
        is_private = false,
        is_member = true,
        is_admin = false,
        created_at = "2026-04-20T08:00:00Z",
        created_by = 5L,
        creator_name = "Admin",
        has_pending_request = false,
        linked_actor = null,
        linked_movie = null,
        linked_tvshow = null,
        linked_crew = null
    )
)

// ── Stateful Screen (NOT previewable — uses hiltViewModel) ────────────────────

@Composable
fun DiscussionsScreen(
    onNavigateBack: () -> Unit,
    onGroupClick: (Long) -> Unit = {},       // FIX: Long, matching FanClubResponse.id
    onDiscussionClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: FanClubViewModel = hiltViewModel()
) {
    val userFanClubs by viewModel.userFanClubs.collectAsStateWithLifecycle()

    DiscussionsScreenContent(
        userFanClubs = userFanClubs,
        suggestedGroups = sampleSuggestedGroups,
        trendingDiscussions = sampleTrendingDiscussions,
        onGroupClick = onGroupClick,
        onDiscussionClick = onDiscussionClick,
        onNavigateBack = onNavigateBack,
        onSearchClick = onSearchClick,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick
    )
}

// ── Stateless Content (previewable) ───────────────────────────────────────────

@Composable
fun DiscussionsScreenContent(
    userFanClubs: List<FanClubResponse>,
    suggestedGroups: List<FanClubResponse>,
    trendingDiscussions: List<TrendingDiscussion>,
    onGroupClick: (Long) -> Unit,            // FIX: Long
    onDiscussionClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
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
            0 -> MyGroupsContent(
                userFanClubs = userFanClubs,
                suggestedGroups = suggestedGroups,
                trendingDiscussions = trendingDiscussions,
                onGroupClick = onGroupClick,
                onDiscussionClick = onDiscussionClick
            )
            1 -> DiscoverContent(
                suggestedGroups = suggestedGroups,
                trendingDiscussions = trendingDiscussions,
                onGroupClick = onGroupClick,
                onDiscussionClick = onDiscussionClick
            )
        }
    }
}

// ── My Groups Tab ──────────────────────────────────────────────────────────────

@Composable
private fun MyGroupsContent(
    userFanClubs: List<FanClubResponse>,
    suggestedGroups: List<FanClubResponse>,
    trendingDiscussions: List<TrendingDiscussion>,
    onGroupClick: (Long) -> Unit,            // FIX: Long
    onDiscussionClick: (String) -> Unit
) {
    if (userFanClubs.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "My Groups (${userFanClubs.size})",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(userFanClubs, key = { it.id }) { group ->
                GroupCard(
                    group = group,
                    showJoin = false,
                    onClick = { onGroupClick(group.id) }   // FIX: group.id is Long, matches
                )
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

            items(trendingDiscussions, key = { it.id }) { discussion ->
                TrendingDiscussionCard(
                    discussion = discussion,
                    onClick = { onDiscussionClick(discussion.id) }
                )
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

            items(suggestedGroups, key = { "suggested_${it.id}" }) { group ->
                GroupCard(
                    group = group,
                    showJoin = true,
                    onClick = { onGroupClick(group.id) }   // FIX: group.id is Long, matches
                )
            }
        }
    }
}

// ── Discover Tab ───────────────────────────────────────────────────────────────

@Composable
private fun DiscoverContent(
    suggestedGroups: List<FanClubResponse>,
    trendingDiscussions: List<TrendingDiscussion>,
    onGroupClick: (Long) -> Unit,            // FIX: Long
    onDiscussionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
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

        items(suggestedGroups, key = { it.id }) { group ->
            GroupCard(
                group = group,
                showJoin = true,
                onClick = { onGroupClick(group.id) }       // FIX: group.id is Long, matches
            )
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

        items(trendingDiscussions, key = { it.id }) { discussion ->
            TrendingDiscussionCard(
                discussion = discussion,
                onClick = { onDiscussionClick(discussion.id) }
            )
        }
    }
}

// ── Group Card ─────────────────────────────────────────────────────────────────

@Composable
private fun GroupCard(
    group: FanClubResponse,
    showJoin: Boolean,
    onClick: () -> Unit
) {
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
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = group.photo_url,
                contentDescription = group.name
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "👥", fontSize = 11.sp)
                Text(
                    text = "${formatMemberCount(group.members_count)} members",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (!group.description.isNullOrBlank()) {
                Text(
                    text = group.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Right side: join button or nothing
        if (showJoin) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentBlue)
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (group.is_member) "Joined" else "Join",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Trending Discussion Card ───────────────────────────────────────────────────

@Composable
private fun TrendingDiscussionCard(
    discussion: TrendingDiscussion,
    onClick: () -> Unit
) {
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
                Text(
                    text = "${discussion.replyCount} replies",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(text = "•", color = TextSecondary, fontSize = 12.sp)
                val likesText = if (discussion.likeCount >= 1000f)
                    "${"%.1f".format(discussion.likeCount / 1000f)}K"
                else
                    "${discussion.likeCount.toInt()}"
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

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun DiscussionsScreenMyGroupsPreview() {
    DiscussionsScreenContent(
        userFanClubs = sampleSuggestedGroups.take(2),
        suggestedGroups = sampleSuggestedGroups,
        trendingDiscussions = sampleTrendingDiscussions,
        onGroupClick = {},
        onDiscussionClick = {},
        onNavigateBack = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF080C14)
@Composable
fun DiscussionsScreenEmptyGroupsPreview() {
    DiscussionsScreenContent(
        userFanClubs = emptyList(),
        suggestedGroups = sampleSuggestedGroups,
        trendingDiscussions = sampleTrendingDiscussions,
        onGroupClick = {},
        onDiscussionClick = {},
        onNavigateBack = {}
    )
}