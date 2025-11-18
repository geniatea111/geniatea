package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.OnboardingAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.theme.subtitleApp


@Composable
fun OnboardingRoot(
    viewModel: OnboardingViewModel,
    onBackPressed: () -> Unit,
) {

    OnboardingScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                is OnboardingAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        viewModel = viewModel
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onAction: (OnboardingAction) -> Unit,
    viewModel : OnboardingViewModel?,
    state: OnboardingScreenState
) {
    var listItemShown by rememberSaveable { mutableStateOf(0) }
    var selectedOptionPerson by remember { mutableStateOf("") }
    var selectedOptionGoal by remember { mutableStateOf("") }
    var enabledButton by remember { mutableStateOf(false) }
    var selectedImage = state.selectedImage

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            OnboardingAppBar(
                onNavIconPressed = {
                    if(selectedImage != null){
                        viewModel!!.updateImage(null)
                    }else{
                        if (listItemShown == 0)
                            onAction(OnboardingAction.OnBackPressed)
                        else
                            listItemShown--
                    }
                },
                onAction = onAction
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            Image(
                painter = painterResource(id = R.drawable.deco1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.20f),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.deco2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            AnimatedContent(
                targetState = listItemShown,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Forward navigation
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        // Backward navigation
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "OnboardingAnimation"
            ) { page ->
                when (page) {
                    0 -> ContentPerson(onAction, selectedOptionPerson) {
                        enabledButton = true
                        selectedOptionPerson = it
                    }
                    1 -> ContentGoal(onAction, selectedOptionGoal) {
                        enabledButton = true
                        selectedOptionGoal = it
                    }
                    2 -> {
                        ContentGeni(onAction, selectedImage, innerPadding)
                        enabledButton = true
                    }
                    else -> BasicText(text = "Error")
                }
            }

            if(listItemShown != 2){
                OutlinedButton(
                    onClick = { listItemShown++
                        enabledButton = false
                    },
                    enabled = enabledButton,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 35.dp)
                        .heightIn(min = 56.dp)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    colors = outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor =  MaterialTheme.colorScheme.surface,
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.continues),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = W700,
                    )
                }
            }

        }
    }
}

@Composable
fun ContentPerson(onAction: (OnboardingAction) -> Unit, selectedOption : String, onOptionSelected: (String) -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center,
                style = subtitleApp,
                fontSize = 36.sp,
                lineHeight = 45.sp,
                text = stringResource(R.string.titleOnboarding1),
            )

            OptionsPerson(onAction, selectedOption, onOptionSelected)

        }
    }
}


@Composable
fun ContentGoal(onAction: (OnboardingAction) -> Unit, selectedOption : String, onOptionSelected: (String) -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center,
                style = subtitleApp,
                fontSize = 36.sp,
                lineHeight = 45.sp,
                text = stringResource(R.string.titleOnboarding2),
            )

            OptionsGoal(onAction, selectedOption, onOptionSelected)

        }
    }
}

@Composable
fun ContentGeni(onAction: (OnboardingAction) -> Unit, selectedImage : Uri? = null, innerPadding : PaddingValues){
    if(selectedImage == null){
        geni(onAction, innerPadding)
    }else{
        ContentPersonalizedGeni(onAction, selectedImage, innerPadding)
    }
}

@Composable
fun geni(onAction: (OnboardingAction) -> Unit, innerPadding: PaddingValues){

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(bottom = 20.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                text =  buildAnnotatedString {
                    append(stringResource(R.string.titleOnboarding31) + " ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(R.string.titleOnboarding32))
                    }
                    append(stringResource(R.string.titleOnboarding33))
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp, top = 30.dp),
                style = subtitleApp,
                fontSize = 36.sp,
                lineHeight = 45.sp,
            )


            Image(
                painter = painterResource(id = R.drawable.geniv2),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 50.dp)
                    .heightIn(min = 200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )


            Button(
                onClick = { onAction(OnboardingAction.OnContinuePressed) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.continues),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }

            Image(
                painter = painterResource(id = R.drawable.or),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .heightIn(min = 24.dp)
                    .fillMaxWidth(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                contentScale = ContentScale.Fit
            )

            OutlinedButton(
                onClick = { onAction(OnboardingAction.OnGenerateImagePressed) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
                colors = outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.generate_image),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700,
                    textAlign = TextAlign.Center
                )
            }



        }
    }
}



@Composable
fun ContentPersonalizedGeni(onAction: (OnboardingAction) -> Unit, selectedImage : Uri? = null, innerPadding : PaddingValues){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(bottom = 60.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                text = stringResource(R.string.titleOnboarding4),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp, top = 30.dp),
                style = subtitleApp,
                fontSize = 36.sp,
                lineHeight = 45.sp,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 30.dp)
                    .height(250.dp)
            ){
                AsyncImage(
                    model = selectedImage,
                    contentDescription = stringResource(id = R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
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
                    onClick = { onAction(OnboardingAction.OnGenerateImagePressed) },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(50.dp)
                        .align(Alignment.TopEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    contentPadding = PaddingValues(0.dp),
                )
                {
                    Icon(
                        painter = painterResource(id = R.drawable.change),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .heightIn(min = 24.dp)
                            .fillMaxWidth(),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }




            Button(
                onClick = { onAction(OnboardingAction.OnContinuePressed) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.continues),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }

        }
    }
}


@Composable
fun OptionsPerson(onAction: (OnboardingAction) -> Unit, selectedOption : String, onOptionSelected: (String) -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        optionItem(
            text = stringResource(id = R.string.option_adult),
            isSelected = selectedOption == "Adult",
            onClick = {
                onOptionSelected("Adult")
                onAction(OnboardingAction.OnOptionSelectedPerson("Adult"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_student),
            isSelected = selectedOption == "Student",
            onClick = {
                onOptionSelected("Student")
                onAction(OnboardingAction.OnOptionSelectedPerson("Student"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_family),
            isSelected = selectedOption == "Familiar",
            onClick = {
                onOptionSelected("Familiar")
                onAction(OnboardingAction.OnOptionSelectedPerson("Familiar"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_other),
            isSelected = selectedOption == "Otro",
            onClick = {
                onOptionSelected("Otro")
                onAction(OnboardingAction.OnOptionSelectedPerson("Otro"))
            }
        )
    }
}


@Composable
fun OptionsGoal(onAction: (OnboardingAction) -> Unit, selectedOption : String, onOptionSelected: (String) -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        optionItem(
            text = stringResource(id = R.string.option_organize),
            isSelected = selectedOption == "1",
            onClick = {
                onOptionSelected("1")
                onAction(OnboardingAction.OnOptionSelectedGoal("1"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_understand),
            isSelected = selectedOption == "2",
            onClick = {
                onOptionSelected("2")
                onAction(OnboardingAction.OnOptionSelectedGoal("2"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_task),
            isSelected = selectedOption == "3",
            onClick = {
                onOptionSelected("3")
                onAction(OnboardingAction.OnOptionSelectedGoal("3"))
            }
        )
        optionItem(
            text = stringResource(id = R.string.option_other),
            isSelected = selectedOption == "Otro",
            onClick = {
                onOptionSelected("Otro")
                onAction(OnboardingAction.OnOptionSelectedGoal("Otro")) }
        )
    }
}

@Composable
fun optionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 15.dp)
            .heightIn(min = 66.dp)
            .fillMaxWidth(),
        colors = if (isSelected) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = W700
        )
    }
}


@Preview
@Composable
fun PreloginPreview() {
    GenIATEATheme {
        OnboardingScreen(
            state = OnboardingScreenState(),
            onAction = {},
            viewModel = null
        )
    }
}

@Preview
@Composable
fun PreloginPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        OnboardingScreen(
            state = OnboardingScreenState(),
            onAction = {},
            viewModel = null
        )
    }
}