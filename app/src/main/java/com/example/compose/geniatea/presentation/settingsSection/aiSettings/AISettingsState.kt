package com.example.compose.geniatea.presentation.settingsSection.aiSettings

enum class AvatarSource {
    GENI, GALLERY
}

data class AISettingsState(
    val isClearLanguage: Boolean = false,
    val responseStyle: Float = 1f, // 0f: Conciso, 1f: Estándar, 2f: Extenso
    val fontSize: Int = 1, // 0: Small, 1: Medium, 2: Large
    val avatarSource: AvatarSource = AvatarSource.GENI,
    val showPictograms: Boolean = false,
    val avatarBitmap: android.graphics.Bitmap? = null
)
