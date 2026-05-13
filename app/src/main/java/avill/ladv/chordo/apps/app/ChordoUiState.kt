package avill.ladv.chordo.apps.app

import avill.ladv.chordo.apps.app.model.Song

data class ChordoUiState(
    val saved: Boolean = false,
    val searchText: String = "",
    val filteredSongs: List<Song> = emptyList(),
    val loading: Boolean = false
)