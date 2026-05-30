@file:OptIn(ExperimentalMaterial3Api::class)

package avill.ladv.chordo.apps.app

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.apps.app.navigation.Chordo
import avill.ladv.chordo.apps.app.uiscreens.LyricsScreen
import avill.ladv.chordo.apps.app.uiscreens.SongEditScreen
import avill.ladv.chordo.apps.app.uiscreens.SongsListScreen

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NamePreviewDark() {
    ChordoApp(
        viewModel = hiltViewModel(),
        tempoViewModel = hiltViewModel(),
        audioHelper = AudioHelper(LocalContext.current)
    )
}

@Preview
@Composable
fun NamePreview() {
    ChordoApp(
        viewModel = hiltViewModel(),
        tempoViewModel = hiltViewModel(),
        audioHelper = AudioHelper(LocalContext.current)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChordoApp(
    viewModel: ChordoViewModel,
    tempoViewModel: TempoViewModel = hiltViewModel(),
    audioHelper: AudioHelper
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    viewModel.getTabs()
    val uiState by viewModel.uiState.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(viewModel.exportSongsJson().toByteArray())
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                viewModel.importSongsJson(json)
            }
        }
    }

    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = Chordo.List.route
    ) {
        composable(Chordo.OnBoarding.route) {
            // OnBoarding Screen
        }
        composable(Chordo.Permissions.route) {
            // Permissions Screen
        }
        
        composable(Chordo.List.route) {
            SongsListScreen(
                songs = uiState.filteredSongs,
                searchText = uiState.searchText,
                isLoading = uiState.isLoading,
                onSearchTextChange = { viewModel.onSearchTextChange(it) },
                onSongClick = { song ->
                    val index = viewModel.chords.value.songs.indexOf(song)
                    if (index != -1) {
                        navController.navigate("lyrics/$index")
                    }
                },
                onCreateClick = {
                    navController.navigate(Chordo.Edit.route)
                },
                onSyncClick = { viewModel.getTabs() },
                onUploadClick = { viewModel.uploadChords() },
                onDownloadClick = { viewModel.downloadChords() },
                onExportClick = {
                    exportLauncher.launch("chordo_backup.json")
                },
                onImportClick = {
                    importLauncher.launch(arrayOf("application/json"))
                },
                onDeleteSong = { viewModel.deleteSong(it) },
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) },
                playlists = uiState.playlists,
                onPlaylistClick = {},
                tempoViewModel = tempoViewModel,
                audioHelper = audioHelper,

            )
        }

        composable(Chordo.Edit.route) {
            SongEditScreen(
                song = null,
                onSave = {
                    viewModel.saveSong(it)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Chordo.Edit.route + "/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toInt()
            val song = songId?.let { viewModel.getSongById(it) }
            SongEditScreen(
                song = song,
                onSave = {
                    viewModel.saveSong(it)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("lyrics/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toInt()
            val song = songId?.let { viewModel.getSongById(it) }

            song?.let {
                viewModel.checkIfFavorite(it)
                LyricsScreen(
                    song = it,
                    isFavorite = uiState.isCurrentSongFavorite,
                    playlists = uiState.playlists,
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    onAddToPlaylistClick = { playlistId -> viewModel.addSongToPlaylist(it, playlistId) },
                    onCreatePlaylist = { name -> viewModel.createPlaylist(name) },
                    onTranspose = { semitones -> viewModel.transposeSong(it, semitones) },
                    onRestore = { viewModel.restoreSong(it) },
                    onEditClick = {
                        navController.navigate(Chordo.Edit.route + "/$songId")
                    },
                    onBackClick = { navController.popBackStack() },
                    tempoViewModel = tempoViewModel,
                    audioHelper = audioHelper,
                    onTempoChange = {
                        //tempoViewModel.setBPM(it)
                    }
                )
            }
        }
    }
}
