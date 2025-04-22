package com.cmc.babysteps.data.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.babysteps.data.model.WeekData
import com.cmc.babysteps.ui.screens.home.getDeviceLanguage
import com.cmc.babysteps.utils.FirebaseConfig
import com.cmc.babysteps.utils.RetrofitInstance
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _currentPregnancyWeek = mutableStateOf<String?>(null)
    val currentPregnancyWeek: State<String?> = _currentPregnancyWeek

    private val _weekData = mutableStateOf<WeekData?>(null)
    val weekData: State<WeekData?> = _weekData

    private val _datePregnancy = mutableStateOf<String?>(null)
    val datePregnancy: State<String?> = _datePregnancy

    init {
        fetchCurrentPregnancyWeek()
    }

    private fun fetchCurrentPregnancyWeek() {
        val userId = FirebaseConfig.getCurrentUserId()
        val userDoc = FirebaseConfig.firestore.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            if (document != null) {

                val pregnancyWeek = document.getString("currentPregnancyWeek")
                val pregnancyDate = document.getString("pregnancyDate")

                if (pregnancyWeek != null) {
                    _currentPregnancyWeek.value = pregnancyWeek

                    val pregnancyWeekInt = pregnancyWeek.toIntOrNull()
                    if (pregnancyWeekInt != null) {
                        val lang = getDeviceLanguage()
                        fetchWeekData(lang, pregnancyWeekInt)
                    } else {
                        Log.e("data", "Invalid pregnancy week value: $pregnancyWeek")
                    }
                } else {
                    Log.e("data", "Pregnancy Week is missing in Firestore document.")
                }

                if (pregnancyDate != null) {
                    _datePregnancy.value = pregnancyDate
                } else {
                    Log.e("data", "Pregnancy Date is missing in Firestore document.")
                }
            } else {
                Log.e("data", "No such document found")
            }
        }.addOnFailureListener { e ->
            Log.e("data", "Error getting document: ${e.message}")
        }
    }

    fun fetchWeekData(lang: String, week: Int) {
        viewModelScope.launch {
            try {
                val data = RetrofitInstance.api.getWeekData(lang, week)
                _weekData.value = data
            } catch (e: Exception) {
                Log.e("data", "Error: ${e.message}")
            }
        }
    }
}

