package com.example.compose.geniatea.presentation.home

sealed interface HomeAction {
    data object OnBackPressed : HomeAction
    data object OnAccountPressed : HomeAction
    data object OnChatPressed : HomeAction
    data object OnJudgePressed : HomeAction
    data object OnFormalizerPressed : HomeAction
    data object OnResourcesPressed : HomeAction
    data object OnTaskListPressed : HomeAction

}