package avill.ladv.chordo.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.SoundPool
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern Audio Helper for playing UI sounds, streaming audio, and recording.
 */
@Singleton
class AudioHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "AudioHelper"
    
    // --- UI Sounds (SoundPool) ---
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()
    }

    /**
     * Plays a long audio file from raw resources (e.g. background music).
     */
    fun playRawResource(resId: Int, isLooping: Boolean = false) {
        stopPlayback()
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            this.isLooping = isLooping
            start()
        }
    }

    /**
     * Plays a short UI sound from raw resources.
     */
    fun playUiSound(resId: Int) {
        val soundId = soundMap[resId]
        if (soundId != null) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            soundPool?.load(context, resId, 1)?.let { id ->
                soundMap[resId] = id
                // SoundPool load is async, so we use a listener for the first time
                soundPool?.setOnLoadCompleteListener { pool, loadedId, status ->
                    if (status == 0 && loadedId == id) {
                        pool.play(loadedId, 1f, 1f, 1, 0, 1f)
                    }
                }
            }
        }
    }

    // --- Media Player (Streaming/Long Files) ---
    private var mediaPlayer: MediaPlayer? = null

    fun startStream(url: String, isLooping: Boolean = false) {
        stopPlayback()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
                this.isLooping = isLooping
            } catch (e: IOException) {
                Log.e(TAG, "Stream failed: ${e.message}")
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    // --- System Volume ---
    fun increaseVolume() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    // --- Recording (MediaRecorder) ---
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null

    fun startRecording(outputFileName: String = "recording_${System.currentTimeMillis()}.3gp") {
        val file = File(context.cacheDir, outputFileName)
        currentRecordingFile = file

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e(TAG, "Recording failed: ${e.message}")
            }
        }
    }

    fun stopRecording(): File? {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Stop recording failed: ${e.message}")
        }
        mediaRecorder = null
        return currentRecordingFile
    }

    fun release() {
        stopPlayback()
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
