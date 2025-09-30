package com.nemanjamicanovic.only_fitness.database.models


data class User(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val subscribers: Int = 0,
    val worksWith: String = "",
    val available: String = "",
    val aboutMe: String = "",
    val profilePicture: String = "",
    val location: String = ""
)
