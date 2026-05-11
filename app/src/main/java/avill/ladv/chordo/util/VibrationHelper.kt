package avill.ladv.chordo.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern Vibration Helper that handles different API levels and complex patterns.
 */
@Singleton
class VibrationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun hasVibrator(): Boolean = vibrator.hasVibrator()

    /**
     * Simple one-shot vibration.
     */
    fun vibrate(duration: Long = 50) {
        if (!hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    /**
     * A short "tick" vibration for haptic feedback.
     */
    fun hapticTick() {
        vibrate(20)
    }

    /**
     * A "shake" or error pattern.
     */
    fun vibrateError() {
        val pattern = longArrayOf(0, 100, 50, 100, 50, 100)
        vibratePattern(pattern)
    }

    /**
     * Starts a repeating vibration pattern (e.g., for an incoming call or alarm).
     */
    fun startIndefiniteVibration() {
        val pattern = longArrayOf(0, 500, 1000)
        vibratePattern(pattern, repeatIndex = 0)
    }

    fun stop() {
        vibrator.cancel()
    }

    private fun vibratePattern(pattern: LongArray, repeatIndex: Int = -1) {
        if (!hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeatIndex))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeatIndex)
        }
    }
}
