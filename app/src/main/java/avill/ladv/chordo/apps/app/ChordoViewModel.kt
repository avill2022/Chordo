package avill.ladv.chordo.apps.app

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.apps.app.helpers.ChordTransposer
import avill.ladv.chordo.apps.app.helpers.Tab
import avill.ladv.chordo.apps.app.helpers.extractTabs
import avill.ladv.chordo.apps.app.helpers.extractTabsFlexible
import avill.ladv.chordo.apps.app.helpers.replaceTabsFlexible
import avill.ladv.chordo.apps.app.helpers.replaceTabsWithPlaceholders
import avill.ladv.chordo.apps.app.model.Chords
import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.local.db.room.entities.FavoriteSong
import avill.ladv.chordo.data.local.db.room.entities.Playlist
import avill.ladv.chordo.data.local.db.room.entities.PlaylistSong
import avill.ladv.chordo.data.network.RemoteDataSource
import avill.ladv.chordo.data.network.retrofit.APIClients.chordoApiService
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
        viewModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                val favoriteSongs = favorites.map { it.toSong() }
                _uiState.update { it.copy(favoriteSongs = favoriteSongs) }
                updateFilteredSongs()
            }
        }
    }

    fun addtabs(tabx:List<Tab>,count:Int = 0):String{
        var result = ""
        var index = count
        for (tab in tabx) { result += "\n[tab-${index}]\n${tab.content}"
            index++}
        return result
    }

    suspend fun getTabsFromServer(){
        try {
            val chords = repository.remoteDataSource.apiChords.getAll()
            _chords.value = chords
            chords.songs.forEach {
                val uniqueChords = ChordTransposer.getUniqueChords(it.content)
                it.chords = uniqueChords.joinToString(" ")
                if (it.tone.isBlank()) {
                    it.tone = uniqueChords.firstOrNull() ?: ""
                }
                var tabListTotal: List<Tab> = emptyList()
                var tabString = ""

                var tabList = extractTabs(it.content,0,1)
                tabString = addtabs(tabList,1)
                it.content = replaceTabsWithPlaceholders(it.content,0)
                tabListTotal += tabList

                tabList = extractTabs(it.content,1,tabListTotal.size)
                if(tabList.isNotEmpty())
                    tabString = tabString + "\n" + addtabs(tabList,tabListTotal.size)
                it.content = replaceTabsWithPlaceholders(it.content,1)
                tabListTotal += tabList

                tabList = extractTabs(it.content,2,tabListTotal.size)
                if(tabList.isNotEmpty())
                    tabString = tabString + "\n" + addtabs(tabList,tabListTotal.size)
                it.content = replaceTabsWithPlaceholders(it.content,2)

                tabList = extractTabsFlexible(it.content)
                if(tabList.isNotEmpty())
                    tabString = tabString + "\n" + addtabs(tabList,tabListTotal.size)
                it.content = replaceTabsFlexible(it.content)

                it.tab = tabString
            }

            updateFilteredSongs()
            val json = Gson().toJson(chords)
            repository.getMyFilesManager().save("chords_cache.json", json)
        } catch (e: Exception) {
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
            }
        } catch (fileEx: Exception) {
            Log.e("ChordoViewModel", "Error recovering from file: ${fileEx.message}")
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

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        updateFilteredSongs()
    }

    private fun updateFilteredSongs() {
        val query = _uiState.value.searchText
        val selectedTab = _uiState.value.selectedTab
        val allSongs = if (selectedTab == 0) _chords.value.songs else _uiState.value.favoriteSongs
        
        val filtered = if (query.isEmpty()) {
            allSongs
        } else {
            allSongs.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredSongs = filtered) }
    }

    fun getSongById(id: Int): Song? {
        if (id < 0 || id >= _chords.value.songs.size) return null
        return _chords.value.songs[id]
    }

    fun saveSong(song: Song) {
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

    fun deleteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSongs = _chords.value.songs.toMutableList()
            val removed = currentSongs.removeIf { it.name == song.name && it.folder == song.folder }
            if (removed) {
                _chords.value = _chords.value.copy(songs = currentSongs)
                val json = Gson().toJson(_chords.value)
                repository.getMyFilesManager().save("chords_cache.json", json)
                
                // Also remove from favorites if it's there
                repository.removeFavorite(song.name, song.folder)
                
                updateFilteredSongs()
            }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFav = repository.isFavorite(song.name, song.folder)
            if (isFav) {
                repository.removeFavorite(song.name, song.folder)
            } else {
                repository.addFavorite(song.toFavoriteSong())
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

    fun createPlaylist(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createPlaylist(name)
        }
    }

    fun addSongToPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSongToPlaylist(song.toPlaylistSong(playlistId))
        }
    }

    fun transposeSong(song: Song, semitones: Int) {
        val newContent = ChordTransposer.transpose(song.content, semitones)
        saveSong(song.copy(content = newContent))
    }

    fun restoreSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chords = repository.remoteDataSource.apiChords.getAll()
                val original = chords.songs.find { it.name == song.name && it.folder == song.folder }
                original?.let { saveSong(it) }
            } catch (e: Exception) {
                Log.e("ChordoViewModel", "Error restoring song: ${e.message}")
            }
        }
    }

    fun uploadChords() {
        viewModelScope.launch(Dispatchers.IO) {
            val json = Gson().toJson(_chords.value)
            try {
                chordoApiService.saveChords(json)
            } catch (e: Exception) {
                Log.e("ChordoViewModel", "Error UPLOAD song: ${e.message}")
            }

        }
    }

    fun downloadChords() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = chordoApiService.getChords()
            if (response.isSuccessful) {
                val jsonString = response.body() ?: return@launch
                val chordsFromFile = Gson().fromJson(jsonString, Chords::class.java)
                _chords.value = chordsFromFile
                updateFilteredSongs()
                repository.getMyFilesManager().save("chords_cache.json", jsonString)
            }
        }
    }
}

fun Song.toFavoriteSong() = FavoriteSong(
    name = name, folder = folder, content = content, tone = tone,
    rhythm = rhythm, tempo = tempo, harmony = harmony, melody = melody,
    chords = chords, tab = tab, structure = structure, author = author,
    urlsong = urlsong, urltutorial = urltutorial, urlmidi = urlmidi,
    urlgpt = urlgpt, urlpartiture = urlpartiture
)

fun FavoriteSong.toSong() = Song(
    name = name, folder = folder, content = content, tone = tone,
    rhythm = rhythm, tempo = tempo, harmony = harmony, melody = melody,
    chords = chords, tab = tab, structure = structure, author = author,
    urlsong = urlsong, urltutorial = urltutorial, urlmidi = urlmidi,
    urlgpt = urlgpt, urlpartiture = urlpartiture
)

fun Song.toPlaylistSong(playlistId: Long) = PlaylistSong(
    playlistId = playlistId, name = name, folder = folder, content = content,
    tone = tone, rhythm = rhythm, tempo = tempo, harmony = harmony,
    melody = melody, chords = chords, tab = tab, structure = structure,
    author = author, urlsong = urlsong, urltutorial = urltutorial,
    urlmidi = urlmidi, urlgpt = urlgpt, urlpartiture = urlpartiture
)
