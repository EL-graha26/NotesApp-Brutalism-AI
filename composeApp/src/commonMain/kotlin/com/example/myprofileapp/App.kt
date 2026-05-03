package com.example.myprofileapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myprofileapp.data.Note
import com.example.myprofileapp.data.NotesUiState
import com.example.myprofileapp.navigation.BottomNavItem
import com.example.myprofileapp.navigation.Screen
import com.example.myprofileapp.screens.*
import com.example.myprofileapp.viewmodel.ProfileViewModel
import com.example.myprofileapp.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val profileViewModel: ProfileViewModel = koinInject()
    val notesViewModel: NotesViewModel = koinInject()
    val uiState by profileViewModel.uiState.collectAsState()
    val notesUiState by notesViewModel.uiState.collectAsState()
    val searchQuery by notesViewModel.searchQuery.collectAsState()
    val notesList = when (notesUiState) {
        is NotesUiState.Success -> (notesUiState as NotesUiState.Success).notes
        else -> emptyList<Note>()
    }
    val colorScheme = if (uiState.isDarkMode) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val bottomNavItems = listOf(BottomNavItem.Notes, BottomNavItem.Favorites, BottomNavItem.Profile)
        val showBottomNav = bottomNavItems.any { it.route == currentRoute }

        val NeuBorder = Color(0xFF000000)
        val NeuYellow = Color(0xFFFFE600)
        val NeuPink = Color(0xFFFF90E8)
        val NeuGreen = Color(0xFF4ADE80)
        val bgColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF4F4F0)
        val textColor = if (uiState.isDarkMode) Color.White else NeuBorder

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = bgColor, modifier = Modifier.width(300.dp).border(4.dp, NeuBorder)) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("MENU UTAMA", modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp), fontSize = 28.sp, fontWeight = FontWeight.Black, color = textColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    val drawerItems = listOf(
                        Triple(Screen.Notes.route, Icons.Default.Home, "Semua Catatan"),
                        Triple(Screen.Favorites.route, Icons.Default.Favorite, "Catatan Favorit"),
                        Triple(Screen.Profile.route, Icons.Default.Person, "Profil Saya")
                    )

                    drawerItems.forEach { (route, icon, label) ->
                        val isSelected = currentRoute == route
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { navController.navigate(route) { popUpTo(Screen.Notes.route) { saveState = true }; launchSingleTop = true; restoreState = true }; scope.launch { drawerState.close() } }) {
                            if (isSelected) Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorder, RoundedCornerShape(8.dp)))
                            Row(modifier = Modifier.fillMaxWidth().background(if (isSelected) NeuPink else bgColor, RoundedCornerShape(8.dp)).border(if (isSelected) 3.dp else 0.dp, if (isSelected) NeuBorder else Color.Transparent, RoundedCornerShape(8.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, tint = if (isSelected) NeuBorder else textColor, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(16.dp))
                                Text(label, fontWeight = FontWeight.Black, color = if (isSelected) NeuBorder else textColor, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (uiState.isDarkMode) "Mode Terang" else "Mode Gelap", fontWeight = FontWeight.Black, color = NeuBorder, fontSize = 16.sp)
                            Switch(checked = uiState.isDarkMode, onCheckedChange = { profileViewModel.toggleDarkMode(it) }, colors = SwitchDefaults.colors(checkedThumbColor = NeuBorder, checkedTrackColor = Color.White, uncheckedThumbColor = Color.White, uncheckedTrackColor = NeuBorder, uncheckedBorderColor = NeuBorder))
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (showBottomNav) {
                        TopAppBar(
                            title = { Text("NOTES", fontWeight = FontWeight.Black, fontSize = 28.sp, color = textColor) },
                            navigationIcon = {
                                Box(modifier = Modifier.padding(start = 16.dp).clickable { scope.launch { drawerState.open() } }.background(NeuGreen, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                                    Icon(Icons.Default.Menu, contentDescription = null, tint = NeuBorder)
                                }
                            },
                            actions = {
                                Box(modifier = Modifier.padding(end = 16.dp).clickable { navController.navigate(Screen.AiAssistant.route) }.background(NeuYellow, RoundedCornerShape(8.dp)).border(3.dp, NeuBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = NeuBorder)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
                        )
                    }
                },
                bottomBar = {
                    if (showBottomNav) {
                        Box(modifier = Modifier.fillMaxWidth().background(bgColor).border(4.dp, NeuBorder)) {
                            NavigationBar(containerColor = bgColor, tonalElevation = 0.dp, modifier = Modifier.height(72.dp)) {
                                bottomNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    NavigationBarItem(
                                        icon = {
                                            Box(modifier = Modifier.background(if (isSelected) NeuPink else Color.Transparent, RoundedCornerShape(12.dp)).border(if (isSelected) 3.dp else 0.dp, if (isSelected) NeuBorder else Color.Transparent, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp, vertical = 8.dp)) {
                                                Icon(item.icon, contentDescription = null, modifier = Modifier.size(28.dp))
                                            }
                                        },
                                        selected = isSelected,
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NeuBorder, indicatorColor = Color.Transparent, unselectedIconColor = textColor.copy(alpha = 0.5f)),
                                        onClick = { navController.navigate(item.route) { popUpTo(route = Screen.Notes.route) { saveState = true }; launchSingleTop = true; restoreState = true } }
                                    )
                                }
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (currentRoute == Screen.Notes.route) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val translation by animateFloatAsState(targetValue = if (isPressed) 6f else 0f, animationSpec = tween(100))

                        Box(modifier = Modifier.size(72.dp).clickable(interactionSource = interactionSource, indication = null) { navController.navigate(Screen.AddNote.route) }) {
                            Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorder, RoundedCornerShape(16.dp)))
                            Box(modifier = Modifier.matchParentSize().offset(translation.dp, translation.dp).background(NeuGreen, RoundedCornerShape(16.dp)).border(3.dp, NeuBorder, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = NeuBorder, modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(navController = navController, startDestination = Screen.Notes.route, modifier = Modifier.padding(innerPadding)) {
                    composable(Screen.Notes.route) {
                        NoteListScreen(
                            uiState = notesUiState, searchQuery = searchQuery, onSearchChanged = { notesViewModel.onSearchQueryChanged(it) },
                            isDarkMode = uiState.isDarkMode, onNoteClick = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                            onToggleFavorite = { noteId, isFav -> notesViewModel.toggleFavorite(noteId, isFav) }
                        )
                    }
                    composable(Screen.Favorites.route) {
                        FavoritesScreen(
                            notes = notesList, isDarkMode = uiState.isDarkMode, onNoteClick = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                            onToggleFavorite = { noteId -> val note = notesList.find { it.id == noteId }; if (note != null) notesViewModel.toggleFavorite(noteId, note.isFavorite) }
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(uiState = uiState, onEditProfile = { newName, newBio -> profileViewModel.updateProfile(newName, newBio) }, onToggleDarkMode = { isDark -> profileViewModel.toggleDarkMode(isDark) })
                    }
                    composable(Screen.AddNote.route) {
                        AddNoteScreen(isDarkMode = uiState.isDarkMode, onSave = { title, content -> notesViewModel.addNote(title, content) }, onBack = { navController.popBackStack() })
                    }
                    composable(route = Screen.NoteDetail.route, arguments = listOf(navArgument("noteId") { type = NavType.IntType })) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                        val note = notesList.find { it.id == noteId }
                        NoteDetailScreen(
                            note = note, isDarkMode = uiState.isDarkMode, onBack = { navController.popBackStack() },
                            onEditClick = { id -> navController.navigate(Screen.EditNote.createRoute(id)) },
                            onDeleteClick = { id -> notesViewModel.deleteNote(id); navController.popBackStack() }
                        )
                    }
                    composable(route = Screen.EditNote.route, arguments = listOf(navArgument("noteId") { type = NavType.IntType })) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                        val note = notesList.find { it.id == noteId }
                        EditNoteScreen(noteId = noteId, note = note, isDarkMode = uiState.isDarkMode, onSave = { id, title, content -> notesViewModel.editNote(id, title, content) }, onBack = { navController.popBackStack() })
                    }
                    composable(Screen.AiAssistant.route) {
                        AiAssistantScreen(isDarkMode = uiState.isDarkMode, onBack = { navController.popBackStack() })
                    }
                    // ========================

                }
            }
        }
    }
}