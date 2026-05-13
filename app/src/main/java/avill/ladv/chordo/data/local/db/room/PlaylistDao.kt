package avill.ladv.chordo.data.local.db.room

import androidx.room.*
import avill.ladv.chordo.data.local.db.room.entities.Playlist
import avill.ladv.chordo.data.local.db.room.entities.PlaylistSong
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<PlaylistSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongInPlaylist(song: PlaylistSong)

    @Delete
    suspend fun deleteSongFromPlaylist(song: PlaylistSong)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND name = :name AND folder = :folder")
    suspend fun deleteSongFromPlaylist(playlistId: Long, name: String, folder: String)
}
