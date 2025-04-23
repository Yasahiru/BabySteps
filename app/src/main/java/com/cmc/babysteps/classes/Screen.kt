package com.cmc.babysteps.classes

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object SignUp : Screen("sign_up")
    object SignIn : Screen("sign_in")
    object Main : Screen("main")
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Reminder : Screen("reminder")
}