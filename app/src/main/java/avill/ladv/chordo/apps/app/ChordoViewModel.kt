package avill.ladv.chordo.apps.app

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.network.RemoteDataSource
import avill.ladv.chordo.apps.app.model.Chords
import avill.ladv.chordo.apps.app.model.Song
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChordoViewModel
@Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChordoUiState())
    val uiState: StateFlow<ChordoUiState> = _uiState.asStateFlow()

    private val _chords = mutableStateOf(
        Chords(arrayListOf(), "", 0)
    )
    val chords: State<Chords> = _chords

    fun getTabs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chords = repository.remoteDataSource.apiChords.getAll()
                _chords.value = chords
                //updateFilteredSongs()

                // Save the response to an internal file
                val json = Gson().toJson(chords)
                repository.getMyFilesManager().save("chords_cache.json", json)

                Log.v(RemoteDataSource::class.simpleName, "isSuccessful ")
            } catch (e: Exception) {
                Log.e(RemoteDataSource::class.simpleName, "Error ${e}")

                // If there is no internet or another error, recover data from the file
                try {
                    val json = repository.getMyFilesManager().getInformation("chords_cache.json")
                    if (json.isNotEmpty()) {
                        val chordsFromFile = Gson().fromJson(json, Chords::class.java)
                        _chords.value = chordsFromFile
                        //updateFilteredSongs()
                        Log.v(RemoteDataSource::class.simpleName, "Recovered from file successfully")
                    }
                } catch (fileEx: Exception) {
                    Log.e(RemoteDataSource::class.simpleName, "Error recovering from file: ${fileEx.message}")
                }
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _uiState.update { it.copy(searchText = text) }
        updateFilteredSongs()
    }

    private fun updateFilteredSongs() {
        val query = _uiState.value.searchText
        val allSongs = _chords.value.songs
        val filtered = if (query.isEmpty()) {
            allSongs
        } else {
            allSongs.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.folder.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredSongs = filtered) }
    }

    fun getSongById(id: Int): Song? {
        if (id < 0 || id >= _chords.value.songs.size) return null
        return _chords.value.songs[id]
    }

    fun saveSong(song: Song) {
        // Here we should save it to the repository.
        // For now, let's update the local list if it's an edit or add if it's new
        // Ideally, the repository would handle the persistence and we'd observe the changes.
        // Assuming we are just updating the in-memory list for this exercise.
        val currentSongs = _chords.value.songs.toMutableList()
        val index = currentSongs.indexOfFirst { it.name == song.name && it.folder == song.folder }
        if (index != -1) {
            currentSongs[index] = song
        } else {
            currentSongs.add(song)
        }
        _chords.value = _chords.value.copy(songs = currentSongs)
        updateFilteredSongs()
    }
}
