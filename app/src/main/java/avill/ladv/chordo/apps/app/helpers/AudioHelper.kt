package avill.ladv.chordo.apps.app.helpers

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

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