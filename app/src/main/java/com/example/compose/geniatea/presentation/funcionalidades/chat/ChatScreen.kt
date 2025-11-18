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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.BottomSheetOptions
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.presentation.components.UserInput
import com.example.compose.geniatea.presentation.components.JumpToBottom
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
        onAction =  { action ->
            when (action) {
                is ChatAction.OnBackPressed -> onBackPressed()
                else -> { Unit }
            }
            viewModel.onAction(action)
        },
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

    val authorMe = stringResource(R.string.author_me)

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Scaffold(
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.home),
                onNavIconPressed = { onNavIconPressed() },
                optionalButton = true,
                onOptionalButtonPressed = { showBottomSheet = true },
                iconButton = R.drawable.svg_preferences
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
            )
        }
        Column(
            Modifier.fillMaxSize().padding(paddingValues)
                .background(color = Color.Transparent)
                .border(width = 2.dp, color = Color.Transparent),
        ) {
            if( uiState.messages.isEmpty() ) {
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
                onMessageSent = { content, image ->
                    val time = LocalDateTime.now().format(dateFormatter)
                    uiState.addMessage(
                        Message(authorMe, content, time, image),
                    )
                } ,
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(uiState.messages.size)
                    }
                },
                modifier = Modifier.imePadding().padding(top = 10.dp, start = 15.dp, end = 15.dp, bottom = bottomPaddingInput),
            )
        }
    }
}

const val ConversationTestTag = "ConversationTestTag"
val dateFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault())
//val dayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

@Composable
fun Messages(messages: List<Message>, scrollState: LazyListState, modifier: Modifier = Modifier, onAction: (ChatAction) -> Unit) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {

        LazyColumn(
            reverseLayout = false,
            state = scrollState,
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize(),
        ) {
            for (index in messages.indices) {
                val content = messages[index]
               /* val currentDate = LocalDateTime.parse(content.timestamp, dateFormatter).toLocalDate()
                val nextDate = if (index < messages.lastIndex) {
                    LocalDateTime.parse(messages[index + 1].timestamp, dateFormatter).toLocalDate()
                } else null*/

                item {
                    Message(
                        msg = content,
                        isUserMe = content.author != "Geni",
                        onAction
                    )
                }

             /*   if (nextDate == null || nextDate != currentDate) {
                    val label = when (currentDate) {
                        LocalDate.now() -> "Hoy"
                        LocalDate.now().minusDays(1) -> "Ayer"
                        else -> currentDate.format(dayFormatter)
                    }
                    item { DayHeader(label) }
                }*/
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
            /*
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }*/

                val lastIndex = messages.lastIndex
                if (lastIndex < 0) return@derivedStateOf false

                val visibleItem = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                visibleItem < lastIndex
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(messages.lastIndex)
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

    val ChatBubbleShape = if (isUserMe) {
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

        message.image?.let { uri ->
            val painter = rememberAsyncImagePainter(model = uri)
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape,
                modifier = Modifier.align(Alignment.End)
            ) {
                Image(
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp),
                    contentDescription = stringResource(id = R.string.attached_image),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape,
            modifier = Modifier
                .align(if (isUserMe) Alignment.End else Alignment.Start)
                .padding(start = if (isUserMe) 40.dp else 0.dp, end = if (isUserMe) 0.dp else 40.dp)
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
            )
        }


    }
}

@Composable
fun ClickableMessage(message: Message, isUserMe: Boolean) {
    val uriHandler = LocalUriHandler.current

  /*  val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe,
    )*/

    Text(
        text = message.content,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(16.dp),
       /* onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        else -> Unit
                    }
                }
        },*/
    )
}


@Preview
@Composable
fun Preview() {
    GenIATEATheme {
        ChatScreen(
            uiState = ChatState(
                initialMessages = listOf(
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
                initialMessages = listOf(
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
                initialMessages = listOf(),
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
                initialMessages = listOf(),
            ),
            onAction = { },
        )
    }
}