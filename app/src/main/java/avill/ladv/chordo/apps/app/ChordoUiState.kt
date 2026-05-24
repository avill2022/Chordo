package avill.ladv.chordo.apps.app

import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.local.db.room.entities.Playlist

data class ChordoUiState(
    val saved: Boolean = false,
    val searchText: String = "",
    val filteredSongs: List<Song> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val loading: Boolean = false,
    val isLoading: Boolean = false, // Synced with ViewModel
    val isCurrentSongFavorite: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val selectedTab: Int = 0 // 0: All, 1: Favorites, 2: Playlists, 3: Tempo, 4: Tuner, 5: Tools
)
