package com.example.compose.geniatea.presentation.settingsSection.locationSettings

interface LocationAction {
    data object OnBackPressed : LocationAction
    data object OnAddLocation : LocationAction
    data object OnEditLocation : LocationAction
    data object OnDeleteLocation : LocationAction
}