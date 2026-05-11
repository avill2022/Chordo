package avill.ladv.chordo.data.local.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import avill.ladv.chordo.data.local.db.room.entities.ModelEntity
import avill.ladv.chordo.data.local.db.room.entities.Note

@Database(entities = [ModelEntity::class,Note::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nameDao(): NameDao
    abstract fun noteDao(): NoteDao
}
