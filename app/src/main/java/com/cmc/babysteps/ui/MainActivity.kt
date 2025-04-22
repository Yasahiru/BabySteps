package com.cmc.babysteps.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmc.babysteps.data.repository.SignInRepository
import com.cmc.babysteps.data.repository.SignUpRepository
import com.cmc.babysteps.data.viewmodel.SignInViewModel
import com.cmc.babysteps.data.viewmodel.SignUpViewModel
import com.cmc.babysteps.ui.screens.home.HomeScreen
import com.cmc.babysteps.ui.screens.reminder.ReminderScreen
import com.cmc.babysteps.ui.screens.signin.SignInScreen
import com.cmc.babysteps.ui.screens.signup.SignUpScreen
import com.cmc.babysteps.ui.theme.BabyStepsTheme
import com.cmc.babysteps.utils.FirebaseConfig

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseConfig.initialize(this)

        // Demander la permission POST_NOTIFICATIONS si nécessaire
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Demander la permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_PERMISSION
                )
            }
        }

        setContent {
            BabyStepsTheme {
                val navController = rememberNavController()

                val signUpRepository = SignUpRepository(FirebaseConfig.auth, FirebaseConfig.firestore)
                val signInRepository = SignInRepository(FirebaseConfig.auth)

                val signUpViewModel = SignUpViewModel(signUpRepository)
                val signInViewModel = SignInViewModel(signInRepository)

                Surface(color = MaterialTheme.colorScheme.background) {

                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_up") {
                            SignUpScreen(
                                this@MainActivity,
                                viewModel = signUpViewModel,
                                navController = navController
                            )
                        }
                        composable("sign_up") {
                            SignInScreen(navController = navController, viewModel = signInViewModel)
                        }
                        composable("home") {
                            ReminderScreen()
                        }
                    }
                }
            }
        }
    }

    // Gérer la réponse à la demande de permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée
                Toast.makeText(this, "Permission de notifications accordée", Toast.LENGTH_SHORT).show()
            } else {
                // La permission a été refusée
                Toast.makeText(this, "Permission de notifications refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
