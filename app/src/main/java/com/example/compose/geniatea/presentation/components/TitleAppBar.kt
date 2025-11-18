package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.DtGetaiTypography
import com.example.compose.geniatea.theme.GenIATEATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: String,
    optionalButton: Boolean = false,
    onOptionalButtonPressed: () -> Unit = { },
    iconButton: Int = R.drawable.svg_preferences,
) {
    TopAppBar(
        //transparent
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f)
        ),
        actions = {
            if (optionalButton) {
                IconButton(
                    onClick = onOptionalButtonPressed,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconButton),
                        contentDescription = stringResource(id = R.string.more_options)
                    )
                }
            }
        },
        title = {
            Text(
                style = DtGetaiTypography.titleLarge,
                text = title,
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed){
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                )
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TittleAppBarPreview() {
    GenIATEATheme {
        TitleAppBar(title = "Preview!")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TittleAppBarDark() {
    GenIATEATheme(isDarkTheme = true) {
        TitleAppBar(title =  "Preview!")
    }
}
