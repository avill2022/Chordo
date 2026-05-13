package avill.ladv.chordo.data

import android.content.Context
import avill.ladv.chordo.data.local.LocalDataSource
import avill.ladv.chordo.data.local.db.room.entities.*
import avill.ladv.chordo.data.local.files.MyFilesManager
import avill.ladv.chordo.data.local.shared.MySharedPreferences
import avill.ladv.chordo.data.network.RemoteDataSource
import avill.ladv.chordo.data.network.okhttpclient.MyOkHttpInterface
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(context: Context) {
    var localDataSource: LocalDataSource = LocalDataSource(context)
    var remoteDataSource: RemoteDataSource = RemoteDataSource()


    //----------------------------------------------------------------------------------------------
    fun getMyFilesManager(): MyFilesManager{
        return  localDataSource.myFilesManager
    }
    fun getMySharedPreferences(): MySharedPreferences{
        return localDataSource.mySharedPreferences
    }
    //----------------------------------------------------------------------------------------------
    fun getMyOkHttpInterface(): MyOkHttpInterface {
        return remoteDataSource.myOkHttpInterface
    }

    //----------------------------------------------------------------------------------------------
    suspend fun addFavorite(favorite: FavoriteSong) = localDataSource.addFavorite(favorite)
    suspend fun removeFavorite(name: String, folder: String) = localDataSource.removeFavorite(name, folder)
    fun getAllFavorites(): Flow<List<FavoriteSong>> = localDataSource.getAllFavorites()
    suspend fun isFavorite(name: String, folder: String): Boolean = localDataSource.isFavorite(name, folder)

    // Playlists
    suspend fun createPlaylist(name: String) = localDataSource.createPlaylist(name)
    suspend fun deletePlaylist(playlist: Playlist) = localDataSource.deletePlaylist(playlist)
    fun getAllPlaylists(): Flow<List<Playlist>> = localDataSource.getAllPlaylists()
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong) = localDataSource.addSongToPlaylist(playlistSong)
    suspend fun removeSongFromPlaylist(playlistId: Long, name: String, folder: String) = 
        localDataSource.removeSongFromPlaylist(playlistId, name, folder)
    fun getSongsInPlaylist(playlistId: Long): Flow<List<PlaylistSong>> = localDataSource.getSongsInPlaylist(playlistId)

    //----------------------------------------------------------------------------------------------
    suspend fun addAWish(wish:Note){
        localDataSource.addNote(wish)
    }

    fun getWishes(): Flow<List<Note>> = localDataSource.getNotes()

    fun getAWishById(id:Long) :Flow<Note> {
        return localDataSource.getNoteById(id)
    }

    suspend fun updateAWish(wish:Note){
        localDataSource.updateNote(wish)
    }

    suspend fun deleteAWish(wish: Note){
        localDataSource.deleteAWish(wish)
    }
    //-----------------------------------------------------------
}