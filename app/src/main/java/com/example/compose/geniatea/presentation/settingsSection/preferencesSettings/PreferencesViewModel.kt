package com.example.compose.geniatea.presentation.settingsSection.preferencesSettings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.presentation.settingsSection.settings.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 3. Recibir 'application' y heredar de AndroidViewModel
class PreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(PreferencesScreenState())
    val state = _state

    private val _actionEvent = MutableLiveData<Event<PreferencesAction>>()
    val actionEvent: LiveData<Event<PreferencesAction>> = _actionEvent

    // 4. Usar getApplication() para obtener el contexto
    private val storeData = StoreDataUser(getApplication<Application>().applicationContext)

    fun onAction(action: PreferencesAction) {
        when (action) {
            PreferencesAction.OnBackPressed -> {
                _actionEvent.value = Event(PreferencesAction.OnBackPressed)
            }

            is PreferencesAction.OnDarkModeToggle -> {
                // Aquí llamamos a la función para guardar y cambiar el tema
                setDarkMode(!_state.value.isDarkMode)
            }

            is PreferencesAction.OnLanguagePress -> {
                _actionEvent.value = Event(PreferencesAction.OnLanguagePress(action.language))
            }

            PreferencesAction.OnNotificationPress -> {
                _actionEvent.value = Event(PreferencesAction.OnNotificationPress)
            }
        }
    }

    // 5. Eliminé el parámetro 'context' de aquí, ya no es necesario pasarlo desde la UI
    // Nota: Es mejor lanzar esto en viewModelScope si es una operación suspendida llamada desde onAction
    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch { // Lanzamos corrutina
            storeData.saveDarkMode(isDarkMode)
            _state.value = _state.value.copy(isDarkMode = isDarkMode)

            withContext(Dispatchers.Main) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )

                // Disparamos el evento para notificar a la UI si es necesario
                _actionEvent.value = Event(PreferencesAction.OnDarkModeToggle(isDarkMode))
            }
        }
    }
}






open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

}