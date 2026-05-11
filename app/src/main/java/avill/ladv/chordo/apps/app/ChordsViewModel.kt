package avill.ladv.chordo.apps.app

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.network.RemoteDataSource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChordsViewModel  @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _chords = mutableStateOf(
        Chords(arrayListOf(),"",0)
    )
    val chords: State<Chords> = _chords

    fun getTabs(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val chords = repository.remoteDataSource.apiChords.getAll()
                _chords.value = chords
                
                // Save the response to an internal file
                val json = Gson().toJson(chords)
                repository.getMyFilesManager().save("chords_cache.json", json)
                
                Log.v(RemoteDataSource::class.simpleName,"isSuccessful ")
            }catch (e: Exception) {
                Log.e(RemoteDataSource::class.simpleName, "Error ${e}")
                
                // If there is no internet or another error, recover data from the file
                try {
                    val json = repository.getMyFilesManager().getInformation("chords_cache.json")
                    if (json.isNotEmpty()) {
                        val chordsFromFile = Gson().fromJson(json, Chords::class.java)
                        _chords.value = chordsFromFile
                        Log.v(RemoteDataSource::class.simpleName, "Recovered from file successfully")
                    }
                } catch (fileEx: Exception) {
                    Log.e(RemoteDataSource::class.simpleName, "Error recovering from file: ${fileEx.message}")
                }
            }
        }
    }
    fun getSongById(id: Int): Song? {
        //return _chords.value.songs.find { it. == id }
        if(_chords.value.songs.size-1<=id)
            return null
        return _chords.value.songs[id]
    }
}