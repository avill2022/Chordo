package avill.ladv.chordo.apps.app.uiscreens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import avill.ladv.chordo.apps.app.MetronomeTick
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.apps.app.helpers.ChordTransposer
import avill.ladv.chordo.apps.app.helpers.convertAmericanToLatinAdvanced
import avill.ladv.chordo.apps.app.helpers.extractTabsFlexible
import avill.ladv.chordo.apps.app.helpers.replaceTabsFlexible
import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.local.db.room.entities.Playlist
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
    song: Song,
    isFavorite: Boolean,
    playlists: List<Playlist>,
    onFavoriteClick: () -> Unit,
    onAddToPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onTranspose: (Int) -> Unit,
    onRestore: () -> Unit,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    tempoViewModel: TempoViewModel,
    audioHelper: AudioHelper
) {
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showTempoDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var isChordsRemoved by remember { mutableStateOf(false) }
    var isAmericanLatinNotation by remember { mutableStateOf(false) }
    var isMetronomePlaying by remember { mutableStateOf(false) }

    val bpm by tempoViewModel.bpm.collectAsState()

    val scrollState = rememberScrollState()
    var isAutoScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableIntStateOf(5) } // Default level 5

    // Sync tempoViewModel with song's tempo when song changes
    LaunchedEffect(song.name, song.folder) {
        song.tempo.toIntOrNull()?.let {
            tempoViewModel.setBPM(it)
        }
    }

    LaunchedEffect(isAutoScrolling, scrollSpeed) {
        if (isAutoScrolling) {
            while (true) {
                val delayTime = (100L / (scrollSpeed * 0.5 + 0.5)).toLong()
                scrollState.scrollBy(1f)
                delay(delayTime)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Song")
                    }
                    IconButton(onClick = { isAutoScrolling = !isAutoScrolling }) {
                        Icon(
                            imageVector = if (isAutoScrolling) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                            contentDescription = "Auto Scroll",
                            tint = if (isAutoScrolling) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showPlaylistDialog = true }) {
                        Icon(Icons.Default.List, contentDescription = "Add to Playlist")
                    }
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Transpose and Scroll Controls
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { onTranspose(-2) }) { Text("-1") }
                    OutlinedButton(onClick = { onTranspose(-1) }) { Text("-1/2") }
                    OutlinedButton(onClick = onRestore) { Text("Restore") }
                    OutlinedButton(onClick = { onTranspose(1) }) { Text("+1/2") }
                    OutlinedButton(onClick = { onTranspose(2) }) { Text("+1") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { isChordsRemoved = !isChordsRemoved }) {
                        Text(if (isChordsRemoved) "S" else "R")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { isAmericanLatinNotation = !isAmericanLatinNotation }) {
                        Text(if (isAmericanLatinNotation) "American" else "Latin")
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { showTempoDialog = true }) {
                        Text("Tempo: $bpm")
                    }

                    if(song.urlsong.isNotEmpty()){
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { /* Open URL */ }) {
                            Text("URL")
                        }
                    }
                }
                
                if (isAutoScrolling) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Speed: $scrollSpeed", style = MaterialTheme.typography.labelLarge)
                        Slider(
                            value = scrollSpeed.toFloat(),
                            onValueChange = { scrollSpeed = it.toInt() },
                            valueRange = 1f..9f,
                            steps = 7,
                            modifier = Modifier.padding(horizontal = 16.dp).weight(1f)
                        )
                        IconButton(onClick = { isAutoScrolling = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Stop Scroll")
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                val chordColor = MaterialTheme.colorScheme.primary
                val annotatedContent = remember(song.content, isChordsRemoved, chordColor, isAmericanLatinNotation) {
                    var textToProcess = replaceTabsFlexible(song.content)
                    
                    val activeRegex = if (isAmericanLatinNotation) {
                        textToProcess = convertAmericanToLatinAdvanced(textToProcess)
                        ChordTransposer.latinChordRegex
                    } else {
                        ChordTransposer.chordRegex
                    }
                    
                    if (isChordsRemoved) {
                        buildAnnotatedString { append(ChordTransposer.removeChords(textToProcess, activeRegex)) }
                    } else {
                        buildAnnotatedString {
                            val text = textToProcess
                            var lastIndex = 0
                            activeRegex.findAll(text).forEach { match ->
                                append(text.substring(lastIndex, match.range.first))
                                withStyle(style = SpanStyle(color = chordColor, fontWeight = FontWeight.Bold)) {
                                    append(match.value)
                                }
                                lastIndex = match.range.last + 1
                            }
                            append(text.substring(lastIndex))
                        }
                    }
                }

                Text(
                    text = annotatedContent,
                    modifier = Modifier.verticalScroll(scrollState)
                )
            }

            if (song.tab.isNotEmpty()) {
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "TABLATURE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = song.tab,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            ),
                            softWrap = false,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Add to Playlist") },
            text = {
                Column {
                    playlists.forEach { playlist ->
                        TextButton(
                            onClick = {
                                onAddToPlaylistClick(playlist.id)
                                showPlaylistDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(playlist.name)
                        }
                    }
                    HorizontalDivider()
                    TextButton(
                        onClick = {
                            showCreatePlaylistDialog = true
                            showPlaylistDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create New Playlist")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlaylistDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("New Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            onCreatePlaylist(newPlaylistName)
                            newPlaylistName = ""
                            showCreatePlaylistDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showTempoDialog) {
        AlertDialog(
            onDismissRequest = { showTempoDialog = false },
            title = { Text("Tempo & Metronome") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(text = "$bpm BPM", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = bpm.toFloat(),
                        onValueChange = { tempoViewModel.setBPM(it.toInt()) },
                        valueRange = 40f..240f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isMetronomePlaying = !isMetronomePlaying },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isMetronomePlaying) "Stop Metronome" else "Start Metronome")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTempoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (isMetronomePlaying) {
        MetronomeTick(bpm, audioHelper) { isMetronomePlaying = false }
    }
}
