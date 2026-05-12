@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package avill.ladv.chordo.apps.app

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import avill.ladv.chordo.apps.app.navigation.Chordo
import avill.ladv.chordo.apps.app.navigation.NavHostMain
import avill.ladv.chordo.apps.app.navigation.addNewScreens



data class Song(
    val name: String,
    val folder: String,
    val content: String
)

data class Chords(
    val songs: List<Song>,
    val title: String,
    val id: Int
)

@Composable
fun SongsListScreen(
    chords: Chords,
    onSongClick: (Int) -> Unit
) {
    LazyColumn {
        items(chords.songs.size) { song ->
            SongItem(song = chords.songs.get(song), onClick = {
                onSongClick(song)
            })
        }
    }
}
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() }
            .padding(16.dp).fillMaxWidth()
    ) {
        Text(text = song.name, fontSize = 18.sp)
        Text(text = song.folder, fontSize = 14.sp)
    }
}


@Composable
fun LyricsScreen(lyrics: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = lyrics,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}











@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NamePreviewDark() {
    ChordoApp(
        viewModel = hiltViewModel() // Assuming MainViewModel has a default constructor
    )
}
@Preview
@Composable
fun NamePreview() {
    ChordoApp(
        viewModel = hiltViewModel() // Assuming MainViewModel has a default constructor
    )
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChordoApp(viewModel: ChordoViewModel) {
    val context = LocalContext.current
    //val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    viewModel.getTabs()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = Chordo.Splash.route
    ) {
        composable(Chordo.Splash.route) {

        }
        composable(Chordo.OnBoarding.route) {

        }
        composable(Chordo.Permissions.route) {

        }
        //Components
        composable(Chordo.List.route) {
            SongsListScreen(
                chords = viewModel.chords.value,
                onSongClick = { songId ->
                    navController.navigate("lyrics/$songId")
                }
            )
        }
        composable("lyrics/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toInt()
            val chord = songId?.let { viewModel.getSongById(it) }

            chord?.let {
                LyricsScreen(lyrics = it.content)
            }
        }
    }
}