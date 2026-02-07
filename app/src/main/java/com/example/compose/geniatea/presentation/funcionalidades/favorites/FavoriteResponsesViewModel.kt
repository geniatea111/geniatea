package com.example.compose.geniatea.presentation.funcionalidades.favorites

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.backendConection.BackendAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteResponsesViewModel : ViewModel() {

    private val _state = MutableStateFlow(FavoriteResponsesState())
    val state = _state.asStateFlow()

    // No init block fetching data automatically if it requires context for token.
    // Call loadFavorites from the UI LaunchedEffect or pass context here.

    fun onAction(action: FavoriteAction) {
        when(action) {
            FavoriteAction.OnBack -> { /* Handled by navigation */ }
            is FavoriteAction.OnFavoriteToggle -> {
                // TODO: Implement toggle favorite if needed in this screen, 
                // currently the requirement is just to display.
            }
            is FavoriteAction.OnItemClick -> {
                // Handled in UI
            }
        }
    }

    fun loadFavorites(context: Context) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val token = StoreDataUser(context).getToken()
                if (token != null) {
                    val response = BackendAPI.retrofitService.getFavoriteMessages("Bearer $token")
                    if (response.isSuccessful) {
                        val favorites = response.body()?.map { msg ->
                            FavoriteItem(
                                id = msg.messageId,
                                date = com.example.compose.geniatea.utils.Formats.formatFriendlyDate(msg.createdAt),
                                content = msg.message,
                                previousMessage = msg.previousMessage,
                                sessionId = msg.sessionId ?: -1L
                            )
                        } ?: emptyList()
                        _state.update { it.copy(favorites = favorites, isLoading = false) }
                    } else {
                        Log.e("FavoriteViewModel", "Error fetching favorites: ${response.code()}")
                         _state.update { it.copy(isLoading = false) }
                    }
                } else {
                     _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception fetching favorites", e)
                 _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class FavoriteResponsesState(
    val favorites: List<FavoriteItem> = emptyList(),
    val isLoading: Boolean = false
)

data class FavoriteItem(
    val id: Long,
    val date: String,
    val content: String,
    val previousMessage: String?,
    val sessionId: Long
)

sealed interface FavoriteAction {
    data object OnBack : FavoriteAction
    data class OnFavoriteToggle(val id: Int) : FavoriteAction
    data class OnItemClick(val sessionId: Long) : FavoriteAction
}
