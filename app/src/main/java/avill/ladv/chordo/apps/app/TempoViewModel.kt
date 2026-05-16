package avill.ladv.chordo.apps.app

// TempoViewModel.kt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TempoViewModel : ViewModel() {
    private val _bpm = MutableStateFlow(120)
    val bpm = _bpm.asStateFlow()

    private val _tapTimes = mutableListOf<Long>()
    private var lastTapTime = 0L

    fun addTap() {
        val currentTime = System.currentTimeMillis()

        // Reset if tap is too old (> 2 seconds)
        if (currentTime - lastTapTime > 2000) {
            _tapTimes.clear()
        }

        _tapTimes.add(currentTime)

        // Keep only last 8 taps for calculation
        if (_tapTimes.size > 8) {
            _tapTimes.removeAt(0)
        }

        // Calculate BPM if we have at least 2 taps
        if (_tapTimes.size >= 2) {
            calculateBPM()
        }

        lastTapTime = currentTime
    }

    private fun calculateBPM() {
        val differences = mutableListOf<Long>()
        for (i in 1 until _tapTimes.size) {
            differences.add(_tapTimes[i] - _tapTimes[i - 1])
        }

        val averageInterval = differences.average()
        val bpmValue = (60000.0 / averageInterval).roundToInt()

        // Clamp reasonable BPM range
        _bpm.value = bpmValue.coerceIn(40, 240)
    }

    fun setBPM(newBpm: Int) {
        _bpm.value = newBpm.coerceIn(40, 240)
        // Clear tap history when manually setting BPM
        _tapTimes.clear()
        lastTapTime = 0
    }

    fun incrementBPM() {
        _bpm.value = (_bpm.value + 1).coerceIn(40, 240)
    }

    fun decrementBPM() {
        _bpm.value = (_bpm.value - 1).coerceIn(40, 240)
    }

    fun resetToDefault() {
        _bpm.value = 120
        _tapTimes.clear()
        lastTapTime = 0
    }
}