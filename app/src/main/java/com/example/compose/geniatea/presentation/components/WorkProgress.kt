package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.titleApp

@Composable
fun WorkProgress() {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    )
    {
        Image(
            painter = painterResource(id = R.drawable.work_progress),
            contentDescription = null,
            modifier = Modifier.padding(top = 40.dp).heightIn(300.dp),
        )
        Text(
            text = "Work in progress",
            style = titleApp,
        )
    }
}

@Preview
@Composable
fun WorkProgressPreview() {
    WorkProgress()
}