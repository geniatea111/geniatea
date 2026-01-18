package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.domain.User
import com.example.compose.geniatea.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface OnboardingNavigationEvent {
    data class OnRegisterSuccess(val user: User) : OnboardingNavigationEvent
    data class OnRegisterError(val error: String) : OnboardingNavigationEvent
}

// 1. Usamos AndroidViewModel para tener el contexto de forma segura
class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _navigationEvent = MutableLiveData<Event<OnboardingNavigationEvent>>()
    val navigationEvent: LiveData<Event<OnboardingNavigationEvent>> = _navigationEvent

    // 2. Inicializamos StoreDataUser con el contexto de la aplicación
    private val dataStore = StoreDataUser(getApplication<Application>().applicationContext)

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnNameChange -> _state.update { it.copy(name = action.name) }
            is OnboardingAction.OnPronounChange -> _state.update { it.copy(pronoun = action.pronoun) }
            is OnboardingAction.OnBirthDateChange -> _state.update { it.copy(birthDate = action.birthDate) }
            is OnboardingAction.OnDescriptionChange -> _state.update { it.copy(description = action.description) }
            is OnboardingAction.OnShowPictogramsChange -> _state.update { it.copy(showPictograms = action.show) }
            is OnboardingAction.OnRegister -> updateUser()
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // 3. CORRECCIÓN PRINCIPAL:
                // Llamamos a getUser() directamente. No usamos .first() porque getUser ya devuelve el objeto User?
                val storedUser = dataStore.getUser()

                if (storedUser != null) {
                    // 4. Usamos las propiedades del objeto User directamente.
                    // No necesitas volver a definir las keys (userIdKey, etc), StoreDataUser ya lo hizo.

                    val formattedDate = try {
                        LocalDate.parse(_state.value.birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    } catch (e: Exception) {
                        // Manejo por si la fecha no tiene el formato correcto
                        _state.value.birthDate
                    }

                    val gender = when (_state.value.pronoun) {
                        "Hombre" -> "M"
                        "Mujer" -> "F"
                        else -> "X"
                    }

                    // 5. Hacemos la llamada a la API
                    // Nota: Es recomendable envolver llamadas de red en Dispatchers.IO
                    val response = withContext(Dispatchers.IO) {
                        BackendAPI.retrofitService.updateUser(
                            token = "Bearer ${storedUser.accessToken}", // Usamos la propiedad del objeto
                            userId = storedUser.id.toString(),
                            updaterRequest = ApiService.UpdateUserRequest(
                                name = _state.value.name,
                                gender = gender,
                                birthdate = formattedDate,
                                showPictograms = _state.value.showPictograms
                            )
                        )
                    }

                    if (response.isSuccessful) {
                        // Actualizamos el usuario localmente con los nuevos datos si es necesario
                        val updatedUser = storedUser.copy(
                            name = _state.value.name,
                            gender = gender,
                            birthdate = formattedDate,
                            showPictograms = _state.value.showPictograms
                        )
                        // Guardamos los cambios en local también para mantener consistencia
                        dataStore.updateUser(updatedUser)

                        _navigationEvent.value = Event(OnboardingNavigationEvent.OnRegisterSuccess(updatedUser))
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Update failed"
                        _navigationEvent.value = Event(OnboardingNavigationEvent.OnRegisterError(errorMsg))
                    }
                } else {
                    // El usuario es null (no está logueado o hubo error leyendo datos)
                    _navigationEvent.value = Event(OnboardingNavigationEvent.OnRegisterError("User not found locally"))
                }

            } catch (e: SocketTimeoutException) {
                // NOTA: Usar Toast dentro del ViewModel no es ideal.
                // Es mejor enviar un evento de error y que la UI muestre el Toast.
                // Pero si mantienes el Toast, usa getApplication()
                Toast.makeText(getApplication(), "Connection not available", Toast.LENGTH_SHORT).show()
                _navigationEvent.value = Event(OnboardingNavigationEvent.OnRegisterError("Connection Timeout"))
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                _navigationEvent.value = Event(OnboardingNavigationEvent.OnRegisterError(e.message ?: "Unknown error"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
