package com.example.compose.geniatea.presentation.funcionalidades.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.BottomSheetOptions
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.presentation.components.UserInput
import com.example.compose.geniatea.presentation.components.TypingIndicator
import com.example.compose.geniatea.presentation.components.JumpToBottom
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatRoot(
    viewModel: ChatViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreen(
        uiState = state,
        onAction = viewModel::onAction,
        onNavIconPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    uiState: ChatState,
    onAction: (ChatAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
) {

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Scroll logic moved to Messages composable to handle loader visibility

    Scaffold(
        topBar = {
            TitleAppBar(
                title = if (uiState.topic.isNotEmpty()) uiState.topic else stringResource(id = R.string.home),
                onNavIconPressed = { onNavIconPressed() },
//                optionalButton = true,
//                onOptionalButtonPressed = { showBottomSheet = true },
//                iconButton = R.drawable.svg_preferences
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        if(showBottomSheet){
            BottomSheetOptions(
                onDismiss = { showBottomSheet = false },
                chatStyle = uiState.chatStyle,
                onStyleChange = { onAction(ChatAction.OnStyleChange(it)) }
            )
        }
        Column(
            Modifier.fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(top = if (uiState.userAvatar != null) 0.dp else paddingValues.calculateTopPadding())
                .background(color = Color.Transparent)
                .border(width = 2.dp, color = Color.Transparent),
        ) {
            if( uiState.messages.isEmpty() && uiState.userAvatar == null ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.geniv2),
                            contentDescription = stringResource(id = R.string.empty_chat),
                            modifier = Modifier.size(120.dp).padding(bottom = 20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.no_messages),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.alpha(0.8f),
                        )
                    }
                }

            }else{
                Messages(
                    messages = uiState.messages,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    onAction = onAction,
                    avatar = uiState.userAvatar,
                    isGenerating = uiState.isGenerating
                )
            }


            val bottomPaddingInput = if(isImeVisible){
                10.dp
            }else{
                25.dp
            }

            UserInput(
                state = uiState,
                onAction = onAction,
                resetScroll = {
                    scope.launch {
                        if (uiState.messages.isNotEmpty()) {
                            scrollState.scrollToItem(uiState.messages.size - 1)
                        }
                    }
                },
                modifier = Modifier.imePadding().padding(top = 10.dp, start = 15.dp, end = 15.dp, bottom = bottomPaddingInput),
            )
        }
    }
}

const val ConversationTestTag = "ConversationTestTag"
val dateFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault())


@Composable
fun Messages(messages: List<Message>, scrollState: LazyListState, modifier: Modifier = Modifier, onAction: (ChatAction) -> Unit, avatar: android.graphics.Bitmap? = null, isGenerating: Boolean = false) {
    val scope = rememberCoroutineScope()
    
    // Auto-scroll when generating state changes or message content updates (redundancy for safety)
    LaunchedEffect(messages.size, messages.lastOrNull()?.content, isGenerating) {
        if (messages.isNotEmpty()) {
             // We scroll to the very last item, which might be the loader if isGenerating is true
             // The loader is an extra item, so size is index of last message, size + 1 is loader if present.
             // LazyColumn item count calculation:
             // 1 item (avatar) if not null + messages.size + 1 item (loader) if generating
             
             // Simplest approach: scroll to a large index, LazyList handles bounds safely
             scrollState.animateScrollToItem(Int.MAX_VALUE)
        }
    }

    Box(modifier = modifier) {

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize(),
        ) {
            if (avatar != null) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                         Image(
                            bitmap = avatar.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = stringResource(id = R.string.chat_header_greeting), 
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
            items(messages) { content ->
                Message(
                    msg = content,
                    isUserMe = content.author != "Geni",
                    onAction
                )
            }
            if (isGenerating && (messages.isEmpty() || messages.last().author != "Geni")) {
                item {
                   Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart // Align to left
                    ) {
                        Surface(
                            shape = ChatBubbleShapeGeni,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 0.dp) // Maintain consistent padding
                        ) {
                            TypingIndicator(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                dotSize = 8.dp,
                                dotColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                val lastIndex = messages.lastIndex
                if (lastIndex < 0) return@derivedStateOf false

                val visibleItem = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                visibleItem < lastIndex
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    if (messages.isNotEmpty()) {
                        scrollState.animateScrollToItem(messages.size - 1)
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp),
        )
    }
}

private val JumpToBottomThreshold = 56.dp


@Composable
fun Message(
    msg: Message,
    isUserMe: Boolean,
    onAction : (ChatAction) -> Unit = {  },
) {

    Row(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            modifier = Modifier.padding(end = 16.dp, start = 16.dp).weight(1f, fill = true),
            onAction
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    modifier: Modifier = Modifier,
    onAction: (ChatAction) -> Unit = {  },
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
    ) {

        ChatItemBubble(msg, isUserMe, onAction)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private val ChatBubbleShapeGeni = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeMe = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp),
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

@Composable
fun ChatItemBubble(message: Message, isUserMe: Boolean, onAction: (ChatAction) -> Unit = {}) {

    val chatBubbleShape = if (isUserMe) {
        ChatBubbleShapeMe
    } else {
        ChatBubbleShapeGeni
    }

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    }

    Column{
        if(!isUserMe){
            Row (
                modifier = Modifier.align(Alignment.End).padding(end = 45.dp),
            ){
                IconButton(
                    onClick = { onAction(ChatAction.OnSoundPressed(message.content)) },
                    modifier = Modifier
                        .size(32.dp)

                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_sound),
                        contentDescription = stringResource(id = R.string.home),
                        modifier = Modifier.size(24.dp).alpha(0.6f),
                    )
                }

                IconButton(
                    onClick = { /* Handle translate click */ },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_translate),
                        contentDescription = stringResource(id = R.string.home),
                        modifier = Modifier.size(24.dp).alpha(0.6f),
                    )
                }

                IconButton(
                    onClick = { onAction(ChatAction.OnCopyPressed(message.content)) },
                    modifier = Modifier
                        .size(32.dp)

                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_copy),
                        contentDescription = stringResource(id = R.string.home),
                        modifier = Modifier.size(24.dp).alpha(0.6f),
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_bookmark),
                        contentDescription = stringResource(id = R.string.home),
                        modifier = Modifier.size(24.dp).alpha(0.6f),
                    )
                }
            }
        }

        message.image?.let { imageString ->
            val isBase64 = !imageString.startsWith("content://") && !imageString.startsWith("file://")
            
            Surface(
                color = backgroundBubbleColor,
                shape = chatBubbleShape,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isBase64) {
                    val bitmap = remember(imageString) {
                         try {
                            val imageBytes = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT)
                            val decoded = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                             decoded?.asImageBitmap()
                        }catch (e: Exception){
                            e.printStackTrace()
                            null
                        }
                    }
                    
                    if (bitmap != null) {
                         Image(
                            bitmap = bitmap,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(160.dp),
                            contentDescription = stringResource(id = R.string.attached_image),
                        )
                    }
                } else {
                    val painter = rememberAsyncImagePainter(model = android.net.Uri.parse(imageString))
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(160.dp),
                        contentDescription = stringResource(id = R.string.attached_image),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Surface(
            color = backgroundBubbleColor,
            shape = chatBubbleShape,
            modifier = Modifier
                .align(if (isUserMe) Alignment.End else Alignment.Start)
                .padding(start = if (isUserMe) 40.dp else 0.dp, end = if (isUserMe) 0.dp else 40.dp)
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
            )
        }

        message.pictograms?.let { pictograms ->
            if (pictograms.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .align(if (isUserMe) Alignment.End else Alignment.Start)
                        .padding(top = 8.dp, start = if (isUserMe) 40.dp else 0.dp, end = if (isUserMe) 0.dp else 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pictograms) { url ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White, // White background for pictograms
                            modifier = Modifier.size(64.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = url),
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun ClickableMessage(message: Message, isUserMe: Boolean) {
    val uriHandler = LocalUriHandler.current

    Text(
        text = message.content,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(16.dp),
    )
}


@Preview
@Composable
fun Preview() {
    GenIATEATheme {
        ChatScreen(
            uiState = ChatState(
                messages = listOf(
                    Message(
                        author = "Geni",
                        content = "Hola, ¿cómo puedo ayudarte hoy?",
                        timestamp = "10:00 10/10/2025",
                    ),
                    Message(
                        author = "User",
                        content = "Hola, tengo una pregunta sobre mi cuenta.",
                        timestamp = "10:01 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Claro, ¿qué pregunta tienes?",
                        timestamp = "10:02 10/10/2025",
                    ),
                    Message(
                        author = "User",
                        content = "¿Cómo puedo cambiar mi contraseña?",
                        timestamp = "10:03 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Para cambiar tu contraseña, ve a la sección de configuración de tu cuenta y selecciona 'Cambiar contraseña'.",
                        timestamp = "10:04 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Hola, ¿cómo puedo ayudarte hoy?",
                        timestamp = "10:00 11/10/2025",
                    ),
                ),
            ),
            onAction = {}
           ,
        )
    }
}


@Preview
@Composable
fun PreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        ChatScreen(
            uiState = ChatState(
                messages = listOf(
                    Message(
                        author = "Geni",
                        content = "Hola, ¿cómo puedo ayudarte hoy?",
                        timestamp = "10:00 10/10/2025",
                    ),
                    Message(
                        author = "User",
                        content = "Hola, tengo una pregunta sobre mi cuenta.",
                        timestamp = "10:01 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Claro, ¿qué pregunta tienes?",
                        timestamp = "10:02 10/10/2025",
                    ),
                    Message(
                        author = "User",
                        content = "¿Cómo puedo cambiar mi contraseña?",
                        timestamp = "10:03 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Para cambiar tu contraseña, ve a la sección de configuración de tu cuenta y selecciona 'Cambiar contraseña'.",
                        timestamp = "10:04 10/10/2025",
                    ),
                    Message(
                        author = "Geni",
                        content = "Hola, ¿cómo puedo ayudarte hoy?",
                        timestamp = "10:00 11/10/2025",
                    ),
                ),
            ),
            onAction = { },
        )
    }
}


@Preview
@Composable
fun PreviewNoMessage() {
    GenIATEATheme(isDarkTheme = false) {
        ChatScreen(
            uiState = ChatState(
                messages = listOf(),
            ),
            onAction = { },
        )
    }
}


@Preview
@Composable
fun PreviewNoMessageDark() {
    GenIATEATheme(isDarkTheme = true) {
        ChatScreen(
            uiState = ChatState(
                messages = listOf(),
            ),
            onAction = { },
        )
    }
}
