package com.cmc.babysteps.ui.screens.signup

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.cmc.babysteps.data.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    context:Context,
    viewModel: SignUpViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    when (currentStep) {
        1 -> Step1Screen(
            viewModel = viewModel,
            onNext = { viewModel.nextStep() }
        )
        2 -> Step2Screen(
            context,
            viewModel = viewModel,
            onNext = { viewModel.nextStep() },
            onPrevious = { viewModel.previousStep() }
        )
        3 -> Step3Screen(
            viewModel = viewModel,
            onComplete = {
                coroutineScope.launch {
                    val success = viewModel.completeSignUp()
                    if (success) {
                        navController.navigate("sign_in")
                    } else {
                        // handle err message
                    }
                }
            },
            onPrevious = { viewModel.previousStep() }
        )
    }
}

