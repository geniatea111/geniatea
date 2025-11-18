package com.example.compose.geniatea.presentation.settingsSection.locationSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView

@Composable
fun LocationRoot(
    viewModel: LocationViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LocationScreen(
        uiState = state,
        onAction = { action ->
            when (action) {
                is LocationAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNavIconPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onAction: (LocationAction) -> Unit,
    uiState: LocationScreenState,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.addresses),
                onNavIconPressed = { onNavIconPressed() },
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues)
                .background(color = Color.Transparent)
                .border(width = 2.dp, color = Color.Transparent),
        ) {
            if( uiState.locations.isEmpty() ) {
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
                            painter = painterResource(id = R.drawable.ic_empty_chat),
                            contentDescription = stringResource(id = R.string.empty_chat),
                            modifier = Modifier.size(80.dp).padding(bottom = 20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.no_messages),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.alpha(0.8f),
                        )
                    }
                }

            }else{
                Locations(
                    locations = uiState.locations,
                    modifier = Modifier.weight(1f),
                    onAction = onAction,
                )
            }

           /* AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                        controller.setZoom(20.0)
                        controller.setCenter(GeoPoint(39.162489945601905, -3.0258693116112356))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // adjust map height
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )*/





            Button(
                onClick = { onAction(LocationAction.OnAddLocation) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 15.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.add_address),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }


        }
    }
}


@Composable
fun Locations(locations: List<Location>, modifier: Modifier = Modifier, onAction: (LocationAction) -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (loc in locations) {
            LocationItem(location = loc, modifier = Modifier.fillMaxWidth(), onAction = onAction)
        }
    }
}

@Composable
fun LocationItem(location: Location, modifier: Modifier = Modifier, onAction: (LocationAction) -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)

    ){
        //row with name and two icons ( edit and delete)
        Column{
            Row(
                modifier = Modifier
                    .padding(10.dp)
            ){
                BasicTextField(
                    value = location.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 8.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    ),
                    decorationBox = { innerTextField ->
                        if (location.name.isEmpty()) {
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                OutlinedIconButton(
                    onClick = { /*TODO*/ },
                    border = BorderStroke(0.dp, Color.Transparent),
                    modifier = Modifier.size(36.dp).align(Alignment.CenterVertically),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                OutlinedIconButton(
                    onClick = { /*TODO*/ },
                    border = BorderStroke(0.dp, Color.Transparent),
                    modifier = Modifier.size(36.dp).align(Alignment.CenterVertically),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_trash),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

            }

        }
    }

}

@Preview
@Composable
fun RegisterPreview() {
    GenIATEATheme {
        LocationScreen(
            onAction = {},
            uiState = LocationScreenState(
                initialLocations = listOf(
                    Location(
                        location = "123 Main St, Springfield, IL 62701",
                        name = "Home"
                    ),
                    Location(
                        location = "456 Elm St, Springfield, IL 62701",
                        name = "Work",
                    ),
                    Location(
                        location = "789 Oak St, Springfield, IL 62701",
                        name = "Gym"
                    ),
                )
            ),
            onNavIconPressed = {}
        )
    }
}