package avill.ladv.chordo.service

import android.R
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

import androidx.media3.session.CommandButton
import androidx.media3.session.MediaController
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Inject
import javax.inject.Singleton

class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player)
            .setId("StaticFMSession")
            .build()
        setMediaNotificationProvider(CustomNotificationProvider(applicationContext))
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession = mediaSession

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }
}
@Singleton
class MediaControllerManager
@Inject constructor(val context: Context) {

    private val sessionToken = SessionToken(
        context,
        ComponentName(context, PlaybackService::class.java)
    )

    val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(context, sessionToken).buildAsync()
}
@UnstableApi
class CustomNotificationProvider (
    private val context: Context
): MediaNotification.Provider {
    override fun createNotification(
        mediaSession: MediaSession,
        mediaButtonPreferences: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val notificationId = 1

        val notification = NotificationCompat.Builder(
            context,
            "media_playback_channel"
        )
            .setContentTitle("Static FM")
            .setContentText("Reproduciendo radio local")
            .setSmallIcon(R.drawable.btn_plus)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

        return MediaNotification(notificationId, notification)
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean {
        return true
    }

}
