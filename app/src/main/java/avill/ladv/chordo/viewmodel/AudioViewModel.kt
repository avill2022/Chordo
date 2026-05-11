package avill.ladv.chordo.viewmodel

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.util.AudioTrack
import avill.ladv.chordo.util.FileScannerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val fileScannerHelper: FileScannerHelper
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks = _tracks.asStateFlow()

    fun loadAudio() {
        viewModelScope.launch {
            _tracks.value = fileScannerHelper.scanAudioFiles()
        }
    }
}