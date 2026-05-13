package avill.ladv.chordo.data.local.db.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_songs",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlistId"])]
)
data class PlaylistSong(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val playlistId: Long,
    val name: String,
    val folder: String,
    val content: String,
    val tone: String,
    val rhythm: String,
    val tempo: String,
    val harmony: String,
    val melody: String,
    val chords: String,
    val tab: String,
    val structure: String,
    val author: String,
    val urlsong: String,
    val urltutorial: String,
    val urlmidi: String,
    val urlgpt: String,
    val urlpartiture: String
)
