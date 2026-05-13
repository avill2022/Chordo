package avill.ladv.chordo.data.local.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import avill.ladv.chordo.data.local.db.room.entities.*

@Database(entities = [ModelEntity::class, Note::class, FavoriteSong::class, Playlist::class, PlaylistSong::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nameDao(): NameDao
    abstract fun noteDao(): NoteDao
    abstract fun favoriteSongDao(): FavoriteSongDao
    abstract fun playlistDao(): PlaylistDao
}
