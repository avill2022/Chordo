package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ChordDiagram(
    matrixRow: List<Int>,
    modifier: Modifier = Modifier
) {
    val capo = matrixRow[0]
    val stringsData = matrixRow.drop(1) // [S6, S5, S4, S3, S2, S1]
    
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Top) {
                if (capo > 0) {
                    Text(
                        text = "${capo}fr",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 10.dp, end = 8.dp)
                    )
                }
                Canvas(modifier = Modifier.size(100.dp, 140.dp)) {
                    val numStrings = 6
                    val numFrets = 4
                    val stringSpacing = size.width / (numStrings - 1)
                    val fretSpacing = size.height / numFrets
                    
                    // Draw Frets
                    for (i in 0..numFrets) {
                        val y = i * fretSpacing
                        drawLine(
                            color = Color.DarkGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = if (i == 0 && capo == 0) 8f else 2f
                        )
                    }
                    
                    // Draw Strings
                    for (i in 0 until numStrings) {
                        val x = i * stringSpacing
                        drawLine(
                            color = Color.DarkGray,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 2f
                        )
                    }
                    
                    // Draw Markers
                    stringsData.forEachIndexed { index, fret ->
                        val x = index * stringSpacing
                        when {
                            fret == -1 -> {
                                val s = 8f
                                drawLine(Color.Red, Offset(x - s, -15f), Offset(x + s, -5f), 3f)
                                drawLine(Color.Red, Offset(x + s, -15f), Offset(x - s, -5f), 3f)
                            }
                            fret == 0 -> {
                                drawCircle(Color.Gray, radius = 6f, center = Offset(x, -10f), style = Stroke(3f))
                            }
                            fret > 0 -> {
                                val y = (fret - 0.5f) * fretSpacing
                                drawCircle(Color.Yellow, radius = 10f, center = Offset(x, y))
                            }
                        }
                    }
                }
                if (capo == 0) {
                    Spacer(modifier = Modifier.width(30.dp)) // padding balance
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.width(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("E", "A", "D", "G", "B", "e").forEach {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ChordVersionDialog(
    chordName: String,
    matrix: List<List<Int>>,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { matrix.size })
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = chordName,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Text(
                    text = "Variation ${pagerState.currentPage + 1} of ${matrix.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    ChordDiagram(
                        matrixRow = matrix[page],
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Indicators
                Row(
                    Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(matrix.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outlineVariant
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
