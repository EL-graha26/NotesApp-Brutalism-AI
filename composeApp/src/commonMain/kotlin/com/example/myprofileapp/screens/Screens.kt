package com.example.myprofileapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.data.Note
import com.example.myprofileapp.data.NotesUiState
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import org.koin.compose.koinInject

val NeuPink = Color(0xFFFF90E8)
val NeuYellow = Color(0xFFFFE600)
val NeuGreen = Color(0xFF4ADE80)
val NeuBlue = Color(0xFF90C1FF)
val NeuRed = Color(0xFFFF6B6B)
val NeuBorder = Color(0xFF000000)
val NeuBgLight = Color(0xFFF4F4F0)
val NeuBgDark = Color(0xFF1E1E1E)

fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    return "${localDateTime.dayOfMonth} ${monthNames[localDateTime.monthNumber - 1]} ${localDateTime.year}, ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
}

fun formatRealtimeClock(instant: Instant): String {
    val time = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

@Composable
fun GlobalOfflineBanner() {
    val networkMonitor: com.example.myprofileapp.platform.NetworkMonitor = koinInject()
    val isConnected by networkMonitor.observeConnectivity().collectAsState(initial = true)
    var showBanner by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            showBanner = true
            delay(3000)
            showBanner = false
        } else {
            showBanner = false
        }
    }

    AnimatedVisibility(
        visible = showBanner,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.offset(4.dp, 4.dp).background(NeuBorder, RoundedCornerShape(8.dp)).matchParentSize()
            )
            Row(
                modifier = Modifier.background(NeuRed, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CloudOff, contentDescription = null, tint = NeuBorder, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Kamu sedang offline", color = NeuBorder, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun AppHeader(isDarkMode: Boolean) {
    var currentTime by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while (true) { delay(1000); currentTime = Clock.System.now() }
    }
    val textColor = if (isDarkMode) Color.White else NeuBorder

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Halo, Piela", fontSize = 36.sp, fontWeight = FontWeight.Black, color = textColor, letterSpacing = (-1).sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tuliskan ide cemerlangmu hari ini.", fontSize = 16.sp, color = textColor.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
        }
        Box {
            Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorder, RoundedCornerShape(8.dp)))
            Box(modifier = Modifier.background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(formatRealtimeClock(currentTime), fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeuBorder)
            }
        }
    }
}

@Composable
fun SearchBarClean(searchQuery: String, onSearchChanged: (String) -> Unit, isDarkMode: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorder, RoundedCornerShape(12.dp)))
        TextField(
            value = searchQuery, onValueChange = onSearchChanged,
            placeholder = { Text("Cari catatanmu...", color = Color.DarkGray, fontWeight = FontWeight.Bold) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeuBorder) },
            modifier = Modifier.fillMaxWidth().height(56.dp).border(3.dp, NeuBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = NeuBorder, fontSize = 16.sp, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
    }
}

@Composable
fun CategoryChips(selectedCategory: String, onCategorySelected: (String) -> Unit, isDarkMode: Boolean) {
    val categories = listOf("Semua", "Penting \uD83D\uDCCC", "Ide \uD83D\uDCA1", "Tugas \uD83D\uDCDD")
    LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp), contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val translation by animateFloatAsState(targetValue = if (isPressed) 4f else 0f, animationSpec = tween(100))

            Box(modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) { onCategorySelected(category) }) {
                Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorder, RoundedCornerShape(8.dp)))
                Box(modifier = Modifier.offset(translation.dp, translation.dp).background(if (isSelected) NeuPink else Color.White, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(text = category, color = NeuBorder, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun NeuNoteCard(note: Note, isDarkMode: Boolean, onNoteClick: (Int) -> Unit, onToggleFavorite: (Int) -> Unit) {
    val cardColors = listOf(NeuGreen, NeuPink, NeuBlue, NeuYellow)
    val bgColor = cardColors[note.id.hashCode() % cardColors.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val translation by animateFloatAsState(targetValue = if (isPressed) 6f else 0f, animationSpec = tween(100))

    Box(modifier = Modifier.fillMaxWidth().clickable(interactionSource = interactionSource, indication = null) { onNoteClick(note.id) }) {
        Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorder, RoundedCornerShape(12.dp)))
        Column(modifier = Modifier.fillMaxWidth().offset(translation.dp, translation.dp).background(bgColor, RoundedCornerShape(12.dp)).border(3.dp, NeuBorder, RoundedCornerShape(12.dp)).padding(16.dp)) {
            Text(note.title.ifEmpty { "Tanpa Judul" }, fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeuBorder, lineHeight = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(note.content, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeuBorder.copy(alpha = 0.9f), maxLines = 4, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp)).border(2.dp, NeuBorder, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 4.dp)) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = NeuBorder, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(formatTimestamp(note.timestamp).split(",")[0], fontSize = 12.sp, color = NeuBorder, fontWeight = FontWeight.Black)
                }
                IconButton(onClick = { onToggleFavorite(note.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = if (note.isFavorite) NeuRed else NeuBorder, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

@Composable
fun NoteListScreen(uiState: NotesUiState, searchQuery: String, onSearchChanged: (String) -> Unit, isDarkMode: Boolean, onNoteClick: (Int) -> Unit, onToggleFavorite: (Int, Boolean) -> Unit) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    var isSortDescending by remember { mutableStateOf(true) }
    val bgColor = if (isDarkMode) NeuBgDark else NeuBgLight

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        GlobalOfflineBanner()
        AppHeader(isDarkMode)
        SearchBarClean(searchQuery, onSearchChanged, isDarkMode)
        CategoryChips(selectedCategory, { selectedCategory = it }, isDarkMode)

        when (uiState) {
            is NotesUiState.Loading -> { Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = NeuBorder, strokeWidth = 4.dp) } }
            is NotesUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada catatan.", color = if(isDarkMode) Color.White else NeuBorder, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            is NotesUiState.Success -> {
                val filteredNotes = when (selectedCategory) {
                    "Penting \uD83D\uDCCC" -> uiState.notes.filter { it.isFavorite }
                    "Ide \uD83D\uDCA1" -> uiState.notes.filter { it.title.contains("ide", true) || it.content.contains("ide", true) }
                    "Tugas \uD83D\uDCDD" -> uiState.notes.filter { it.title.contains("tugas", true) || it.content.contains("tugas", true) }
                    else -> uiState.notes
                }
                val sortedNotes = if (isSortDescending) filteredNotes.sortedByDescending { it.timestamp } else filteredNotes.sortedBy { it.timestamp }

                if (sortedNotes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) { Text("Tidak ada catatan di sini.", color = if(isDarkMode) Color.White else NeuBorder, fontWeight = FontWeight.Bold) }
                } else {
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Catatan Anda", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if(isDarkMode) Color.White else NeuBorder)
                        Box(modifier = Modifier.background(NeuYellow, RoundedCornerShape(8.dp)).border(2.dp, NeuBorder, RoundedCornerShape(8.dp)).clickable { isSortDescending = !isSortDescending }.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeuBorder)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isSortDescending) "Terbaru" else "Terlama", color = NeuBorder, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2), modifier = Modifier.fillMaxSize().weight(1f),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp), verticalItemSpacing = 16.dp
                    ) { items(sortedNotes) { note -> NeuNoteCard(note = note, isDarkMode = isDarkMode, onNoteClick = onNoteClick, onToggleFavorite = { noteId -> onToggleFavorite(noteId, note.isFavorite) }) } }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(notes: List<Note>, isDarkMode: Boolean, onNoteClick: (Int) -> Unit, onToggleFavorite: (Int) -> Unit) {
    val favoriteNotes = notes.filter { it.isFavorite }
    val bgColor = if (isDarkMode) NeuBgDark else NeuBgLight

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        GlobalOfflineBanner()
        Text("Favorit Saya", fontSize = 36.sp, fontWeight = FontWeight.Black, color = if (isDarkMode) Color.White else NeuBorder, modifier = Modifier.padding(start = 20.dp, top = 32.dp, bottom = 24.dp), letterSpacing = (-1).sp)

        if (favoriteNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) { Text("Belum ada yang ditandai.", color = if(isDarkMode) Color.White else NeuBorder, fontWeight = FontWeight.Bold) }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2), modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp), verticalItemSpacing = 16.dp
            ) { items(favoriteNotes) { note -> NeuNoteCard(note = note, isDarkMode = isDarkMode, onNoteClick = onNoteClick, onToggleFavorite = { noteId -> onToggleFavorite(noteId) }) } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(isDarkMode: Boolean, onSave: (String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val bgColor = if (isDarkMode) NeuBgDark else NeuBgLight
    val textColor = if (isDarkMode) Color.White else NeuBorder

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp).clickable { onBack() }.background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = NeuBorder)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp).clickable { onSave(title, content); onBack() }.background(NeuGreen, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text("Simpan", color = NeuBorder, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(innerPadding).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Judul Catatan", fontSize = 36.sp, fontWeight = FontWeight.Black, color = textColor.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(fontSize = 36.sp, fontWeight = FontWeight.Black, color = textColor),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            TextField(
                value = content, onValueChange = { content = it },
                placeholder = { Text("Mulai menulis...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth().weight(1f), textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = textColor),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(noteId: Int, note: Note?, isDarkMode: Boolean, onSave: (Int, String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    val bgColor = if (isDarkMode) NeuBgDark else NeuBgLight
    val textColor = if (isDarkMode) Color.White else NeuBorder

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp).clickable { onBack() }.background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = NeuBorder)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp).clickable { onSave(noteId, title, content); onBack() }.background(NeuGreen, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text("Update", color = NeuBorder, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(innerPadding).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = title, onValueChange = { title = it }, textStyle = LocalTextStyle.current.copy(fontSize = 36.sp, fontWeight = FontWeight.Black, color = textColor),
                modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            TextField(
                value = content, onValueChange = { content = it }, textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = textColor),
                modifier = Modifier.fillMaxWidth().weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(note: Note?, isDarkMode: Boolean, onBack: () -> Unit, onEditClick: (Int) -> Unit, onDeleteClick: (Int) -> Unit) {
    if (note == null) return
    val bgColor = if (isDarkMode) NeuBgDark else NeuBgLight
    val textColor = if (isDarkMode) Color.White else NeuBorder

    // Siapkan Mesin AI khusus untuk membaca catatan ini
    val scope = rememberCoroutineScope()
    val aiService = remember { com.example.myprofileapp.platform.AiService(io.ktor.client.HttpClient { install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { io.ktor.serialization.kotlinx.json.json(kotlinx.serialization.json.Json { ignoreUnknownKeys = true }) } }) }
    var aiSummary by remember { mutableStateOf<String?>(null) }
    var isAiLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp).clickable { onBack() }.background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = NeuBorder)
                    }
                },
                actions = {
                    Box(modifier = Modifier.clickable { onDeleteClick(note.id) }.background(NeuRed, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = NeuBorder)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.padding(end = 16.dp).clickable { onEditClick(note.id) }.background(NeuBlue, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeuBorder)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(innerPadding).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(note.title.ifEmpty { "Tanpa Judul" }, fontSize = 36.sp, fontWeight = FontWeight.Black, color = textColor, lineHeight = 42.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(NeuPink, RoundedCornerShape(8.dp)).border(2.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = NeuBorder, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(formatTimestamp(note.timestamp), fontSize = 14.sp, color = NeuBorder, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(note.content, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = textColor.copy(alpha = 0.9f), lineHeight = 30.sp)

            Box(modifier = Modifier.padding(bottom = 16.dp).clickable {
                if (!isAiLoading) {
                    isAiLoading = true
                    scope.launch {
                        // AI Prompt: Memaksa AI membaca konteks catatan yang sedang dibuka
                        val prompt = "Tolong rangkum catatan ini menjadi 2 atau 3 poin penting yang singkat. Judul: ${note.title}. Isi: ${note.content}"
                        aiSummary = aiService.sendMessage(prompt)
                        isAiLoading = false
                    }
                }
            }) {
                Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorder, RoundedCornerShape(8.dp)))
                Row(
                    modifier = Modifier.fillMaxWidth().background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NeuBorder, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI SEDANG MEMBACA...", color = NeuBorder, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    } else {
                        Text("⚡ RANGKUM DENGAN AI", color = NeuBorder, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }

            if (aiSummary != null) {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorder, RoundedCornerShape(12.dp)))
                    Column(modifier = Modifier.fillMaxWidth().background(NeuGreen, RoundedCornerShape(12.dp)).border(3.dp, NeuBorder, RoundedCornerShape(12.dp)).padding(16.dp)) {
                        Text("✨ HASIL RANGKUMAN AI:", fontSize = 14.sp, fontWeight = FontWeight.Black, color = NeuBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(aiSummary ?: "", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuBorder, lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}