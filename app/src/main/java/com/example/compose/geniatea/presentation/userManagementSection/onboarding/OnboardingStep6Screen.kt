package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.stringResource
import com.example.compose.geniatea.theme.sdp
import com.example.compose.geniatea.theme.ssp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.settingsSection.aiSettings.AISettingsHeader
import com.example.compose.geniatea.presentation.settingsSection.aiSettings.AvatarSource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep6Screen(
    state: OnboardingState,
    onAvatarSourceChange: (AvatarSource) -> Unit,
    onAvatarSelected: (android.net.Uri, Context) -> Unit,
    onAvatarVideoSelected: (android.net.Uri, Context) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAvatarSelected(it, context) }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAvatarVideoSelected(it, context) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { ProgressIndicator(currentStep = 5, totalSteps = 6, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.sdp()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.geni),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.sdp())
                        .padding(bottom = 16.sdp())
                )

                Text(
                    text = stringResource(id = R.string.titleOnboarding4),
                    fontSize = 24.ssp(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.sdp())
                )

                AISettingsHeader(
                    selectedSource = state.avatarSource,
                    avatarBitmap = state.avatarBitmap,
                    avatarVideoUri = state.avatarVideoUri,
                    contentHeight = 120.sdp(),
                    onSourceChange = { source ->
                        onAvatarSourceChange(source)
                        if (source == AvatarSource.GALLERY) {
                            imageLauncher.launch("image/*")
                        } else if (source == AvatarSource.VIDEO_GALLERY) {
                            videoLauncher.launch("video/*")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.sdp()))
                
                Text(
                    text = stringResource(id = R.string.you_can_change_this_later),
                    fontSize = 14.ssp(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.sdp(), vertical = 32.sdp())
                    .heightIn(min = 56.sdp()),
                enabled = !state.isLoading
            ) {
                 if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.sdp()),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(id = R.string.finish), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
