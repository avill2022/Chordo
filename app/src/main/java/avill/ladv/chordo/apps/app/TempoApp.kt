package avill.ladv.chordo.apps.app

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import kotlinx.coroutines.delay

@Composable
fun TempoApp(viewModel: TempoViewModel = viewModel(), audioHelper: AudioHelper) {
    val bpm by viewModel.bpm.collectAsState()
    var isMetronomePlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // BPM Display
        Text(
            text = "$bpm BPM",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tap Tempo Button
        Button(
            onClick = { viewModel.addTap() },
            modifier = Modifier.size(120.dp)
        ) {
            Text("TAP", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Manual BPM Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { viewModel.decrementBPM() }) {
                Text("-")
            }

            Button(onClick = { viewModel.resetToDefault() }) {
                Text("Reset")
            }

            Button(onClick = { viewModel.incrementBPM() }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BPM Slider
        Slider(
            value = bpm.toFloat(),
            onValueChange = { viewModel.setBPM(it.toInt()) },
            valueRange = 40f..240f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Metronome Toggle (Optional)
        Button(
            onClick = { isMetronomePlaying = !isMetronomePlaying }
        ) {
            Text(if (isMetronomePlaying) "Stop Metronome" else "Start Metronome")
        }
    }

    // Metronome logic (optional but useful)
    if (isMetronomePlaying) {
        MetronomeTick(bpm,audioHelper) { isMetronomePlaying = false }
    }
}

@Composable
fun MetronomeTick(bpm: Int, audioHelper: AudioHelper, onStop: () -> Unit) {
    val intervalMs = (60000 / bpm).toLong()

    LaunchedEffect(bpm) {
        while (true) {
            delay(intervalMs)
            // Play tick sound (you need to add audio implementation)
            // Example:
            Log.v("Tick","--")
            audioHelper.playClick()
        }
    }

    DisposableEffect(Unit) {
        onDispose { onStop() }
    }
}