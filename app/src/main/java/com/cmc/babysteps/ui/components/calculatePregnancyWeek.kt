package com.cmc.babysteps.ui.components

import java.text.SimpleDateFormat
import java.util.*

fun calculatePregnancyWeek(signUpDate: String): Int {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val signUpDateObj = sdf.parse(signUpDate) ?: return 0

    val currentDate = Calendar.getInstance().time
    val diffInMillis = currentDate.time - signUpDateObj.time
    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

    val weeks = (diffInDays / 7).toInt() + 1
    return weeks
}
