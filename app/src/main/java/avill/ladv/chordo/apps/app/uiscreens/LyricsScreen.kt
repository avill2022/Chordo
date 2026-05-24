package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import avill.ladv.chordo.apps.app.MetronomeTick
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.apps.app.helpers.ChordFinder
import avill.ladv.chordo.apps.app.helpers.ChordTransposer
import avill.ladv.chordo.apps.app.helpers.convertAmericanToLatinAdvanced
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
    onTempoChange: (Int) -> Unit,
    onRestore: () -> Unit,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    tempoViewModel: TempoViewModel,
    audioHelper: AudioHelper,
    chordFinder: ChordFinder = ChordFinder()
) {
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showTempoDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    var selectedChordName by remember { mutableStateOf<String?>(null) }
    var selectedChordMatrix by remember { mutableStateOf<List<List<Int>>?>(null) }

    var isChordsRemoved by remember { mutableStateOf(false) }
    var isAmericanLatinNotation by remember { mutableStateOf(false) }
    var isMetronomePlaying by remember { mutableStateOf(false) }

    val bpm by tempoViewModel.bpm.collectAsState()

    val scrollState = rememberScrollState()
    var isAutoScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableIntStateOf(5) }

    val sheetState = rememberModalBottomSheetState()

    // Sync tempo
    LaunchedEffect(song.name, song.folder) {
        song.tempo.toIntOrNull()?.let { tempoViewModel.setBPM(it) }
    }

    // Auto-scroll logic
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
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(song.name, style = MaterialTheme.typography.titleMedium)
                        Text(song.folder, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAutoScrolling = !isAutoScrolling },
                containerColor = if (isAutoScrolling) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (isAutoScrolling) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = "Auto Scroll"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Quick Access Control Bar
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Transpose group
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tone", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
                            FilledTonalIconButton(onClick = { onTranspose(-1) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Down", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                song.tone.ifEmpty { "C" },
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            FilledTonalIconButton(onClick = { onTranspose(1) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Rounded.Add, contentDescription = "Up", modifier = Modifier.size(18.dp))
                            }
                        }

                        VerticalDivider(modifier = Modifier.height(24.dp))

                        // Metronome Quick Toggle
                        TextButton(onClick = { showTempoDialog = true }) {
                            Icon(Icons.Rounded.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("$bpm BPM")
                        }
                    }
                }
                
                // Chords Horizontal Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    song.chords.split(" ")
                        .filter { it.isNotBlank() }
                        .distinct()
                        .forEach { chord ->
                            val displayedChord = if (isAmericanLatinNotation) {
                                convertAmericanToLatinAdvanced(chord)
                            } else {
                                chord
                            }
                            SuggestionChip(
                                onClick = {
                                    selectedChordName = displayedChord
                                    selectedChordMatrix = chordFinder.getMatrix(chord)
                                },
                                label = {
                                    Text(
                                        text = displayedChord,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            )
                        }
                }

                // Lyrics Content
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    val chordColor = MaterialTheme.colorScheme.primary
                    val annotatedContent = remember(song.content, isChordsRemoved, isAmericanLatinNotation, chordColor) {
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
                                var lastIndex = 0
                                activeRegex.findAll(textToProcess).forEach { match ->
                                    append(textToProcess.substring(lastIndex, match.range.first))
                                    withStyle(style = SpanStyle(
                                        color = chordColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        background = chordColor.copy(alpha = 0.08f)
                                    )) {
                                        append(match.value)
                                    }
                                    lastIndex = match.range.last + 1
                                }
                                append(textToProcess.substring(lastIndex))
                            }
                        }
                    }

                    Text(
                        text = annotatedContent,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 32.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Visual Metronome Pulse
                    if (isMetronomePlaying) {
                        MetronomePulse(bpm)
                    }
                }

                // Tablature Section (if exists)
                if (song.tab.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(160.dp).padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("TABLATURE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Box(modifier = Modifier.horizontalScroll(rememberScrollState()).verticalScroll(rememberScrollState())) {
                                Text(
                                    text = song.tab,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, lineHeight = 16.sp),
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState
        ) {
            SettingsContent(
                isChordsRemoved = isChordsRemoved,
                onChordsToggled = { isChordsRemoved = it },
                isLatin = isAmericanLatinNotation,
                onNotationToggled = { isAmericanLatinNotation = it },
                scrollSpeed = scrollSpeed,
                onSpeedChanged = { scrollSpeed = it },
                onEditClick = {
                    showSettingsSheet = false
                    onEditClick()
                },
                onAddToPlaylistClick = {
                    showSettingsSheet = false
                    showPlaylistDialog = true
                }
            )
        }
    }

    // Dialogs
    if (showTempoDialog) {
        TempoDialog(
            bpm = bpm,
            isPlaying = isMetronomePlaying,
            onBpmChange = { 
                tempoViewModel.setBPM(it)
                song.tempo = it.toString()
                onTempoChange(it)
            },
            onTogglePlay = { isMetronomePlaying = !isMetronomePlaying },
            onDismiss = { showTempoDialog = false }
        )
    }

    if (isMetronomePlaying) {
        MetronomeTick(bpm, audioHelper) { isMetronomePlaying = false }
    }

    // Playlist Dialogs
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
        var newPlaylistName by remember { mutableStateOf("") }
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

    // Chord Variation Dialog
    selectedChordMatrix?.let { matrix ->
        ChordVersionDialog(
            chordName = selectedChordName ?: "",
            matrix = matrix,
            onDismiss = { selectedChordMatrix = null }
        )
    }
}

@Composable
fun MetronomePulse(bpm: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (60000 / bpm), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(12.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}

@Composable
fun SettingsContent(
    isChordsRemoved: Boolean,
    onChordsToggled: (Boolean) -> Unit,
    isLatin: Boolean,
    onNotationToggled: (Boolean) -> Unit,
    scrollSpeed: Int,
    onSpeedChanged: (Int) -> Unit,
    onEditClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 48.dp)) {
        Text("Display Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        ListItem(
            headlineContent = { Text("Show Chords") },
            trailingContent = { Switch(checked = !isChordsRemoved, onCheckedChange = { onChordsToggled(!it) }) }
        )
        ListItem(
            headlineContent = { Text("Latin Notation (DO, RE, MI)") },
            trailingContent = { Switch(checked = isLatin, onCheckedChange = onNotationToggled) }
        )

        Spacer(Modifier.height(8.dp))
        Text("Auto-scroll Speed", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Slider(
            value = scrollSpeed.toFloat(),
            onValueChange = { onSpeedChanged(it.toInt()) },
            valueRange = 1f..10f,
            steps = 8
        )

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onEditClick, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Edit Song")
            }
            OutlinedButton(onClick = onAddToPlaylistClick, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.PlaylistAdd, null)
                Spacer(Modifier.width(8.dp))
                Text("Playlist")
            }
        }
    }
}

@Composable
fun TempoDialog(
    bpm: Int,
    isPlaying: Boolean,
    onBpmChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } },
        title = { Text("Metronome") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$bpm", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                Text("BPM", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = bpm.toFloat(),
                    onValueChange = { onBpmChange(it.toInt()) },
                    valueRange = 40f..240f
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onTogglePlay() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isPlaying) "Stop" else "Start")
                }
            }
        }
    )
}
