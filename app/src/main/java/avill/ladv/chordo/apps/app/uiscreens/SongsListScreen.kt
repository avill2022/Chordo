package avill.ladv.chordo.apps.app.uiscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
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

@Composable
fun SongsListScreen(
    songs: List<Song>,
    playlists: List<Playlist>,
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
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tempoViewModel: TempoViewModel,
    audioHelper: AudioHelper
) {
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
                                Text(if (selectedTab == 2) "Search playlists..." else "Search songs or artists...") 
                            },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        //add an 'Options Menu' with sync, upload and download, import, and export (json) and options
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
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Playlists") },
                    label = { Text("Playlists") }
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
            }
        },
        floatingActionButton = {
            if (selectedTab < 2) {
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Song")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                3 -> TempoApp(tempoViewModel, audioHelper)
                4 -> TunerApp()
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
                                    onClick = { onSongClick(song) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.name, fontSize = 18.sp)
            Text(text = song.folder, fontSize = 14.sp)
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
        Song(name = "Wonderwall", folder = "Oasis", tone = "G", chords = "", rhythm = "", tempo = "", content = "", tab = "TAB", structure = "", harmony = "", melody = "", author = "", urlsong = "", urltutorial = "", urlmidi = "", urlgpt = "", urlpartiture = ""),
        Song(name = "Wish You Were Here", folder = "Pink Floyd", tone = "G", chords = "", rhythm = "", tempo = "", content = "", tab = "", structure = "", harmony = "", melody = "", author = "", urlsong = "", urltutorial = "", urlmidi = "", urlgpt = "", urlpartiture = "")
    )
    val samplePlaylists = listOf(
        Playlist(id = 1, name = "My Favorites"),
        Playlist(id = 2, name = "Rock")
    )
    
    MaterialTheme {
        SongsListScreen(
            songs = sampleSongs,
            playlists = samplePlaylists,
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
            selectedTab = 0,
            onTabSelected = {},
            tempoViewModel = TempoViewModel(),
            audioHelper = AudioHelper(LocalContext.current)
        )
    }
}
