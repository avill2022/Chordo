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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import avill.ladv.chordo.R
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
            text = stringResource(R.string.chord_finder),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = chordQuery,
            onValueChange = { chordQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.chord_placeholder)) },
            trailingIcon = {
                IconButton(onClick = {
                    if (chordQuery.isNotBlank()) {
                        searchedChord = chordQuery
                        variations = chordFinder.getMatrix(chordQuery)
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_content_description))
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (variations.isNotEmpty()) {
            Text(
                text = stringResource(R.string.chord_variations_title, searchedChord),
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
                text = stringResource(R.string.no_variations_found, searchedChord),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
