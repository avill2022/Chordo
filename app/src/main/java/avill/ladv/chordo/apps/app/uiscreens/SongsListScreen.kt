package avill.ladv.chordo.apps.app.uiscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import avill.ladv.chordo.R
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
    audioHelper: AudioHelper,
    isAudioPermissionGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    if (songToDelete != null) {
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            title = { Text(stringResource(R.string.delete_song_title)) },
            text = { Text(stringResource(R.string.delete_song_confirmation, songToDelete?.name ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    songToDelete?.let { onDeleteSong(it) }
                    songToDelete = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { songToDelete = null }) {
                    Text(stringResource(R.string.cancel))
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
                                        2 -> stringResource(R.string.search_playlists)
                                        1 -> stringResource(R.string.search_favorites)
                                        else -> stringResource(R.string.search_songs_artists)
                                    }
                                ) 
                            },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.options))
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.export_json)) },
                                    onClick = { onExportClick(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Save, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.import_json)) },
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
                                text = { Text(stringResource(R.string.favorites)) },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { onTabSelected(2) },
                                text = { Text(stringResource(R.string.playlists)) },
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
                    icon = { Icon(Icons.Default.List, contentDescription = stringResource(R.string.all_songs)) },
                    label = { Text(stringResource(R.string.all)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1 || selectedTab == 2,
                    onClick = { 
                        if (selectedTab != 1 && selectedTab != 2) {
                            onTabSelected(1)
                        }
                    },
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = stringResource(R.string.library)) },
                    label = { Text(stringResource(R.string.library)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = stringResource(R.string.tempo)) },
                    label = { Text(stringResource(R.string.tempo)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(Icons.Default.MusicNote, contentDescription = stringResource(R.string.tuner)) },
                    label = { Text(stringResource(R.string.tuner)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { onTabSelected(5) },
                    icon = { Icon(Icons.Default.Build, contentDescription = stringResource(R.string.tools)) },
                    label = { Text(stringResource(R.string.tools)) }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab < 3) {
                FloatingActionButton(onClick = onCreateClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,){
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_song))
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
                    4 -> TunerApp(
                        isAudioPermissionGranted = isAudioPermissionGranted,
                        onRequestPermission = onRequestPermission
                    )
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
                                            Text(stringResource(R.string.no_playlists_found), color = MaterialTheme.colorScheme.outline)
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
                                                text = if (selectedTab == 1) stringResource(R.string.no_favorites_yet) else stringResource(R.string.no_songs_found),
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
                    text = stringResource(R.string.tab_label),
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
            audioHelper = AudioHelper(LocalContext.current),
            isAudioPermissionGranted = true,
            onRequestPermission = {}
        )
    }
}
