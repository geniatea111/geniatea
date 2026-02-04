package com.example.compose.geniatea.presentation.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
    isLooping: Boolean = true,
    autoPlay: Boolean = true
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            VideoView(context).apply {
                setVideoURI(uri)
                setOnPreparedListener { mp ->
                    mp.isLooping = isLooping
                    if (autoPlay) {
                        start()
                    }
                }
            }
        },
        update = { videoView ->
            videoView.setVideoURI(uri)
            if (autoPlay) {
                videoView.start()
            }
        },
        modifier = modifier
    )
}
