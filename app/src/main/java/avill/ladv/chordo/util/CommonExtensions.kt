package avill.ladv.chordo.util

import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.local.db.room.entities.Note
import kotlinx.coroutines.flow.Flow

/**
 * General purpose Kotlin extension functions.
 */

/**
 * Check if an object is null.
 */
fun Any?.isnull() = this == null

/**
 * Execute a block of code if the object is not null.
 */
inline fun <T : Any, R> T?.withNotNull(block: (T) -> R): R? {
    return this?.let(block)
}

/**
 * Returns the next element in a list after the [currentObject].
 * Loops back to the beginning if the current object is the last one.
 */
fun <T> List<T>.getNextElement(currentObject: T): T? {
    if (isEmpty()) return null
    val currentIndex = indexOf(currentObject)
    if (currentIndex == -1) return null
    val nextIndex = (currentIndex + 1) % size
    return get(nextIndex)
}

/**
 * Extension for Repository to handle Wish/Note operations.
 * Note: These are defined as local functions within the extension.
 */
fun Repository.wish() {
    suspend fun addAWish(wish: Note) {
        localDataSource.addNote(wish)
    }
    fun getWishes(): Flow<List<Note>> = localDataSource.getNotes()

    fun getAWishById(id: Long): Flow<Note> {
        return localDataSource.getNoteById(id)
    }
    suspend fun updateAWish(wish: Note) {
        localDataSource.updateNote(wish)
    }
    suspend fun deleteAWish(wish: Note) {
        localDataSource.deleteAWish(wish)
    }
}
