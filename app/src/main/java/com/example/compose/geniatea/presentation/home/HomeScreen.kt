package com.example.compose.geniatea.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.theme.titleApp
import com.example.compose.geniatea.theme.sdp
import com.example.compose.geniatea.theme.ssp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.compose.geniatea.presentation.components.HomeAppBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronRight
import com.example.compose.geniatea.data.Conversation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeRoot(
    viewModel: HomeViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = {
            viewModel.onAction(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeAppBar(
                onAccountPressed = { onAction(HomeAction.OnAccountPressed) },
                onFavoritesPressed = { onAction(HomeAction.OnFavoritesPressed) }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 0.sdp(), start = 24.sdp(), end = 24.sdp(), bottom = 24.sdp())
        ) {
            Box(
                modifier = Modifier
                    .height(300.sdp())
                    .padding(bottom = 16.sdp())
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.sdp(), 20.sdp(), 20.sdp(), 20.sdp()))
                    .paint(
                        painter = painterResource(id = R.drawable.background_home),
                        contentScale = ContentScale.Crop,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.sdp(), 0.sdp(), 20.sdp(), 20.sdp()))
                        .padding(top = 8.sdp(), bottom = 8.sdp())
                        .padding(horizontal = 24.sdp()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.saludo,
                        style = titleApp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 21.ssp(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(id = R.string.how_can_we_help_you_today),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(top = 4.sdp(), bottom = 8.sdp())
                            .fillMaxWidth(),
                        fontSize = 15.ssp(),
                        textAlign = TextAlign.Center

                    )

                    Button(
                        modifier = Modifier
                            .heightIn(min = 45.sdp())
                            .fillMaxWidth()
                            .padding(horizontal = 30.sdp()),
                        onClick = { onAction(HomeAction.OnChatPressed) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                    ) {
                        Text(
                            text = "Habla con Geni",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = W700
                        )
                    }
                }
            }

            RecentConversationsCard(conversations = state.conversations, onAction = onAction)

            optionsButtons(onAction = onAction, state.pendingTasks)

        }
    }
}

@Composable
fun RecentConversationsCard(conversations: List<Conversation>, onAction: (HomeAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.sdp())
            .clip(RoundedCornerShape(24.sdp()))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.sdp())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chats recientes",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = W700,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Ver todo",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onAction(HomeAction.OnSeeAllRecentChatsPressed) }
            )
            Icon(
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.sdp())
            )
        }

        Spacer(modifier = Modifier.height(16.sdp()))

        if (conversations.isEmpty()) {
            Text(
                text = "No hay chats recientes 🕸️",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.sdp()),
                textAlign = TextAlign.Center
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                conversations.take(2).forEach { conversation ->
                    RecentConversationItem(conversation = conversation, onAction = onAction)
                }
            }
        }
    }
}

@Composable
fun RecentConversationItem(conversation: Conversation, onAction: (HomeAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(HomeAction.OnRecentChatPressed(conversation.id, conversation.title)) }
            .padding(vertical = 8.sdp()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.sdp())
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = null,
                modifier = Modifier.size(24.sdp()),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(16.sdp()))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = W700
            )
            Text(
                text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(conversation.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(24.sdp()),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}


@Composable
fun optionsButtons(onAction: (HomeAction) -> Unit, tasks: List<String> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.sdp())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.sdp())
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.sdp()))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .heightIn(max = 300.sdp())
                    .padding(20.sdp())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.sdp())
                ) {
                    Button(
                        onClick = { onAction(HomeAction.OnTaskListPressed) },
                        modifier = Modifier
                            .padding(bottom = 10.sdp())
                            .fillMaxWidth(1f)
                            .height(30.sdp()),
                        shape = RoundedCornerShape(20.sdp()),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row {
                            Text(
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                text = "Tareas pendientes",
                                textAlign = TextAlign.Start,
                                fontWeight = W700,
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.chevron_right),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(start = 10.sdp())
                            )
                        }
                    }

                    if (tasks.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.sdp()), // adjust height as needed
                            verticalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            items(tasks.size) { index ->
                                task(text = tasks[index])
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.sdp())
                                .clip(RoundedCornerShape(50.sdp()))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(15.sdp()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay tareas pendientes 🕸️",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = W700,
                            )
                        }

                    }


                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.sdp())
        ) {
            Button(
                onClick = { onAction(HomeAction.OnJudgePressed) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 55.sdp()),
                shape = RoundedCornerShape(30.sdp()),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 8.sdp())
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth()
                            .padding(start = 16.sdp(), end = 32.sdp()),
                        text = "Intención",
                        textAlign = TextAlign.Start,
                        fontSize = 13.ssp()
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Button(
                onClick = { onAction(HomeAction.OnFormalizerPressed) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 55.sdp()),
                shape = RoundedCornerShape(30.sdp()),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 8.sdp())
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth()
                            .padding(start = 16.sdp(), end = 32.sdp()),
                        text = "Reescribir",
                        textAlign = TextAlign.Start,
                        fontSize = 13.ssp()
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun task(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.sdp())
            .clip(RoundedCornerShape(50.sdp()))
            .background(MaterialTheme.colorScheme.surface)
            .padding(15.sdp()),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = W700,
        )
    }
}

@Preview
@Composable
fun Preview() {
    GenIATEATheme {
        HomeScreen(
            state = HomeScreenState(saludo = "Buenos días, usuario"),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun PreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        HomeScreen(
            state = HomeScreenState(saludo = "Buenas noches, usuario"),
            onAction = {}
        )
    }
}
