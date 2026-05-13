package avill.ladv.chordo.data.local.db.room

import androidx.room.*
import avill.ladv.chordo.data.local.db.room.entities.FavoriteSong
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSongDao {
    @Query("SELECT * FROM favorite_songs")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    @Query("SELECT * FROM favorite_songs WHERE name = :name AND folder = :folder LIMIT 1")
    suspend fun getFavoriteByNameAndFolder(name: String, folder: String): FavoriteSong?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteSong)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteSong)

    @Query("DELETE FROM favorite_songs WHERE name = :name AND folder = :folder")
    suspend fun deleteFavoriteByNameAndFolder(name: String, folder: String)
}
