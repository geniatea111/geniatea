package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme

@Composable
fun AISettingsRoot(
    viewModel: AISettingsViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AISettings(
        state = state,
        onAction = { action ->
            when (action) {
                is AISettingsAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNavIconPressed = { viewModel.onAction(AISettingsAction.OnBackPressed) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISettings(
    state: AISettingsState,
    onAction: (AISettingsAction) -> Unit,
    onNavIconPressed: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.ia_settings),
                onNavIconPressed = { onNavIconPressed() }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 20.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AISettingsHeader(
                    selectedSource = state.avatarSource,
                    onSourceChange = { onAction(AISettingsAction.OnAvatarSourceChange(it)) }
                )

                ClearLanguageToggle(
                    isChecked = state.isClearLanguage,
                    onToggle = { onAction(AISettingsAction.OnClearLanguageToggle(it)) }
                )

                ResponseStyleSlider(
                    value = state.responseStyle,
                    onValueChange = { onAction(AISettingsAction.OnResponseStyleChange(it)) }
                )

                FontSizeSelector(
                    selectedSize = state.fontSize,
                    onSizeChange = { onAction(AISettingsAction.OnFontSizeChange(it)) }
                )

                Button(
                    onClick = { onAction(AISettingsAction.OnSavePressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8AB4F8) // Light Blue from design
                    )
                ) {
                    Text(
                        text = "Guardar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
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
        AISettings(
            state = AISettingsState(),
            onAction = {},
            onNavIconPressed = {}
        )
    }
}

@Composable
fun AISettingsHeader(
    selectedSource: AvatarSource,
    onSourceChange: (AvatarSource) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE8F0FE)) // Light blue background
    ) {
        // Illustration (Placeholder for now, creating a mimic)
        Image(
            painter = painterResource(id = R.drawable.geniv2avatarsettings), // Using geni as placeholder
            contentDescription = "Avatar Preview",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        // Toggle Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SelectableButton(
                text = "Geni",
                isSelected = selectedSource == AvatarSource.GENI,
                onClick = { onSourceChange(AvatarSource.GENI) },
                modifier = Modifier.weight(1f)
            )
            SelectableButton(
                text = "Foto de la galería",
                isSelected = selectedSource == AvatarSource.GALLERY,
                onClick = { onSourceChange(AvatarSource.GALLERY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SelectableButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color(0xFFD2E3FC) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ClearLanguageToggle(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Lenguaje claro",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Help",
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp),
                tint = Color.Gray
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5F6368)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseStyleSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Estilo de respuesta",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Help",
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp),
                tint = Color.Gray
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..2f,
            steps = 1,
            colors = SliderDefaults.colors(
                activeTrackColor = Color(0xFF333333),
                inactiveTrackColor = Color.White
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFF333333))
                        .padding(4.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White)
                )
            },
            track = { sliderState ->
                val activeColor = Color(0xFF333333)
                val inactiveColor = Color.White
                val trackHeight = 4.dp

                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                ) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2

                    // Calculate active width based on slider value (0f to 2f, so we normalize)
                    // sliderState.value is the current value. valueRange is 0..2
                    // But sliderState has .valueRange (if we cast? No, simpler to use logic if accessible)
                    // Actually SliderState (M3) has `value` and `valueRange`.
                    // But wait, the standard M3 Slider `track` lambda passes `SliderState`.
                    // We can use sliderState.value directly.
                    
                    val valueRange = sliderState.valueRange
                    val fraction = (sliderState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                    val activeWidth = width * fraction

                    // Inactive Track
                    drawLine(
                        color = inactiveColor,
                        start = Offset(activeWidth, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Active Track
                    drawLine(
                        color = activeColor,
                        start = Offset(0f, centerY),
                        end = Offset(activeWidth, centerY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Start Cap (Active Color)
                    drawCircle(
                        color = activeColor,
                        radius = trackHeight.toPx() * 2f, // Slightly larger to match design? Or just same/similar
                        center = Offset(0f, centerY)
                    )

                    // End Cap (Inactive Color)
                    drawCircle(
                        color = inactiveColor,
                        radius = trackHeight.toPx() * 2f,
                        center = Offset(width, centerY)
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Conciso", fontSize = 16.sp, color = Color.Gray)
            Text(text = "Estándar", fontSize = 16.sp, color = Color.Gray)
            Text(text = "Extenso", fontSize = 16.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FontSizeSelector(
    selectedSize: Int,
    onSizeChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Tamaño de la letra",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensure all children have the same height
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FontSizeOption(label = "Aa", size = 14, isSelected = selectedSize == 0, onClick = { onSizeChange(0) }, modifier = Modifier.weight(1f).fillMaxHeight())
            FontSizeOption(label = "Aa", size = 18, isSelected = selectedSize == 1, onClick = { onSizeChange(1) }, modifier = Modifier.weight(1f).fillMaxHeight())
            FontSizeOption(label = "Aa", size = 24, isSelected = selectedSize == 2, onClick = { onSizeChange(2) }, modifier = Modifier.weight(1f).fillMaxHeight())
        }
        
        Text(
            text = "Esta es una frase de prueba para comprobar cómo se ve el tamaño de la letra en el chat.",
            fontSize = when(selectedSize) {
                0 -> 14.sp
                1 -> 16.sp // Estándar app
                2 -> 20.sp
                else -> 16.sp
            },
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp),
            lineHeight = when(selectedSize) {
                0 -> 18.sp
                1 -> 24.sp
                2 -> 28.sp
                else -> 24.sp
            }
        )
    }
}

@Composable
fun FontSizeOption(
    label: String,
    size: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(if (isSelected) Color(0xFFE8F0FE) else Color.Transparent)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = size.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}