package com.example.compose.geniatea.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.compose.geniatea.presentation.funcionalidades.chat.ChatAction
import com.example.compose.geniatea.presentation.funcionalidades.chat.ChatState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    state: ChatState,
    onAction: (ChatAction) -> Unit = {},
    onMessageSent: (String, Uri?) -> Unit,
    modifier: Modifier = Modifier,
    hideKeyboard: () -> Unit = {},
    resetScroll: () -> Unit = {}) {

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImage = it
        }
    )

   /* var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }*/

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    Surface {
            Row(modifier = modifier) {

                InputSelectorButton(
                    modifier = Modifier.align(Alignment.Bottom),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    icon = painterResource(id = R.drawable.ic_insert_photo),
                    description = stringResource(id = R.string.attach_photo_desc),
                )

                UserInputText(
                    textFieldValue = state.currentMessage,
                    onTextChanged = { state.currentMessage= it },
                    // Close extended selector if text field receives focus
                    onTextFieldFocused = { focused ->
                        if (focused) {
                            resetScroll()
                        }
                        textFieldFocusState = focused
                    },
                    sendMessageEnabled = state.currentMessage.text.isNotBlank(),
                    onMessageSent = {
                        onMessageSent(state.currentMessage.text, selectedImage)
                        // Reset text field and close keyboard
                        hideKeyboard()
                        resetScroll()
                        selectedImage = null
                        textFieldFocusState = false
                        state.currentMessage = TextFieldValue()
                        //hide the keyboard
                        onAction(ChatAction.OnMessageSend(state.currentMessage.text, selectedImage))
                        true
                    },
                    onAction = onAction,
                    selectedImage = selectedImage,
                    onRemoveImage = { selectedImage = null },
                )

//            UserInputSelector(
//                onAction = onAction,
//                photoLauncher = photoPickerLauncher,
//            )
            }
        }
}


//
//@Composable
//private fun UserInputSelector(
//    onAction: (ChatAction) -> Unit,
//    photoLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>?,
//    modifier: Modifier = Modifier,
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(48.dp)
//            .padding(start = 15.dp, end = 15.dp)
//            .wrapContentHeight(),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//
//
//
//
//        Spacer(
//            modifier = Modifier
//                .fillMaxHeight()
//                .width(8.dp)
//        )
//
//
//    }
//}

@Composable
private fun InputSelectorButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Painter,
    description: String,
) {

    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier
            .size(50.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    ) {
        Icon(
            icon,
            contentDescription = description,
        )

    }
}


@OptIn(ExperimentalAnimationApi::class)
@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    onMessageSent: () -> Unit,
    sendMessageEnabled: Boolean,
    onAction: (ChatAction) -> Unit = {},
    selectedImage : Uri? = null,
    onRemoveImage: () -> Unit = {},
    ) {
    val swipeOffset = remember { mutableStateOf(0f) }
    var isProcessing by remember { mutableStateOf(false) }
    var isRecordingMessage by remember { mutableStateOf(false) }
    val a11ylabel = stringResource(id = R.string.textfield_desc)

     val maxHeightTextInput = if(selectedImage != null){
        240.dp
    } else {
        120.dp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp, max = maxHeightTextInput)
            .padding(start = 10.dp)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                ),
                shape = RoundedCornerShape(25.dp)
            )
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.End,
    ) {
        Column {
            if(selectedImage != null){
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = stringResource(id = R.string.app_name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )

                    Button(
                        onClick = { onRemoveImage() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 5.dp, y = (-5).dp)
                            .size(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                        ),
                        contentPadding =  PaddingValues(0.dp),

                        ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(0.7f)
                        )
                    }
                }
            }

            Row{
                AnimatedContent(
                    targetState = isRecordingMessage,
                    label = "text-field",
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .align(Alignment.CenterVertically)
                ) { recording ->
                    Box(Modifier.fillMaxWidth()) {
                        if (recording) {
                            RecordingIndicator()
                        } else {
                            UserInputTextField(
                                textFieldValue,
                                onTextChanged,
                                keyboardType,
                                Modifier.fillMaxWidth().semantics {
                                    contentDescription = a11ylabel
                                },
                            )
                        }
                    }
                }


                if (textFieldValue.text.isEmpty()) {
                    RecordButton(
                        onAction = onAction,
                        recording = isRecordingMessage,
                        swipeOffset = { swipeOffset.value },
                        onSwipeOffsetChange = { offset -> swipeOffset.value = offset },
                        onStartRecording = {
                            val consumed = !isRecordingMessage
                            isRecordingMessage = true
                            consumed
                        },
                        onFinishRecording = {
                            isRecordingMessage = false
                            isProcessing = true
                        },
                        onCancelRecording = {
                            isRecordingMessage = false
                        },
                        modifier = Modifier,
                        isProcessing = isProcessing
                    )
                }

                LaunchedEffect(textFieldValue.text) {
                    if (textFieldValue.text.isNotEmpty()) {
                        isProcessing = false
                    }
                }

                val border = if (!sendMessageEnabled) {
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                } else {
                    null
                }

                val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                val buttonColors = IconButtonDefaults.outlinedIconButtonColors(
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = disabledContentColor,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.onTertiaryContainer),
                )

                OutlinedIconButton(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(end = 8.dp, bottom = 7.dp, top = 7.dp)
                        .height(35.dp)
                        .width(35.dp)
                        .minimumInteractiveComponentSize(),
                    enabled = sendMessageEnabled,
                    onClick = onMessageSent,
                    colors = buttonColors,
                    border = border,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = stringResource(id = R.string.send_message_desc),
                        tint = if (sendMessageEnabled) MaterialTheme.colorScheme.onTertiaryContainer else disabledContentColor,
                    )
                }
            }
        }
    }

}

@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
   // var lastFocusState by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(start = 20.dp, top = 8.dp, bottom = 8.dp, end = 10.dp)
            .align(Alignment.Center)
            /*.onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            }*/
            .wrapContentHeight(), // Permite que el campo crezca verticalmente
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send,
            capitalization = KeyboardCapitalization.Sentences,
        ),
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
    )

    val disableContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    if (textFieldValue.text.isEmpty()) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp),
            text = stringResource(R.string.textfield_hint),
            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor),
        )
    }
}


@Composable
private fun RecordingIndicator() {

    Row(
        modifier = Modifier
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gifsound))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(48.dp)
                .padding(5.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .clipToBounds(),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = stringResource(R.string.recording),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}


@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = { _, _ -> },
        state = ChatState(
            initialMessages = listOf()
        ),
        resetScroll = { /* No-op */ }
    )
}