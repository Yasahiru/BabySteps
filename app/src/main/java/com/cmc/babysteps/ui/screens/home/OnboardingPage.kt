package com.cmc.babysteps.ui.screens.home

import androidx.annotation.DrawableRes

data class OnboardingPage(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
)