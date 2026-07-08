package com.partner.cinepulse.ui.screens.lists

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partner.cinepulse.data.remote.models.CollectionResponse
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.partner.cinepulse.data.remote.models.CollectionItemResponse
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.*
import com.partner.cinepulse.utils.Resource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partner.cinepulse.data.remote.models.CollectionCreateRequest
import com.partner.cinepulse.data.remote.models.CollectionRenameRequest
import com.partner.cinepulse.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListsViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _listsState = MutableStateFlow<Resource<List<CollectionResponse>>>(Resource.Loading())
    val listsState: StateFlow<Resource<List<CollectionResponse>>> = _listsState.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            contentRepository.listCollections().collect { result ->
                _listsState.value = result
            }
        }
    }

    fun createCollection(name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            contentRepository.createCollection(CollectionCreateRequest(name)).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadLists()
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to create collection")
                    }
                    else -> {}
                }
            }
        }
    }

    fun renameCollection(collectionId: Int, name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            contentRepository.renameCollection(collectionId, CollectionRenameRequest(name)).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadLists()
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to rename collection")
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteCollection(collectionId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            contentRepository.deleteCollection(collectionId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadLists()
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Failed to delete collection")
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListsScreen(
    onBackClick: () -> Unit,
    onCollectionClick: (Int, String) -> Unit,
    viewModel: UserListsViewModel = hiltViewModel()
) {
    val listsState by viewModel.listsState.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createError by remember { mutableStateOf<String?>(null) }

    var showRenameDialog by remember { mutableStateOf<CollectionResponse?>(null) }
    var renameName by remember { mutableStateOf("") }
    var renameError by remember { mutableStateOf<String?>(null) }

    var showDeleteConfirm by remember { mutableStateOf<CollectionResponse?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Your Lists",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createName = ""
                    createError = null
                    showCreateDialog = true
                },
                containerColor = AccentBlue,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Collection")
            }
        },
        containerColor = BgDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDark)
        ) {
            when (val state = listsState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentBlue
                    )
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message ?: "Failed to load lists",
                            color = AccentRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadLists() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is Resource.Success -> {
                    val collections = state.data ?: emptyList()
                    if (collections.isEmpty()) {
                        Text(
                            text = "No collections created yet",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(collections) { collection ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCollectionClick(collection.id, collection.name)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = CardDark),
                                    border = BorderStroke(1.dp, CardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OverlapPosterStack(items = collection.items)
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = collection.name,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${collection.item_count} items" + if (collection.is_watchlist) " • Watchlist" else "",
                                                color = TextSecondary,
                                                fontSize = 13.sp
                                            )
                                        }

                                        if (!collection.is_watchlist) {
                                            IconButton(
                                                onClick = {
                                                    renameName = collection.name
                                                    renameError = null
                                                    showRenameDialog = collection
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Rename",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    deleteError = null
                                                    showDeleteConfirm = collection
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = AccentRed,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Collection Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Collection", color = TextPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = createName,
                        onValueChange = { createName = it },
                        label = { Text("Collection Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (createError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(createError!!, color = AccentRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createCollection(
                            createName,
                            onSuccess = { showCreateDialog = false },
                            onError = { createError = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardDark
        )
    }

    // Rename Collection Dialog
    showRenameDialog?.let { col ->
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Collection", color = TextPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = renameName,
                        onValueChange = { renameName = it },
                        label = { Text("Collection Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (renameError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(renameError!!, color = AccentRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameCollection(
                            col.id,
                            renameName,
                            onSuccess = { showRenameDialog = null },
                            onError = { renameError = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardDark
        )
    }

    // Delete Collection Confirmation Dialog
    showDeleteConfirm?.let { col ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Collection", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to delete the collection \"${col.name}\"? This action cannot be undone.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    if (deleteError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(deleteError!!, color = AccentRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCollection(
                            col.id,
                            onSuccess = { showDeleteConfirm = null },
                            onError = { deleteError = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardDark
        )
    }
}

@Composable
private fun OverlapPosterStack(items: List<CollectionItemResponse>) {
    val posters = items.mapNotNull {
        val url = (it.movie?.photo_url ?: it.tv_show?.photo_url)
        url?.takeIf { it.isNotEmpty() && it != "null" && it != "None" }
    }.take(3)

    Box(
        modifier = Modifier
            .size(width = 92.dp, height = 96.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (posters.isEmpty()) {
            repeat(3) { index ->
                val scale = 1f - (2 - index) * 0.08f
                val translationX = (index * 12).dp
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.translationX = translationX.toPx()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E293B).copy(alpha = 0.4f + index * 0.2f),
                                    Color(0xFF0F172A).copy(alpha = 0.4f + index * 0.2f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.05f + index * 0.02f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (index == 2) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        } else {
            val posterList = posters.reversed()
            posterList.forEachIndexed { idx, posterUrl ->
                val offsetStep = posters.size - 1 - idx
                val scale = 1f - offsetStep * 0.08f
                val translationX = (offsetStep * 12).dp
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.translationX = translationX.toPx()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
