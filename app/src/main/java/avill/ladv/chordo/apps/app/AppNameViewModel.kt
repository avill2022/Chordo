package avill.ladv.chordo.apps.app

import androidx.lifecycle.ViewModel
import avill.ladv.chordo.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppNameViewModel
@Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppNameUiState())
    val uiState: StateFlow<AppNameUiState> = _uiState

}