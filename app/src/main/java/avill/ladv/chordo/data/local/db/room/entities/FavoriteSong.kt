package avill.ladv.chordo.data.local.db.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
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
