package com.example.compose.geniatea.presentation.userManagementSection.prelogin

sealed interface PreloginAction {
    data object OnBackPressed: PreloginAction
    data object OnLoginPressed: PreloginAction
    data object OnRegisterPressed: PreloginAction
    data object OnTermsOfServiceClicked: PreloginAction
    data object OnPrivacyPolicyClicked: PreloginAction
}