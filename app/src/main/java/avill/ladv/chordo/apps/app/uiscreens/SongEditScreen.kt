package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (song == null) "Create Song" else "Edit Song") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onSave(
                            Song(
                                name = name,
                                folder = folder,
                                content = content,
                                tone = tone,
                                rhythm = rhythm,
                                tempo = tempo,
                                harmony = harmony,
                                melody = melody,
                                chords = chords,
                                tab = tab,
                                structure = structure,
                                author = author,
                                urlsong = urlsong,
                                urltutorial = urltutorial,
                                urlmidi = urlmidi,
                                urlgpt = urlgpt,
                                urlpartiture = urlpartiture
                            )
                        )
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tone, onValueChange = { tone = it }, label = { Text("Tone") }, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chords,
                    onValueChange = { chords = it },
                    label = { Text("Chords") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val uniqueChords = ChordTransposer.getUniqueChords(content)
                        chords = uniqueChords.joinToString(" ")
                        if (tone.isBlank()) {
                            tone = uniqueChords.firstOrNull() ?: ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Extract Chords from lyrics"
                    )
                }
            }
            OutlinedTextField(value = rhythm, onValueChange = { rhythm = it }, label = { Text("Rhythm") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tempo, onValueChange = { tempo = it }, label = { Text("Tempo") }, modifier = Modifier.fillMaxWidth())


            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content/Lyrics") }, modifier = Modifier.fillMaxWidth(), minLines = 5)
            OutlinedTextField(value = tab, onValueChange = { tab = it }, label = { Text("Tab") }, modifier = Modifier.fillMaxWidth())
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Details", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(value = harmony, onValueChange = { harmony = it }, label = { Text("Harmony") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = melody, onValueChange = { melody = it }, label = { Text("Melody") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = structure, onValueChange = { structure = it }, label = { Text("Structure") }, modifier = Modifier.fillMaxWidth())
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("URLs", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(value = folder, onValueChange = { folder = it }, label = { Text("Folder/Artist") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth())


            OutlinedTextField(value = urlsong, onValueChange = { urlsong = it }, label = { Text("Song URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = urltutorial, onValueChange = { urltutorial = it }, label = { Text("Tutorial URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = urlmidi, onValueChange = { urlmidi = it }, label = { Text("MIDI URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = urlgpt, onValueChange = { urlgpt = it }, label = { Text("GPT URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = urlpartiture, onValueChange = { urlpartiture = it }, label = { Text("Partiture URL") }, modifier = Modifier.fillMaxWidth())
        }
    }
}
