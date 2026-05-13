package avill.ladv.chordo.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.room.Room
import avill.ladv.chordo.data.local.db.room.AppDatabase
import avill.ladv.chordo.data.local.db.room.FavoriteSongDao
import avill.ladv.chordo.data.local.db.room.NoteDao
import avill.ladv.chordo.data.local.db.room.PlaylistDao
import avill.ladv.chordo.data.local.db.room.entities.*
import avill.ladv.chordo.data.local.files.MyFilesManager
import avill.ladv.chordo.data.local.shared.MySharedPreferences
import avill.ladv.chordo.util.dataStore
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


@ViewModelScoped
class LocalDataSource @Inject constructor(val context: Context) {
    companion object{
        const val DATABASE_TABLE_NAME = "model_table"
        const val DATABASE_NAME = "geofence_db"
        const val PREFERENCE_NAME = "geofence_preference"
        const val PREFERENCE_FIRST_LAUNCH = "firstLaunch"
    }
    //local resources------------------------------------------------------------------------------
    private val db = Room.databaseBuilder(context, AppDatabase::class.java,"note_db").fallbackToDestructiveMigration().build()
    val noteDao: NoteDao = db.noteDao()
    val favoriteSongDao: FavoriteSongDao = db.favoriteSongDao()
    val playlistDao: PlaylistDao = db.playlistDao()

    suspend fun createPlaylist(name: String) = playlistDao.insertPlaylist(Playlist(name = name))
    suspend fun deletePlaylist(playlist: Playlist) = playlistDao.deletePlaylist(playlist)
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong) = playlistDao.insertSongInPlaylist(playlistSong)
    suspend fun removeSongFromPlaylist(playlistId: Long, name: String, folder: String) = 
        playlistDao.deleteSongFromPlaylist(playlistId, name, folder)
    fun getSongsInPlaylist(playlistId: Long): Flow<List<PlaylistSong>> = playlistDao.getSongsInPlaylist(playlistId)

    suspend fun addFavorite(favorite: FavoriteSong) {
        favoriteSongDao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(name: String, folder: String) {
        favoriteSongDao.deleteFavoriteByNameAndFolder(name, folder)
    }

    fun getAllFavorites(): Flow<List<FavoriteSong>> = favoriteSongDao.getAllFavorites()

    suspend fun isFavorite(name: String, folder: String): Boolean {
        return favoriteSongDao.getFavoriteByNameAndFolder(name, folder) != null
    }

    suspend fun addNote(wish:Note){
        noteDao.insertNote(wish)
    }

    fun getNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getNoteById(id:Long) :Flow<Note> {
        return noteDao.getNoteById(id)
    }

    suspend fun updateNote(wish:Note){
        noteDao.updateNote(wish)
    }

    suspend fun deleteAWish(wish: Note){
        noteDao.deleteNote(wish)
    }


    //bd
    var entityDao = Room.databaseBuilder(context, AppDatabase::class.java,DATABASE_NAME).fallbackToDestructiveMigration().build().nameDao()
    suspend fun dbTest() {
        //insert
        entityDao.insertModel(ModelEntity(
            name = "Test Model",
            data = "{\"key\":\"value\"}"
        ))
        //get
        val modelEntity: ModelEntity = entityDao.getModelBy(0).first()
        //
        val readModelEntities: MutableList<ModelEntity> = entityDao.getAllModels().first()
        /*lifecycleScope.launch {
    localDataSource.dbTest()
}*/
    }
    //files
    var myFilesManager: MyFilesManager = MyFilesManager(context)
    //data-local-files------------------------------------------------------------------------------
    fun addInformationFile() {
        myFilesManager.addInformationToTheFile(
    "",
            "{\"MONTO_TRANSACCION\":100,\"NUMERO_SERIE_HEX\":\"00000000C3AFA6A7\",\"CONTRACT\":\"01011EC43F0132818000000000000000213BAE10D17F0042EBC4C8B761\",\"AUTOBUS\":\"CC\",\"TIPO_TARJETA\":1,\"CONTRACT_RESTRICT_TIME\":\"00\",\"SAM_COUNTER\":\"0042EB\",\"CONTRACT_SALE_SAM\":\"AE10D17F\",\"CONTRACT_TARIFF\":1,\"CONTRACT_VALIDITY_DURATION\":63,\"CONTRACT_VALIDITY_START_DATE\":\"2020-04-17 00:00:00\",\"COUNTER_AMOUNT\":\"\",\"COUNTER_VALUE\":\"\",\"CONTADOR_VALIDACIONES\":51,\"ENVIRONMENT\":\"1484013200000001213B2F7F0000010100000000000000000000000000\",\"ENVIRONMENT_ISSUER_ID\":\"32\",\"EQUIPO\":\"\",\"TIPO_EQUIPO\":\"B\",\"LINEA\":\"AA\",\"CONTADOR_RECARGAS\":0,\"LOCATION_ID\":\"AABBCC\",\"MAC\":\"EBC0AEC423B400000000C3AFA6A7038D44C0724EE5622100002EF82EF5B368041553\",\"FECHA_HORA_TRANSACCION\":\"2023-12-29 02:51:21\",\"PURCHASE_LOG\":\"FF9C26810599C0AEC423B4000031003E8004F9\",\"EVENT_LOG\":\"0100005E030115AABBCC32C4618D00006415AABBCC32C4618D01010000\",\"LOAD_LOG\":\"267B00C000003EE4002710035DAEC40B83025BDB04F8\",\"SAM_SERIAL_HEX_ULTIMA_RECARGA\":\"AEC40B83\",\"SALDO_ANTES_TRANSACCION\":16000,\"PERFIL1\":\"0\",\"PERFIL2\":\"0\",\"PERFIL3\":\"0\",\"PROVIDER\":\"15\",\"RUTA\":\"B\",\"SAM_SERIAL_HEX\":\"AEC423B4\",\"ESTACION\":\"\",\"SALDO_DESPUES_TRANSACCION\":15900,\"SV_PROVIDER_KVC\":-64,\"ID_TRANSACCION_ORGANISMO\":0,\"TIPO_TRANSACCION\":\"03\"}"
        )
    }
    //shares
    var mySharedPreferences: MySharedPreferences = MySharedPreferences(context,PREFERENCE_NAME)
    var preferencesKey: MySharedPreferences = MySharedPreferences(context,PREFERENCE_FIRST_LAUNCH)



    //----------------------------------------------------------------------------------------------
    private object PreferencesKey {
        val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    }

    private val dataStore = context.dataStore

    suspend fun saveOnBoardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.onBoardingKey] = completed
        }
    }

    fun readOnBoardingState(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val onBoardingState = preferences[PreferencesKey.onBoardingKey] ?: false
                onBoardingState
            }
    }
}