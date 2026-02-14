package com.example.compose.geniatea.presentation.funcionalidades.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.theme.sdp
import com.example.compose.geniatea.theme.ssp

@Composable
fun FavoriteResponsesRoot(
    viewModel: FavoriteResponsesViewModel,
    onBackPressed: () -> Unit,
    onNavigateToChat: (Long) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites(context)
    }

    FavoriteResponsesScreen(
        state = state,
        onAction = { action ->
            if (action is FavoriteAction.OnBack) {
                onBackPressed()
            } else if (action is FavoriteAction.OnItemClick) {
                 onNavigateToChat(action.sessionId)
            } else {
                viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteResponsesScreen(
    state: FavoriteResponsesState,
    onAction: (FavoriteAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Consultas favoritas",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        fontSize = 22.ssp()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(FavoriteAction.OnBack) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.sdp())
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.sdp())
        ) {
            if (state.favorites.isEmpty() && !state.isLoading) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.sdp()),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.sdp())
                ) {
                    items(state.favorites) { item ->
                        FavoriteItemCard(item = item, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder Icon for the visual
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier
                .size(80.sdp())
                .padding(bottom = 16.sdp()),
            tint = Color(0xFF6495ED) // Cornflower blue ish
        )
        
        Text(
            text = "Tu lista está vacía",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 18.ssp()
        )
        
        Spacer(modifier = Modifier.height(8.sdp()))
        
        Text(
            text = "Guarda tus consultas haciendo clic\nen el icono \u2661",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.ssp()
        )
    }
}

@Composable
fun FavoriteItemCard(
    item: FavoriteItem,
    onAction: (FavoriteAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.sdp()))
            .background(Color(0xFFE8F0FE)) // Light Blue
            .clickable { onAction(FavoriteAction.OnItemClick(item.sessionId)) }
            .padding(20.sdp())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(12.sdp()))
                
                if (!item.previousMessage.isNullOrBlank()) {
                    Text(
                        text = item.previousMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.sdp()))
                    HorizontalDivider(
                        thickness = 1.sdp(),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.sdp()))
                }

                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.width(16.sdp()))
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to chat",
                modifier = Modifier.size(16.sdp()),
                tint = Color.Gray
            )
        }
    }
}
