package com.cmc.babysteps.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShowWeekBottomSheet(onWeekSelected: (Int) -> Unit) {
    LazyColumn {
        items(40) { index ->
            val week = index + 1
            Button(
                onClick = { onWeekSelected(week) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "Week $week")
            }
        }
    }
}
