package com.example.compose.geniatea.presentation.userManagementSection.prelogin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.LogoAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.theme.subtitleApp

@Composable
fun PreloginRoot(
    viewModel: PreloginViewModel,
    onBackPressed: () -> Unit,
) {
    PreloginScreen(
        onAction = { action ->
            when (action) {
                is PreloginAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreloginScreen(
    onAction: (PreloginAction) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LogoAppBar(
                onNavIconPressed = { onAction(PreloginAction.OnBackPressed) },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Content(onAction)
            }
        }
    }
}

@Composable
fun Content(onAction: (PreloginAction) -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.deco2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.geniprelogin),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 30.dp, top = 40.dp)
                    .height(260.dp)
                    .fillMaxWidth()
            )

            TwoFontText()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Button(
                onClick = { onAction(PreloginAction.OnLoginPressed) },
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }

            OutlinedButton(
                onClick = { onAction(PreloginAction.OnRegisterPressed) },
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
                colors = outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.crear_cuenta),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }

            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.letra_pequena_prelogin1)+ " ")
                    withLink(
                            LinkAnnotation.Clickable(
                                tag = "Terminos",
                                styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
                            ){
                                onAction(PreloginAction.OnTermsOfServiceClicked)
                            }) {
                        append(stringResource(R.string.letra_pequena_prelogin2))
                    }
                    append(" " + stringResource(R.string.letra_pequena_prelogin3)+ " " )
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "Privacidad",
                            styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
                        ){
                            onAction(PreloginAction.OnPrivacyPolicyClicked)
                        }) {
                        append(stringResource(R.string.letra_pequena_prelogin4))
                    }
                    append(" " + stringResource(R.string.letra_pequena_prelogin5))
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }

    }
}


@Composable
fun TwoFontText() {
    val font1 = FontFamily(Font(R.font.dt_getai))
    val font2 = FontFamily(Font(R.font.wallop_regular))

    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontFamily = font2, fontSize = 25.sp)) {
            append("Tu ")
        }
        withStyle(style = SpanStyle(fontFamily = font1, fontSize = 25.sp)) {
            append("aliado inteligente ")
        }
        withStyle(style = SpanStyle(fontFamily = font2, fontSize = 25.sp)) {
            append("para comprender, acompañar y avanzar juntos en el ")
        }
        withStyle(style = SpanStyle(fontFamily = font1, fontSize = 25.sp)) {
            append("TEA")
        }
    }

    BasicText(
        text = text,
        modifier = Modifier.padding(top = 20.dp),
        style = TextStyle(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview
@Composable
fun PreloginPreview() {
    GenIATEATheme {
        PreloginScreen(
            onAction = {}
        )
    }
}

@Preview
@Composable
fun PreloginPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        PreloginScreen(
            onAction = {}
        )
    }
}