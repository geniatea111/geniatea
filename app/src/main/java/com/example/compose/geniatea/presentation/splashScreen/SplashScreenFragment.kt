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
import com.example.compose.geniatea.data.StoreDataUser.Companion.dataStore
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.getValue

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate XML layout containing ComposeView
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Compose splash screen
        rootView.findViewById<ComposeView>(R.id.compose_view)?.setContent {
            GenIATEATheme {
                SplashScreen() // your composable logo / animation
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val isLogged = requireContext().dataStore.data
                .map { it[StoreDataUser.IS_LOGGED_IN] ?: false }
                .first()

            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            val navController = navHostFragment.navController
            val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
            //graph.setStartDestination(if (isLogged) R.id.nav_home else R.id.nav_prelogin)
            graph.setStartDestination(R.id.nav_home)
            navController.graph = graph
        }


    }
}
