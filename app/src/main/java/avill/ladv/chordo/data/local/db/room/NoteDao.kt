package avill.ladv.chordo.data.local.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import avill.ladv.chordo.data.local.db.room.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(wishEntity: Note)
    @Query("Select * from `note_table` where id=:id")
    fun getNoteById(id:Long): Flow<Note>
    @Query("delete from `note_table` where id = :id")
    fun deleteNoteById(id: Int)

    @Delete
    suspend fun deleteNote(wishEntity: Note)
    @Update
    suspend fun updateNote(wishEntity: Note)

    // Loads all wishes from the wish table
    @Query("Select * from `note_table`")
    fun getAllNotes(): Flow<List<Note>>
    @Query("delete from `note_table`")
    fun deleteAllNotes()
}