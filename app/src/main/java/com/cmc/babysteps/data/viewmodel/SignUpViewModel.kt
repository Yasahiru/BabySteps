package com.cmc.babysteps.data.viewmodel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cmc.babysteps.data.repository.SignUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignUpViewModel(private val repository: SignUpRepository) : ViewModel() {

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> get() = _currentStep

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var gender = mutableStateOf("")
    var pregnancyDate = mutableStateOf("")
    var currentPregnancyWeek = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var rememberMe = mutableStateOf(false)

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value++
        }
    }

    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value--
        }
    }

    suspend fun completeSignUp(): Boolean {
        if (password.value != confirmPassword.value) return false

        val signUpSuccess = repository.signUpWithEmailAndPassword(email.value, password.value)

        if (signUpSuccess) {
            val userData = mapOf(
                "firstName" to firstName.value,
                "lastName" to lastName.value,
                "phoneNumber" to phoneNumber.value,
                "gender" to gender.value,
                "pregnancyDate" to pregnancyDate.value,
                "currentPregnancyWeek" to currentPregnancyWeek.value,
                "email" to email.value
            )
            return repository.saveUserData(userData)
        }
        return false
    }
}