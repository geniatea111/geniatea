package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onAccountPressed: () -> Unit = { }
    ) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp),
        actions = {
            Button(
                onClick = onAccountPressed,
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.svg_user),
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
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


        },

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeAppBarPreview() {
    GenIATEATheme {
        HomeAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeAppBarDark() {
    GenIATEATheme(isDarkTheme = true) {
        HomeAppBar()
    }
}
