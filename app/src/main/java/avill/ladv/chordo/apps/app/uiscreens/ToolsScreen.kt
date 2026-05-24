package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import avill.ladv.chordo.apps.app.helpers.ChordFinder

@Composable
fun ToolsScreen(
    chordFinder: ChordFinder = remember { ChordFinder() }
) {
    var chordQuery by remember { mutableStateOf("") }
    var variations by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var searchedChord by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chord Finder",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = chordQuery,
            onValueChange = { chordQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter chord (e.g., C, Am, G7)") },
            trailingIcon = {
                IconButton(onClick = {
                    if (chordQuery.isNotBlank()) {
                        searchedChord = chordQuery
                        variations = chordFinder.getMatrix(chordQuery)
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (variations.isNotEmpty()) {
            Text(
                text = "Variations for $searchedChord",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(variations) { matrixRow ->
                    ChordDiagram(
                        matrixRow = matrixRow,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else if (searchedChord.isNotBlank()) {
            Text(
                text = "No variations found for '$searchedChord'",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
