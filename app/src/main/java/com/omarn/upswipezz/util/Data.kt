package com.omarn.upswipezz.util

data class User (
    val uid: String? = "",
    val name: String? = "",
    val age: String? = "",
    val email: String? = "",
    val gender: String? = "",
    val preferredGender: String? = "",
    val imageURL: String? = ""
)

data class Chat(
    val userId: String? = "",
    val chatId: String? = "",
    val otherUserId: String? = "",
    val name: String? = "",
    val imageURL: String? = ""
)

data class Message (
    val sentBy: String? = null,
    val message: String? = null,
    val time: String? = null
)
