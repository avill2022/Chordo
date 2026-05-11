package avill.ladv.chordo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Base ViewModel for MVI (Model-View-Intent) pattern.
 * @param State Represents the UI State.
 * @param Event Represents UI Actions or Intents.
 */
abstract class BaseViewModel<State, Event>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /**
     * Updates the state using a reducer function.
     */
    protected fun updateState(reducer: (State) -> State) {
        _uiState.update(reducer)
    }

    /**
     * Handles incoming UI events.
     */
    abstract fun onEvent(event: Event)
}
