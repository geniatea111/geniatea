package com.example.compose.geniatea.presentation.settingsSection.appColor

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.AppColorVariant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppColorViewModel(application: Application) : AndroidViewModel(application) {

    private val _actionEvent = MutableLiveData<Event<AppColorAction>>()
    val actionEvent: LiveData<Event<AppColorAction>> = _actionEvent

    // 2. Usamos 'getApplication<Application>().applicationContext'
    private val storeData = StoreDataUser(getApplication<Application>().applicationContext)

    private val _state = MutableStateFlow(AppColorState())
    val state = _state

    private val _themeVariant = MutableStateFlow(AppColorVariant.BLUE)
    val themeVariant: StateFlow<AppColorVariant> = _themeVariant

    fun setThemeVariant(theme: AppColorVariant) {
        _themeVariant.value = theme
    }

    // 3. Ya no necesitas pasar 'context' aquí si storeData ya está inicializado arriba
    fun updateThemeVariant(variant: AppColorVariant) {
        Log.i("MainActivity", "Updating theme variant to $variant")
        viewModelScope.launch {
            storeData.setThemeVariant(variant)
            _themeVariant.value = variant
        }
    }

    fun onAction(action: AppColorAction) {
        when (action) {
            AppColorAction.OnBackPressed -> {
                _actionEvent.value = Event(AppColorAction.OnBackPressed)
            }
            AppColorAction.OnBluePressed -> {
                _actionEvent.value = Event(AppColorAction.OnBluePressed)
            }
            AppColorAction.OnPinkPressed -> {
                Log.d("MainActivity", "Pink theme selected")
                _actionEvent.value = Event(AppColorAction.OnPinkPressed)
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