package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, onExitOnboarding: () -> Unit) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()

    NavHost(navController = navController, startDestination = "step1") {
        composable("step1") {
            OnboardingStep1Screen(
                name = state.name,
                onNameChange = { viewModel.onAction(OnboardingAction.OnNameChange(it)) },
                onNext = { navController.navigate("step2") },
                onBack = onExitOnboarding
            )
        }
        composable("step2") {
            OnboardingStep2Screen(
                selectedPronoun = state.pronoun,
                onPronounSelected = { viewModel.onAction(OnboardingAction.OnPronounChange(it)) },
                onNext = { navController.navigate("step3") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step3") {
            OnboardingStep3Screen(
                birthDate = state.birthDate,
                onBirthDateChange = { viewModel.onAction(OnboardingAction.OnBirthDateChange(it)) },
                onNext = { navController.navigate("step4") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step4") {
            OnboardingStep4Screen(
                selectedDescription = state.description,
                onDescriptionSelected = { viewModel.onAction(OnboardingAction.OnDescriptionChange(it)) },
                onNext = { navController.navigate("step5") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step5") {
            OnboardingStep5Screen(
                showPictograms = state.showPictograms,
                onShowPictogramsChange = { viewModel.onAction(OnboardingAction.OnShowPictogramsChange(it)) },
                onFinish = { navController.navigate("step6") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("step6") {
            OnboardingStep6Screen(
                state = state,
                onAvatarSourceChange = { viewModel.onAction(OnboardingAction.OnAvatarSourceChange(it)) },
                onAvatarSelected = { uri, context -> viewModel.onAction(OnboardingAction.OnAvatarSelected(uri, context)) },
                onAvatarVideoSelected = { uri, context -> viewModel.onAction(OnboardingAction.OnAvatarVideoSelected(uri, context)) },
                onNext = { viewModel.onAction(OnboardingAction.OnRegister) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}