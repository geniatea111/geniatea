package com.example.compose.geniatea.presentation.settingsSection.locations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Park
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.ApiService.LocationDTO
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    viewModel: LocationViewModel,
    onBackPressed: () -> Unit,
    onNavigateToMap: (LocationDTO?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<LocationDTO?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.locations_title),
                onNavIconPressed = onBackPressed
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToMap(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Location", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.locations.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.locations_empty),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.sdp())
                ) {
                    items(state.locations) { location ->
                        LocationItem(
                            location = location,
                            onEdit = { onNavigateToMap(location) },
                            onDelete = { showDeleteDialog = location }
                        )
                        Spacer(modifier = Modifier.height(8.sdp()))
                    }
                }
            }
        }

        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text(stringResource(id = R.string.locations_delete_title)) },
                text = { Text(stringResource(id = R.string.locations_delete_message, showDeleteDialog?.name ?: "")) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog?.let { viewModel.deleteLocation(it.id) }
                        showDeleteDialog = null
                    }) {
                        Text(stringResource(id = R.string.action_delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text(stringResource(id = R.string.action_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun LocationItem(
    location: LocationDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.sdp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForLocationName(location.name),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.sdp())
            )
            Spacer(modifier = Modifier.width(16.sdp()))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun getIconForLocationName(name: String): ImageVector {
    val lowerName = name.lowercase()
    return when {
        listOf("casa", "hogar", "piso", "home", "house", "apartment").any { lowerName.contains(it) } -> Icons.Rounded.Home
        listOf("instituto", "escuela", "colegio", "universidad", "facultad", "clase", "school", "college", "university", "class").any { lowerName.contains(it) } -> Icons.Rounded.School
        listOf("trabajo", "oficina", "empresa", "work", "office", "company", "job").any { lowerName.contains(it) } -> Icons.Rounded.Work
        listOf("gym", "gimnasio", "deporte", "entrena", "fitness", "sport", "workout").any { lowerName.contains(it) } -> Icons.Rounded.FitnessCenter
        listOf("hospital", "médico", "medico", "centro", "clinica", "clínica", "salud", "doctor", "clinic", "health").any { lowerName.contains(it) } -> Icons.Rounded.LocalHospital
        listOf("super", "tienda", "compra", "mercadona", "carrefour", "lidl", "aldi", "dia", "shop", "store", "market", "grocery").any { lowerName.contains(it) } -> Icons.Rounded.ShoppingCart
        listOf("parque", "plaza", "jardín", "jardin", "park", "square", "garden").any { lowerName.contains(it) } -> Icons.Rounded.Park
        else -> Icons.Rounded.Place
    }
}
