package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.material3.CenterAlignedTopAppBar

import android.net.Uri
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: String,
    optionalButton: Boolean = false,
    onOptionalButtonPressed: () -> Unit = { },
    iconButton: Int = R.drawable.svg_preferences,
    avatar: Bitmap? = null,
    isSpeaking: Boolean = false,
    videoUri: Uri? = null
) {
    if (avatar != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 40.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .heightIn(max = 200.dp)
        ) {
            if (isSpeaking && videoUri != null) {
                 VideoPlayer(
                     uri = videoUri,
                     modifier = Modifier
                         .fillMaxWidth()
                         .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                     isMuted = true,
                     useCrop = false
                 )
            } else {
                 Image(
                    bitmap = avatar.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onNavIconPressed,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        tint = Color.Black,
                        contentDescription = null,
                    )
                }

                if (optionalButton) {
                    IconButton(
                        onClick = onOptionalButtonPressed,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = iconButton),
                            tint = Color.Black,
                            contentDescription = stringResource(id = R.string.more_options)
                        )
                    }
                }
            }
        }
    } else {
        TopAppBar(
            //transparent
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
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
