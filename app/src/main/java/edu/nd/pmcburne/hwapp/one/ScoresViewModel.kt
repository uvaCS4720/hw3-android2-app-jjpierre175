package edu.nd.pmcburne.hwapp.one
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ScoresUiState( // UI state used by the ScoresScreen
    val games: List<GameEntity> = emptyList(), // list of games currently displayed on the screen
    val isLoading: Boolean = false, // shows whether scores are currently being loaded
    val error: String? = null, // error message if loading fails
    val selectedDate: LocalDate = LocalDate.now(), // currently selected date for the scoreboard
    val isMens: Boolean = true // shows whether men's and women's gams are shown
)

@HiltViewModel
class ScoresViewModel @Inject constructor(
    private val repository: BasketballRepository
): ViewModel () { // manages UI state and communicates with BasketballRepository to get game data

    private val _uiState = MutableStateFlow(ScoresUiState()) // mutable state
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow() // public read only state exposed to the UI

    private var gamesJob: kotlinx.coroutines.Job? = null // job used to observe database changes

    init { // called when viewmodel created then loads scores and observes updates
        observeAndRefresh()
    }

    private fun observeAndRefresh() { // observes the room database for game updates, when database changes then UI state updates
        val state = uiState.value
        // cancel previous observer
        gamesJob?.cancel() // cancel any existing database observer
        gamesJob = viewModelScope.launch {
            repository.getGames(
                gender = if (state.isMens) "men" else "women",
                date = state.selectedDate
            ).collect { games ->
                _uiState.update { it.copy(games = games) } // update UI state whenever the database emits new data
            }
        }
        refresh() // refresh data from the api
    }

    fun setDate(date: LocalDate) { // updates the selected date and reloads scores
        _uiState.update { it.copy(selectedDate = date) }
        observeAndRefresh()
    }

    fun setGender(isMens: Boolean) { // toggles between men's and women's basketball scores
        _uiState.update { it.copy(isMens = isMens) }
        observeAndRefresh()
    }

    fun refresh() { // refreshes scores from the api, updates loading state and handles potential errors
        val state = uiState.value
        viewModelScope.launch{
            _uiState.update { it.copy(isLoading = true, error = null) } // show loading indicator
            val result = repository.refreshGames(
                gender = if (state.isMens) "men" else "women",
                date = state.selectedDate
            )
            _uiState.update { it.copy(isLoading = false) } // hide loading indicator
            result.onFailure { exception ->
                if (_uiState.value.games.isEmpty()) { // show error only if no cached data exists
                    _uiState.update {it.copy(error = exception.message)}
                }
            }
        }
    }
}