package avill.ladv.chordo.apps.app

// TunerViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

data class GuitarString(
    val name: String,
    val note: String,
    val frequency: Double,
    val stringNumber: Int
)

data class TunerState(
    val detectedFrequency: Double = 0.0,
    val detectedNote: String = "",
    val centsOffset: Int = 0,
    val isInTune: Boolean = false,
    val closestString: GuitarString? = null,
    val pitchDetected: Boolean = false
)

class TunerViewModel : ViewModel() {
    private val _tunerState = MutableStateFlow(TunerState())
    val tunerState = _tunerState.asStateFlow()

    // Standard guitar tuning E2-A2-D3-G3-B3-E4
    private val guitarStrings = listOf(
        GuitarString("E", "E2", 82.41, 6),
        GuitarString("A", "A2", 110.00, 5),
        GuitarString("D", "D3", 146.83, 4),
        GuitarString("G", "G3", 196.00, 3),
        GuitarString("B", "B3", 246.94, 2),
        GuitarString("E", "E4", 329.63, 1)
    )

    fun processPitch(frequency: Double) {
        if (frequency <= 0 || frequency > 1000) {
            _tunerState.value = _tunerState.value.copy(
                pitchDetected = false,
                detectedFrequency = frequency
            )
            return
        }

        val closest = findClosestString(frequency)
        val cents = calculateCentsOffset(frequency, closest.frequency)
        val isInTune = abs(cents) <= 5
        val noteName = getNoteName(frequency)

        _tunerState.value = TunerState(
            detectedFrequency = frequency,
            detectedNote = noteName,
            centsOffset = cents,
            isInTune = isInTune,
            closestString = closest,
            pitchDetected = true
        )
    }

    private fun findClosestString(frequency: Double): GuitarString {
        return guitarStrings.minByOrNull { string ->
            abs(frequency - string.frequency)
        } ?: guitarStrings[0]
    }

    private fun calculateCentsOffset(detectedFreq: Double, targetFreq: Double): Int {
        if (detectedFreq <= 0 || targetFreq <= 0) return 0
        val cents = 1200 * log10(detectedFreq / targetFreq) / log10(2.0)
        return cents.roundToInt().coerceIn(-50, 50)
    }

    private fun getNoteName(frequency: Double): String {
        // A4 = 440Hz
        val semitone = 12 * log10(frequency / 440.0) / log10(2.0)
        val noteIndex = (semitone.roundToInt() + 9) % 12

        val notes = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#","A","A#", "B",)
        return notes[if (noteIndex < 0) noteIndex + 12 else noteIndex]
    }

    fun resetTuner() {
        _tunerState.value = TunerState()
    }
}