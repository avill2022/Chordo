package avill.ladv.chordo.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avill.ladv.chordo.apps.app.navigation.Chordo
import avill.ladv.chordo.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val repository: Repository,
    //this is going to create until the repository is used
    //private val repository: Lazy<DataStoreRepository>
) : ViewModel() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(Chordo.Splash.route)
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            repository.localDataSource.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = Chordo.Permissions.route
                } else {
                    _startDestination.value = Chordo.Splash.route
                }
            }
            _isLoading.value = false
        }
    }
    fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.saveOnBoardingState(completed = completed)
        }
    }

    fun onTriggerEvent(){
        viewModelScope.launch {
            try {
                delay(1000)
            }catch (e: Exception){
                Log.e("", "launchJob: Exception: ${e}, ${e.cause}")
                e.printStackTrace()
            }
        }
    }
}