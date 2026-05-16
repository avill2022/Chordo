package avill.ladv.chordo.apps.app.helpers

// PitchDetector.kt
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

class PitchDetector {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    fun startDetection(): Flow<Double> = flow @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO) {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        val buffer = ShortArray(bufferSize)

        while (currentCoroutineContext().isActive && isRecording) {
            val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (readSize > 0) {
                val frequency = detectPitch(buffer, sampleRate)
                emit(frequency)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun detectPitch(buffer: ShortArray, sampleRate: Int): Double {
        // Autocorrelation method for pitch detection
        val correlations = mutableListOf<Double>()
        val minPeriod = sampleRate / 800 // 800Hz max
        val maxPeriod = sampleRate / 80   // 80Hz min

        for (lag in minPeriod..maxPeriod) {
            var correlation = 0.0
            for (i in 0 until buffer.size - lag) {
                correlation += buffer[i] * buffer[i + lag]
            }
            correlations.add(correlation)
        }

        if (correlations.isEmpty()) return 0.0

        var maxCorr = -1.0
        var maxLag = minPeriod
        for (i in correlations.indices) {
            if (correlations[i] > maxCorr) {
                maxCorr = correlations[i]
                maxLag = i + minPeriod
            }
        }

        return if (maxCorr > 0) {
            sampleRate.toDouble() / maxLag
        } else {
            0.0
        }
    }

    fun stopDetection() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}