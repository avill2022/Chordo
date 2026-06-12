package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import avill.ladv.chordo.R
import avill.ladv.chordo.apps.app.helpers.ChordTransposer
import avill.ladv.chordo.apps.app.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongEditScreen(
    song: Song?,
    onSave: (Song) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(song?.name ?: "") }
    var folder by remember { mutableStateOf(song?.folder ?: "") }
    var content by remember { mutableStateOf(song?.content ?: "") }
    var tone by remember { mutableStateOf(song?.tone ?: "") }
    var rhythm by remember { mutableStateOf(song?.rhythm ?: "") }
    var tempo by remember { mutableStateOf(song?.tempo ?: "") }
    var harmony by remember { mutableStateOf(song?.harmony ?: "") }
    var melody by remember { mutableStateOf(song?.melody ?: "") }
    var chords by remember { mutableStateOf(song?.chords ?: "") }
    var tab by remember { mutableStateOf(song?.tab ?: "") }
    var structure by remember { mutableStateOf(song?.structure ?: "") }
    var author by remember { mutableStateOf(song?.author ?: "") }
    var urlsong by remember { mutableStateOf(song?.urlsong ?: "") }
    var urltutorial by remember { mutableStateOf(song?.urltutorial ?: "") }
    var urlmidi by remember { mutableStateOf(song?.urlmidi ?: "") }
    var urlgpt by remember { mutableStateOf(song?.urlgpt ?: "") }
    var urlpartiture by remember { mutableStateOf(song?.urlpartiture ?: "") }

    var expandedAdvanced by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (song == null) stringResource(R.string.new_song) else stringResource(R.string.edit_song)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            onSave(Song(name, tone, chords, rhythm, tempo, content, tab, structure, harmony, melody, author, folder, urlsong, urltutorial, urlmidi, urlgpt, urlpartiture))
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.save))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Identity
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.basic_info), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.song_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = folder, onValueChange = { folder = it }, label = { Text(stringResource(R.string.artist_folder)) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text(stringResource(R.string.composer)) }, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Section 2: Musical Properties (Horizontal Row for efficiency)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = tone, onValueChange = { tone = it }, label = { Text(stringResource(R.string.tone)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = tempo, onValueChange = { tempo = it }, label = { Text(stringResource(R.string.bpm_tempo)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = rhythm, onValueChange = { rhythm = it }, label = { Text(stringResource(R.string.rhythm)) }, modifier = Modifier.weight(1f))
            }

            // Section 3: Content (Lyrics & Chords) - HIGH PRIORITY
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.lyrics_chords), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        val unique = ChordTransposer.getUniqueChords(content)
                        chords = unique.joinToString(" ")
                        if (tone.isBlank()) tone = unique.firstOrNull() ?: ""
                    }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.extract_chords))
                    }
                }

                OutlinedTextField(
                    value = chords,
                    onValueChange = { chords = it },
                    label = { Text(stringResource(R.string.chord_progression)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.chord_progression_placeholder)) }
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.lyrics_with_chords)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                    minLines = 8
                )
            }

            // Section 4: Tablature
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.tablature), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = tab,
                    onValueChange = { tab = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    placeholder = { Text(stringResource(R.string.tab_placeholder)) }
                )
            }

            // Section 5: Advanced (Collapsible)
            Surface(
                onClick = { expandedAdvanced = !expandedAdvanced },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.advanced_details_links), style = MaterialTheme.typography.titleSmall)
                        Text(stringResource(R.string.advanced_details_summary), style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(if (expandedAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                }
            }

            AnimatedVisibility(
                visible = expandedAdvanced,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.metadata), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(value = structure, onValueChange = { structure = it }, label = { Text(stringResource(R.string.structure_label)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = harmony, onValueChange = { harmony = it }, label = { Text(stringResource(R.string.harmony_details)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = melody, onValueChange = { melody = it }, label = { Text(stringResource(R.string.melody_notes)) }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.external_links), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(value = urlsong, onValueChange = { urlsong = it }, label = { Text(stringResource(R.string.song_stream_url)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = urltutorial, onValueChange = { urltutorial = it }, label = { Text(stringResource(R.string.tutorial_url)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = urlmidi, onValueChange = { urlmidi = it }, label = { Text(stringResource(R.string.midi_file_url)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = urlgpt, onValueChange = { urlgpt = it }, label = { Text(stringResource(R.string.ai_analysis_link)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = urlpartiture, onValueChange = { urlpartiture = it }, label = { Text(stringResource(R.string.sheet_music_url)) }, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongEditScreenPreview() {
    MaterialTheme {
        SongEditScreen(song = null, onSave = {}, onBack = {})
    }
}
