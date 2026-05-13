package avill.ladv.chordo.apps.app

import avill.ladv.chordo.apps.app.model.Song
import avill.ladv.chordo.data.local.db.room.entities.Playlist

data class ChordoUiState(
    val saved: Boolean = false,
    val searchText: String = "",
    val filteredSongs: List<Song> = emptyList(),
    val loading: Boolean = false,
    val isCurrentSongFavorite: Boolean = false,
    val playlists: List<Playlist> = emptyList()
)