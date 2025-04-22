package com.cmc.babysteps.data.viewmodel

import androidx.lifecycle.ViewModel
import com.cmc.babysteps.data.repository.SignInRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel(private val repository: SignInRepository) : ViewModel() {

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess

    suspend fun signIn(email: String, password: String): Boolean {
        val success = repository.signInWithEmailAndPassword(email, password)
        _signInSuccess.value = success
        return success
    }
}
