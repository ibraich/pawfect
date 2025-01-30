package com.example.pawfect

import com.google.firebase.firestore.GeoPoint

class User(
    val id: String = "",
    val dogProfileImage: String = "",
    val dogName: String = "",
    val dogBreed: String = "",
    val statusText: String = "",
    val userInfo: String = "",
    val ownerName: String = "",
    val ownerAge: String = "",
    val dogPersonality: String = "",
    val location: GeoPoint = GeoPoint(49.9327659, 11.5687332)
)
