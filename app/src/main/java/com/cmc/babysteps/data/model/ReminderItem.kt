package com.cmc.babysteps.data.model


data class ReminderItem(
    val id: String = "", // ID unique du document Firestore
    val label: String = "",
    val date: String = "",
    val time: String = "",
    val userId: String = "" // ID de l'utilisateur pour filtrer les rappels par utilisateur
) {
    // Constructeur secondaire sans ID pour la création de nouveaux rappels
    constructor(label: String, date: String, time: String, userId: String) :
            this("", label, date, time, userId)
}