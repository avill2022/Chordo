package avill.ladv.chordo.apps.app

// MainActivity.kt
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import avill.ladv.chordo.apps.app.helpers.PitchDetector
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            MaterialTheme {
                TunerApp()
            }
        }
    }
}

@Composable
fun TunerApp(viewModel: TunerViewModel = viewModel()) {
    val tunerState by viewModel.tunerState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val detector = remember { PitchDetector() }
    var isListening by remember { mutableStateOf(false) }

    LaunchedEffect(isListening) {
        if (isListening) {
            detector.startDetection().collect { frequency ->
                viewModel.processPitch(frequency)
            }
        } else {
            detector.stopDetection()
            viewModel.resetTuner()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Title
        Text(
            text = "Guitar Tuner",
            style = MaterialTheme.typography.headlineMedium
        )

        // Main Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Detected Note
                Text(
                    text = if (tunerState.pitchDetected) tunerState.detectedNote else "--",
                    fontSize = 64.sp,
                    color = if (tunerState.isInTune) Color.Green else Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Target String
                tunerState.closestString?.let { string ->
                    Text(
                        text = "${string.name}${string.stringNumber} String",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "${string.frequency} Hz",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Frequency Display
                Text(
                    text = if (tunerState.detectedFrequency > 0)
                        "${String.format("%.1f", tunerState.detectedFrequency)} Hz"
                    else "Waiting for input...",
                    fontSize = 18.sp,
                    color = Color.LightGray
                )
            }
        }

        // Needle Meter (Tuning indicator)
        TunerMeter(centsOffset = tunerState.centsOffset)

        // Guitar Strings Grid
        Text(
            text = "Tune each string",
            style = MaterialTheme.typography.titleMedium
        )

        StringGrid(
            currentString = tunerState.closestString,
            onStringSelected = { string ->
                // Manually set target frequency if needed
            }
        )

        // Start/Stop Button
        Button(
            onClick = { isListening = !isListening },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                if (isListening) "Stop Listening" else "Start Tuner",
                fontSize = 18.sp
            )
        }

        // Status Text
        Text(
            text = when {
                !isListening -> "Tap Start to begin tuning"
                !tunerState.pitchDetected -> "Playing a string..."
                tunerState.isInTune -> "✓ In tune!"
                tunerState.centsOffset < 0 -> "♭ Flat - Tune up"
                else -> "♯ Sharp - Tune down"
            },
            color = when {
                tunerState.isInTune -> Color.Green
                else -> Color.White
            }
        )
    }
}

@Composable
fun TunerMeter(centsOffset: Int) {
    val animatedOffset by animateIntAsState(
        targetValue = centsOffset,
        animationSpec = tween(durationMillis = 50)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tuning Meter",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2
                val centerX = size.width / 2

                // Draw background lines
                for (i in -5..5) {
                    val x = centerX + (i * size.width / 12)
                    val lineColor = when (abs(i)) {
                        0 -> Color.Green
                        1 -> Color.Yellow
                        else -> Color.Gray
                    }
                    drawLine(
                        color = lineColor,
                        start = Offset(x, centerY - 20),
                        end = Offset(x, centerY + 20),
                        strokeWidth = if (i == 0) 3f else 1f
                    )
                }

                // Draw needle
                val needleX = centerX + (animatedOffset * size.width / 1200)
                rotate(degrees = 0f, pivot = Offset(needleX, centerY)) {
                    drawLine(
                        color = Color.Red,
                        start = Offset(needleX, centerY - 40),
                        end = Offset(needleX, centerY + 10),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                // Draw center circle
                drawCircle(
                    color = Color.Red,
                    radius = 8f,
                    center = Offset(centerX, centerY)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("-50", fontSize = 12.sp, color = Color.Gray)
            Text("-25", fontSize = 12.sp, color = Color.Gray)
            Text("0", fontSize = 14.sp, color = Color.Green)
            Text("+25", fontSize = 12.sp, color = Color.Gray)
            Text("+50", fontSize = 12.sp, color = Color.Gray)
        }

        Text(
            text = "${centsOffset} cents ${if (centsOffset < 0) "♭" else if (centsOffset > 0) "♯" else ""}",
            fontSize = 16.sp,
            color = if (abs(centsOffset) <= 5) Color.Green else Color.Yellow
        )
    }
}

@Composable
fun StringGrid(
    currentString: GuitarString?,
    onStringSelected: (GuitarString) -> Unit
) {
    val strings = listOf("E", "A", "D", "G", "B", "E")
    val stringNumbers = listOf(6, 5, 4, 3, 2, 1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        strings.forEachIndexed { index, note ->
            val stringNumber = stringNumbers[index]
            val isActive = currentString?.stringNumber == stringNumber

            Button(
                onClick = {
                    // Could implement manual string selection
                },
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) Color.Green else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("$note$stringNumber", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun needPermissionScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Microphone Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please grant microphone permission to use the tuner",
            color = Color.Gray
        )
    }
}