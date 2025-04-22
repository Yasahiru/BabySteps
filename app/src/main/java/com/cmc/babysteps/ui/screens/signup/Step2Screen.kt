package com.cmc.babysteps.ui.screens.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.cmc.babysteps.data.viewmodel.SignUpViewModel

@Composable
fun Step2Screen(context:Context, viewModel: SignUpViewModel, onNext: () -> Unit, onPrevious: () -> Unit) {

    val datePattern = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = viewModel.gender.value,
            onValueChange = { viewModel.gender.value = it },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.pregnancyDate.value,
            onValueChange = { newValue ->
                viewModel.pregnancyDate.value = newValue
                if (!datePattern.matches(newValue)) {
                    Toast.makeText(
                        context,
                        "Invalid date format. Please use DD/MM/YYYY",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            label = { Text("Pregnancy Date (Format : DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.currentPregnancyWeek.value,
            onValueChange = {
                if (it.toIntOrNull() in 1..40 || it.isEmpty()) {
                    viewModel.currentPregnancyWeek.value = it
                }
            },
            label = { Text("Current Pregnancy Week") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onPrevious) { Text("Previous") }
            Button(onClick = onNext) { Text("Next") }
        }
    }
}