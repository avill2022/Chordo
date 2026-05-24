package avill.ladv.chordo.apps.app.uiscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import avill.ladv.chordo.apps.app.TempoApp
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.apps.app.TunerApp
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.local.db.room.entities.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongsListScreen(
    songs: List<Song>,
    playlists: List<Playlist>,
    isLoading: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreateClick: () -> Unit,
    onSyncClick: () -> Unit,
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onDeleteSong: (Song) -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tempoViewModel: TempoViewModel,
    audioHelper: AudioHelper
) {
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    if (songToDelete != null) {
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            title = { Text("Delete Song") },
            text = { Text("Are you sure you want to delete '${songToDelete?.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    songToDelete?.let { onDeleteSong(it) }
                    songToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { songToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (selectedTab < 3) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, start = 16.dp, end = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = onSearchTextChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { 
                                Text(
                                    when (selectedTab) {
                                        2 -> "Search playlists..."
                                        1 -> "Search favorites..."
                                        else -> "Search songs or artists..."
                                    }
                                ) 
                            },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sync with server") },
                                    onClick = { onSyncClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Refresh, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Upload to server") },
                                    onClick = { onUploadClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.CloudUpload, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Download from server") },
                                    onClick = { onDownloadClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.CloudDownload, null) }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Export to JSON") },
                                    onClick = { onExportClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Save, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Import from JSON") },
                                    onClick = { onImportClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.DriveFolderUpload, null) }
                                )
                            }
                        }
                    }
                    if (selectedTab == 1 || selectedTab == 2) {
                        TabRow(
                            selectedTabIndex = if (selectedTab == 1) 0 else 1,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { onTabSelected(1) },
                                text = { Text("Favorites") },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { onTabSelected(2) },
                                text = { Text("Playlists") },
                                icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Default.List, contentDescription = "All Songs") },
                    label = { Text("All") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1 || selectedTab == 2,
                    onClick = { 
                        if (selectedTab != 1 && selectedTab != 2) {
                            onTabSelected(1)
                        }
                    },
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Tempo") },
                    label = { Text("Tempo") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(Icons.Default.MusicNote, contentDescription = "Tuner") },
                    label = { Text("Tuner") }
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { onTabSelected(5) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Tools") },
                    label = { Text("Tools") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab < 3) {
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Song")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    3 -> TempoApp(tempoViewModel, audioHelper)
                    4 -> TunerApp()
                    5 -> ToolsScreen()
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            if (selectedTab == 2) {
                                val filteredPlaylists = playlists.filter { it.name.contains(searchText, ignoreCase = true) }
                                if (filteredPlaylists.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No playlists found", color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                                itemsIndexed(filteredPlaylists) { _, playlist ->
                                    PlaylistItem(
                                        playlist = playlist,
                                        onClick = { onPlaylistClick(playlist) }
                                    )
                                }
                            } else {
                                if (songs.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (selectedTab == 1) "No favorites yet" else "No songs found",
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                                itemsIndexed(songs) { _, song ->
                                    SongItem(
                                        song = song,
                                        onClick = { onSongClick(song) },
                                        onLongClick = { songToDelete = song }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.name, fontSize = 18.sp)
            Text(text = song.author, fontSize = 14.sp)
        }

        if (song.tone.isNotEmpty()) {
            Text(
                text = song.tone,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (song.tab.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "TAB",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(text = playlist.name, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SongsListScreenPreview() {
    val sampleSongs = listOf(
        Song(name = "Wonderwall", author = "Oasis", tone = "G", chords = "", rhythm = "", tempo = "", content = "", tab = "TAB", structure = "", harmony = "", melody = "", folder = "", urlsong = "", urltutorial = "", urlmidi = "", urlgpt = "", urlpartiture = ""),
        Song(name = "Wish You Were Here", author = "Pink Floyd", tone = "G", chords = "", rhythm = "", tempo = "", content = "", tab = "", structure = "", harmony = "", melody = "", folder = "", urlsong = "", urltutorial = "", urlmidi = "", urlgpt = "", urlpartiture = "")
    )
    val samplePlaylists = listOf(
        Playlist(id = 1, name = "My Favorites"),
        Playlist(id = 2, name = "Rock")
    )
    
    MaterialTheme {
        SongsListScreen(
            songs = sampleSongs,
            playlists = samplePlaylists,
            isLoading = false,
            searchText = "",
            onSearchTextChange = {},
            onSongClick = {},
            onPlaylistClick = {},
            onCreateClick = {},
            onSyncClick = {},
            onUploadClick = {},
            onDownloadClick = {},
            onExportClick = {},
            onImportClick = {},
            onDeleteSong = {},
            selectedTab = 0,
            onTabSelected = {},
            tempoViewModel = TempoViewModel(),
            audioHelper = AudioHelper(LocalContext.current)
        )
    }
}
