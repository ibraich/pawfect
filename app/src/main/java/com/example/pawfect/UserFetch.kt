package com.example.pawfect

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class UserFetch(
    val id: String = "",
    val ownerName: String = "",
    val addInfo: String = "",
    val dogBreed: String = "",
    val ownerAge: String = "",
    val userInfo: String = "",
    val userName: String = "",
    val dogName: String = "",
    val dogAge: String = "",
    val location: String = "",
    val dogPersonality: String = "",
    val dogProfileImage: String = "",
    val userStatus: String = "",
    val offspringImageUrl: String = ""
){
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "")
}