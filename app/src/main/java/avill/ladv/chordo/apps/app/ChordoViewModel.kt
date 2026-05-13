package avill.ladv.chordo.apps.app

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.network.RemoteDataSource
import avill.ladv.chordo.apps.app.helpers.ChordTransposer
import avill.ladv.chordo.apps.app.model.Chords
import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.local.db.room.entities.FavoriteSong
import avill.ladv.chordo.data.local.db.room.entities.Playlist
import avill.ladv.chordo.data.local.db.room.entities.PlaylistSong
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

    init {
        viewModelScope.launch {
            repository.getAllPlaylists().collect { playlists ->
                _uiState.update { it.copy(playlists = playlists) }
            }
        }
    }

    suspend fun getTabsFromServer(){
        try {
            val chords = repository.remoteDataSource.apiChords.getAll()
            _chords.value = chords
            updateFilteredSongs()
            // Save the response to an internal file
            val json = Gson().toJson(chords)
            repository.getMyFilesManager().save("chords_cache.json", json)

            Log.v(RemoteDataSource::class.simpleName, "isSuccessful ")
        } catch (e: Exception) {
            Log.e(RemoteDataSource::class.simpleName, "Error ${e}")
            // If there is no internet or another error, recover data from the file
            getTabsFromLocal()
        }
    }
    fun getTabsFromLocal(){
        try {
            val json = repository.getMyFilesManager().getInformation("chords_cache.json")
            if (json.isNotEmpty()) {
                val chordsFromFile = Gson().fromJson(json, Chords::class.java)
                _chords.value = chordsFromFile
                updateFilteredSongs()
                Log.v(RemoteDataSource::class.simpleName, "Recovered from file successfully")
            }
        } catch (fileEx: Exception) {
            Log.e(RemoteDataSource::class.simpleName, "Error recovering from file: ${fileEx.message}")
        }
    }
    fun getTabs() {
        viewModelScope.launch(Dispatchers.IO) {
            getTabsFromServer()
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
        // ... (existing code)
        val currentSongs = _chords.value.songs.toMutableList()
        val index = currentSongs.indexOfFirst { it.name == song.name && it.folder == song.folder }
        if (index != -1) {
            currentSongs[index] = song
        } else {
            currentSongs.add(song)
        }
        _chords.value = _chords.value.copy(songs = currentSongs)
        val json = Gson().toJson(_chords.value)
        repository.getMyFilesManager().save("chords_cache.json", json)

        updateFilteredSongs()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFav = repository.isFavorite(song.name, song.folder)
            if (isFav) {
                repository.removeFavorite(song.name, song.folder)
            } else {
                repository.addFavorite(
                    FavoriteSong(
                        name = song.name,
                        folder = song.folder,
                        content = song.content,
                        tone = song.tone,
                        rhythm = song.rhythm,
                        tempo = song.tempo,
                        harmony = song.harmony,
                        melody = song.melody,
                        chords = song.chords,
                        tab = song.tab,
                        structure = song.structure,
                        author = song.author,
                        urlsong = song.urlsong,
                        urltutorial = song.urltutorial,
                        urlmidi = song.urlmidi,
                        urlgpt = song.urlgpt,
                        urlpartiture = song.urlpartiture
                    )
                )
            }
            checkIfFavorite(song)
        }
    }

    fun checkIfFavorite(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFav = repository.isFavorite(song.name, song.folder)
            _uiState.update { it.copy(isCurrentSongFavorite = isFav) }
        }
    }

    // Playlist Methods
    fun createPlaylist(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(playlist)
        }
    }

    fun addSongToPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSongToPlaylist(
                PlaylistSong(
                    playlistId = playlistId,
                    name = song.name,
                    folder = song.folder,
                    content = song.content,
                    tone = song.tone,
                    rhythm = song.rhythm,
                    tempo = song.tempo,
                    harmony = song.harmony,
                    melody = song.melody,
                    chords = song.chords,
                    tab = song.tab,
                    structure = song.structure,
                    author = song.author,
                    urlsong = song.urlsong,
                    urltutorial = song.urltutorial,
                    urlmidi = song.urlmidi,
                    urlgpt = song.urlgpt,
                    urlpartiture = song.urlpartiture
                )
            )
        }
    }

    fun removeSongFromPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeSongFromPlaylist(playlistId, song.name, song.folder)
        }
    }

    // Transposition Methods
    fun transposeSong(song: Song, semitones: Int) {
        val newContent = ChordTransposer.transpose(song.content, semitones)
        saveSong(song.copy(content = newContent))
    }

    fun restoreSong(song: Song) {
        if (song.tone.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // To restore the original, we re-fetch from the server 
                // as the local cache might already be modified and saved.
                val chords = repository.remoteDataSource.apiChords.getAll()
                val original = chords.songs.find { 
                    it.name == song.name && it.folder == song.folder 
                }
                original?.let {
                    saveSong(it)
                }
            } catch (e: Exception) {
                Log.e("ChordoViewModel", "Error restoring song: ${e.message}")
            }
        }
    }
}
