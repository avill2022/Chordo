package avill.ladv.chordo.data

import android.content.Context
import avill.ladv.chordo.data.local.LocalDataSource
import avill.ladv.chordo.data.local.db.room.entities.Note
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