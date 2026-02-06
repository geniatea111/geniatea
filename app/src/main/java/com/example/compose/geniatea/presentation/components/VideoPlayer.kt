package com.example.compose.geniatea.presentation.components

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
    isLooping: Boolean = true,
    autoPlay: Boolean = true,
    isMuted: Boolean = false,
    useCrop: Boolean = false
) {
    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    AndroidView(
        factory = {
            TextureView(context).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        val s = Surface(surface)
                        try {
                            mediaPlayer.setDataSource(context, uri)
                            mediaPlayer.setSurface(s)
                            mediaPlayer.isLooping = isLooping
                            if (isMuted) {
                                mediaPlayer.setVolume(0f, 0f)
                            }
                            mediaPlayer.setOnPreparedListener { mp ->
                                if (useCrop) {
                                    applyScaleMatrix(this@apply, mp.videoWidth, mp.videoHeight)
                                }
                                if (autoPlay) {
                                    mp.start()
                                }
                            }
                            // Also update matrix when video size changes or view size changes
                            mediaPlayer.setOnVideoSizeChangedListener { mp, vidW, vidH ->
                                if (useCrop) applyScaleMatrix(this@apply, vidW, vidH)
                            }
                            mediaPlayer.prepareAsync()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        if (useCrop && mediaPlayer.videoWidth > 0 && mediaPlayer.videoHeight > 0) {
                             applyScaleMatrix(this@apply, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
                        }
                    }

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        return false // MediaPlayer released in onDispose
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) { }
                }
            }
        },
        update = { 
             // Typically we don't update complex logic here for MediaPlayer to avoid re-prepare loops.
             // But if URI changes, we might need logic. Assuming URI stable for now or key will reconstruct.
        },
        modifier = modifier
    )
}

private fun applyScaleMatrix(textureView: TextureView, videoWidth: Int, videoHeight: Int) {
    val viewWidth = textureView.width.toFloat()
    val viewHeight = textureView.height.toFloat()

    if (videoWidth <= 0 || videoHeight <= 0 || viewWidth <= 0 || viewHeight <= 0) return

    val matrix = Matrix()
    
    val scaleX = viewWidth / videoWidth
    val scaleY = viewHeight / videoHeight

    // For Center Crop, we want the LARGER scale factor ensuring both dims are filled
    val scale = kotlin.math.max(scaleX, scaleY)

    // Calculate the new dimensions
    val scaledWidth = videoWidth * scale
    val scaledHeight = videoHeight * scale

    // Scale the matrix (pivot logic handled below or via manual translate)
    // Actually, textureView.setTransform works by mapping the view text coordinates.
    
    // Standard Center Crop Matrix Logic:
    val pivotX = viewWidth / 2f
    val pivotY = viewHeight / 2f

    val sx = scaledWidth / viewWidth
    val sy = scaledHeight / viewHeight

    matrix.setScale(sx, sy, pivotX, pivotY)
    
    textureView.setTransform(matrix)
}
