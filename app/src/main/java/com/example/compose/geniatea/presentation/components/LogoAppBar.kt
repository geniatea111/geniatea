package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.theme.subtitleApp
import com.example.compose.geniatea.theme.titleApp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp),
        actions = actions,
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_geniatea),
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier
                        .size(24.dp) // smaller for alignment
                        .clickable(onClick = onNavIconPressed),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )


                Text(
                    text = stringResource(id = R.string.app_name),
                    style = subtitleApp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(top = 7.dp, start = 9.dp)
                )
            }


        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogoAppBarPreview() {
    GenIATEATheme {
        LogoAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogoAppBarDark() {
    GenIATEATheme(isDarkTheme = true) {
        LogoAppBar()
    }
}
