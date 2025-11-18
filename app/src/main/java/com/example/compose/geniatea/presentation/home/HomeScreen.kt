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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.compose.geniatea.presentation.components.HomeAppBar
import com.example.compose.geniatea.presentation.components.LogoAppBar

@Composable
fun HomeRoot(
    viewModel: HomeViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is HomeAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
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
                onAccountPressed = { onAction(HomeAction.OnAccountPressed) }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->

                Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(top = 0.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                            .paint(
                                painter = painterResource(id = R.drawable.background_home),
                                contentScale = ContentScale.Crop,
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                                .padding(top = 40.dp, bottom = 40.dp)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.saludo,
                                style = titleApp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = stringResource(id = R.string.how_can_we_help_you_today),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 40.dp).fillMaxWidth(),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center

                            )

                            Button(
                                modifier = Modifier
                                    .heightIn(min = 60.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp),
                                onClick ={ onAction(HomeAction.OnChatPressed) },
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

                   // val tasks = listOf("Ir al gimnasio", "Poner una lavadora", "Pagar la suscripción de Netflix", "Comprar comida para el perro", "Llevar el coche al taller")
                   val tasks = listOf<String>()
                    
                    optionsButtons(onAction = onAction, tasks)

                }
                   // BottomNavPanel(onAction)
            }
}
/*
@Composable
fun BoxScope.BottomNavPanel(onAction : (HomeAction) -> Unit = {}) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
    ) {
        //BottomNavPanelWithCutOut(onAction)

        // Floating button positioned over the cutout
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .width(77.dp)
                .height(53.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            onClick = { onAction(HomeAction.OnChatPressed) },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.svg_sparkles2),
                contentDescription = "Floating Action Button",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}*/


@Composable
fun optionsButtons(onAction: (HomeAction) -> Unit, tasks : List<String> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tasks Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .heightIn(max =300.dp)
                    .padding(20.dp)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Button(
                        onClick = {onAction(HomeAction.OnTaskListPressed)},
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(1f)
                            .height(30.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row{
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
                                    .padding(start = 10.dp)
                            )
                        }
                    }

                    //if tasks is not empty show tasks
                    if(tasks.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp), // adjust height as needed
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tasks.size) { index ->
                                task(text = tasks[index])
                            }
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay tareas pendientes \uD83C\uDF89",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = W700,
                            )
                        }

                    }


                }
            }
        }

        // Intention and Rewriter Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onAction(HomeAction.OnJudgePressed) },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer

                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        text = "Intención",
                        textAlign = TextAlign.Start,
                        fontWeight = W700
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                    )
                }
            }

            Button(
                onClick = { onAction(HomeAction.OnFormalizerPressed) },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        text = "Reescribir",
                        textAlign = TextAlign.Start,
                        fontWeight = W700
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
            }

         /*   OutlinedButton(
                onClick = { onAction(HomeAction.OnResourcesPressed) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column{
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_task),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                            .height(130.dp)
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Recursos",
                        textAlign = TextAlign.Center,
                        fontWeight = W400
                    )
                }
            }*/
        }
    }
}

/*
@Composable
fun BoxScope.BottomNavPanelWithCutOut(onAction : (HomeAction) -> Unit = {}) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(70.dp)
            .clip(
                BottomNavBar(
                    dockRadius = with(LocalDensity.current) { 77.dp.toPx() },
                ),
            ) // Apply the custom shape
            .background(MaterialTheme.colorScheme.primaryContainer)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* Handle home click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = { onAction(HomeAction.OnAccountPressed) }) {
                Icon(
                    painter = painterResource(id = R.drawable.account),
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }


        }
    }
}*/

@Composable
fun task(text: String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(15.dp),
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

/*
@Composable
fun buttonOption(text: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    val colorStops = arrayOf(
        0.4f to MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
        1f to MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
    )
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(min = 170.dp, max = 350.dp)
            .heightIn(min = 75.dp)
            .clip(RoundedCornerShape(35.dp))
            .background(brush = Brush.linearGradient(colorStops = colorStops, start = Offset(0f, Float.POSITIVE_INFINITY), end = Offset(Float.POSITIVE_INFINITY, 0f)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.padding(20.dp).align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .height(35.dp)
                    .width(45.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(0.8f))
                    .padding(5.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = W700,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}
@Composable
fun optionsSecondaries(onAction: (HomeAction) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 30.dp, start = 25.dp, end = 25.dp)
    ) {

        buttonSecondaryOption(
            text = "Traductor a pictogramas",
            onClick = { onAction(HomeAction.OnResourcesPressed) }
        )

        buttonSecondaryOption(
            text = "Consultas favoritas",
            onClick = { onAction(HomeAction.OnResourcesPressed) }
        )

        buttonSecondaryOption(
            text = "Recursos",
            onClick = { onAction(HomeAction.OnResourcesPressed) }
        )
    }
}

@Composable
fun buttonSecondaryOption(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp),
        shape = RoundedCornerShape(30.dp),
        border = null,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(1f),
            fontWeight = W700,
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
*/
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