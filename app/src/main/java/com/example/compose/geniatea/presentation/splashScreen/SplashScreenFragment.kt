package com.example.compose.geniatea.presentation.splashScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.getValue
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class SplashScreenFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.compose_view)?.setContent {
            val fontSize by settingsViewModel.fontSize.collectAsState()
            val isDark by settingsViewModel.darkMode.collectAsState()
            val themeVariant by appColorViewModel.themeVariant.collectAsState()

            val fontScale = when(fontSize) {
                "S" -> 0.85f
                "L" -> 1.15f
                else -> 1.0f
            }

            GenIATEATheme(
                themeVariant = themeVariant,
                isDarkTheme = isDark,
                fontScale = fontScale
            ) {
                SplashScreen()
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usamos lifecycleScope.launch.
        // Nota: launchWhenStarted está deprecado en versiones nuevas, es mejor launch normal o repeatOnLifecycle,
        // pero para un splash screen simple, launch está bien.
        viewLifecycleOwner.lifecycleScope.launch {

            // 1. Instanciamos tu clase administradora (ella sí tiene acceso al dataStore privado)
            val storeData = StoreDataUser(requireContext())

            // 2. Leemos el valor usando la variable que creamos en el Paso 1
            // Usamos .first() porque solo queremos el valor actual una vez, no quedarnos escuchando cambios
            val isLogged = storeData.isUserLoggedIn.first()

            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            val navController = navHostFragment.navController
            val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)

            // 3. Decidimos el destino
            if (isLogged) {
                graph.setStartDestination(R.id.nav_home)
            } else {
                // Asumiendo que tienes un id para el login, cámbialo por el correcto
                graph.setStartDestination(R.id.nav_prelogin)
            }

            navController.graph = graph
        }
    }
}
