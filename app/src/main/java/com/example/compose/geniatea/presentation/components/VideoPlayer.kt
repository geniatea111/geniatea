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
                                this@apply.tag = useCrop
                                applyScaleMatrix(this@apply, mp.videoWidth, mp.videoHeight)
                                if (autoPlay) {
                                    mp.start()
                                }
                            }
                            // Also update matrix when video size changes or view size changes
                            mediaPlayer.setOnVideoSizeChangedListener { mp, vidW, vidH ->
                                this@apply.tag = useCrop
                                applyScaleMatrix(this@apply, vidW, vidH)
                            }
                            mediaPlayer.prepareAsync()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        if (mediaPlayer.videoWidth > 0 && mediaPlayer.videoHeight > 0) {
                             this@apply.tag = useCrop
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
             // In case useCrop changes externally
             it.tag = useCrop
             // we could recalculate matrix here if we knew video size, but we are depending on the listener
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
    // For Fit, we want the SMALLER scale factor to ensure it fits entirely
    // We can infer useCrop from whether we want to crop or fit. 
    // Wait, applyScaleMatrix doesn't have the crop parameter right now. I should just pass `crop = true` as default or modify it. 
    // Actually, I'll calculate it assuming we want to use crop conditionally if passed.
    val crop = (textureView.tag as? Boolean) ?: false // We'll set tag when calling
    val scale = if (crop) kotlin.math.max(scaleX, scaleY) else kotlin.math.min(scaleX, scaleY)

    // Calculate the new dimensions
    val scaledWidth = videoWidth * scale
    val scaledHeight = videoHeight * scale

    // Scale the matrix (pivot logic handled below or via manual translate)
    // Actually, textureView.setTransform works by mapping the view text coordinates.
    
    // Standard Center Crop Matrix Logic:
    val pivotX = viewWidth / 2f
    val pivotY = viewHeight / 2f // Align to center

    val sx = scaledWidth / viewWidth
    val sy = scaledHeight / viewHeight

    matrix.setScale(sx, sy, pivotX, pivotY)
    
    textureView.setTransform(matrix)
}
