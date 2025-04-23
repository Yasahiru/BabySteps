package com.cmc.babysteps.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmc.babysteps.data.repository.SignInRepository
import com.cmc.babysteps.data.repository.SignUpRepository
import com.cmc.babysteps.data.viewmodel.SignInViewModel
import com.cmc.babysteps.data.viewmodel.SignUpViewModel
import com.cmc.babysteps.ui.screens.home.OnboardingScreen
import com.cmc.babysteps.ui.screens.signin.SignInScreen
import com.cmc.babysteps.ui.screens.signup.SignUpScreen
import com.cmc.babysteps.ui.theme.BabyStepsTheme
import com.cmc.babysteps.utils.FirebaseConfig
import androidx.compose.ui.platform.LocalContext
import com.cmc.babysteps.classes.Screen
import com.cmc.babysteps.data.repository.CalendarRepository
import com.cmc.babysteps.data.viewmodel.CalendarViewModel
import com.cmc.babysteps.ui.screens.MainScreen
import java.time.LocalDate
import com.cmc.babysteps.data.local.AppDatabase

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseConfig.initialize(this)

        // Request notification permissions if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_PERMISSION
                )
            }
        }

        setContent {
            BabyStepsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Initialize repositories and view models
                    val signUpRepository = SignUpRepository(FirebaseConfig.auth, FirebaseConfig.firestore)
                    val signInRepository = SignInRepository(FirebaseConfig.auth)
                    val signUpViewModel = SignUpViewModel(signUpRepository)
                    val signInViewModel = SignInViewModel(signInRepository)

                    // Create AppDatabase instance to get CalendarDao
                    val database = AppDatabase.getInstance(applicationContext)
                    // Initialize CalendarRepository with DAO
                    val calendarRepository = CalendarRepository(database.calendar())
                    // Create CalendarViewModel with repository instance
                    val calendarViewModel = CalendarViewModel(repository = calendarRepository)

                    // Authentication flow
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            // Check if user is logged in and navigate accordingly
                            val currentUser = FirebaseConfig.auth.currentUser
                            if (currentUser != null) {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                        composable("onboarding") {
                            OnboardingScreen {
                                navController.navigate("sign_in") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        }
                        composable("sign_up") {
                            SignUpScreen(
                                LocalContext.current,
                                viewModel = signUpViewModel,
                                navController = navController
                            )
                        }
                        composable("sign_in") {
                            SignInScreen(
                                navController = navController,
                                viewModel = signInViewModel
                            ) {
                                // On successful login, navigate to main screen
                                navController.navigate("main") {
                                    popUpTo("sign_in") { inclusive = true }
                                }
                            }
                        }
                        // MainScreen with bottom navigation
                        composable("main") {
                            MainScreen(
                                calendarViewModel = calendarViewModel,
                                onCalendarDatesRequested = {
                                    // Return default date range
                                    val today = LocalDate.now()
                                    Pair(today.minusDays(15), today.plusDays(15))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Handle permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Permission de notifications accordée", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission de notifications refusée", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}