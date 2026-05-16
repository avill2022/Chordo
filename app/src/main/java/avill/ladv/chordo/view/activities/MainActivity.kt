package avill.ladv.chordo.view.activities

// AudioHelper.kt
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import avill.ladv.chordo.apps.app.ChordoApp
import avill.ladv.chordo.apps.app.ChordoViewModel
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.ui.theme.AppNameTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val chordoViewModel: ChordoViewModel by viewModels()
    val tempVIewMOdel:TempoViewModel by viewModels()
    lateinit var audioHelper: AudioHelper

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewModelConstructorInComposable")
    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        //splashScreen.setKeepOnScreenCondition{true}
        CoroutineScope(Dispatchers.Main).launch {
            //delay(20)
            //splashScreen.setKeepOnScreenCondition{false}
            audioHelper = AudioHelper(this@MainActivity)
        }
        setContent {
            AppNameTheme {
                TempoApp(tempVIewMOdel)
            //ChordoApp(chordoViewModel)
            }
        }
    }

    @Composable
    fun TempoApp(viewModel: TempoViewModel = viewModel()) {
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
            MetronomeTick(bpm) { isMetronomePlaying = false }
        }
    }

    @Composable
    fun MetronomeTick(bpm: Int, onStop: () -> Unit) {
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


    class AudioHelper(context: Context) {
        private val soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .build()

        private var clickSoundId = soundPool.load(context, avill.ladv.chordo.R.raw.click, 1)

        fun playClick() {
            soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
        }

        fun release() {
            soundPool.release()
        }
    }
}
