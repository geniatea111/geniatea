package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.userManagementSection.onboarding.OnboardingAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    isVisible : Boolean = true,
    onAction : (OnboardingAction) -> Unit = { },
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            OutlinedButton(
                onClick = { onAction(OnboardingAction.OnSkipPressed) },
                modifier = Modifier,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(1f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = stringResource(id = R.string.skip),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }
        },
        title = {
            Text(
                text = "",
                fontSize = 20.sp
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (isVisible)
            OutlinedButton(
                onClick = onNavIconPressed,
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(1f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier
                )
            }
        },


        modifier = modifier.padding(start = 10.dp, end = 10.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun OnboardingAppBarPreview() {
    GenIATEATheme {
        OnboardingAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun OnboardingAppBarDark() {
    GenIATEATheme(isDarkTheme = true) {
        OnboardingAppBar()
    }
}
