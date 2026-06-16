package avill.ladv.chordo.apps.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.data.local.shared.PreferencesKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesKey: PreferencesKey
) : ViewModel() {

    private val _isPermission = MutableStateFlow(true)
    val isPermission: StateFlow<Boolean> = _isPermission.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    private val _isAudioPermissionGranted = MutableStateFlow(false)
    val isAudioPermissionGranted: StateFlow<Boolean> = _isAudioPermissionGranted.asStateFlow()

    init {

    }
    fun init(){
        viewModelScope.launch {
            preferencesKey.readPermission.collect {
                _isPermission.value = it
                preferencesKey.readFirstLaunch.collect { value ->
                    _isFirstLaunch.value = value
                }
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesKey.saveFirstLaunch(false)
        }
    }
    fun completePermission() {
        viewModelScope.launch {
            preferencesKey.savePermissionRequest(false)
        }
    }

    fun updatePermissionStatus(isGranted: Boolean) {
        _isAudioPermissionGranted.value = isGranted
    }
}
