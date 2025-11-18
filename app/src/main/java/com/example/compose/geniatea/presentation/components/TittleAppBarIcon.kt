package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TittleAppBarIcon(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onAccountPressed: () -> Unit = { },
    ) {
    TopAppBar(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
            ),
        actions = {
            Button(
                onClick = onAccountPressed,
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.svg_settings),
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(text = "")
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TittleAppBarIconPreview() {
    GenIATEATheme {
        TittleAppBarIcon()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TittleAppBarIconDark() {
    GenIATEATheme(isDarkTheme = true) {
        TittleAppBarIcon()
    }
}
