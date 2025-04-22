package com.cmc.babysteps.classes

import java.time.LocalDate

sealed class DialogState {
    data object None : DialogState()
    data class Choice(val date: LocalDate) : DialogState()
    data class View(val date: LocalDate) : DialogState()
    data class Add(val date: LocalDate) : DialogState()
}