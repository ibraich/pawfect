package com.example.pawfect

import android.util.Log
import kotlin.math.abs

fun String.toSafeInt(defaultValue: Int = 0): Int {
    return this.toIntOrNull() ?: defaultValue
}

fun loadPersonalityScores() {
    val firestore = Firebase.firestore

    firestore.collection("Personality").get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val personalityType = document.id // Example: "CHILL_COUCH_POTATO"
                val scores = document.data.mapValues { it.value.toString().toDouble() }
                personalityScoresCache[personalityType] = scores
            }
            Log.d("PersonalityMatch", "Personality scores loaded successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("PersonalityMatch", "Error fetching personality scores", e)
        }
}


fun calculateMatchScore(
    currentUser: UserFetch,
    potentialMatch: UserFetch,
    weightPersonality: Double = 0.4,
    weightDogAge: Double = 0.3,
    weightOwnerAge: Double = 0.3
): Double {
    val currentDogAge = currentUser.dogAge.toSafeInt()
    val matchDogAge = potentialMatch.dogAge.toSafeInt()

    val currentOwnerAge = currentUser.ownerAge.toSafeInt()
    val matchOwnerAge = potentialMatch.ownerAge.toSafeInt()

    val dogAgeScore = 1.0 / (1 + abs(currentDogAge - matchDogAge))
    val ownerAgeScore = 1.0 / (1 + abs(currentOwnerAge - matchOwnerAge))

    // Fetch personality compatibility score from the cache
    val personalityScore = personalityScoresCache[currentUser.dogPersonality]
        ?.get(potentialMatch.dogPersonality) ?: 0.5

    return (dogAgeScore * weightDogAge) +
            (ownerAgeScore * weightOwnerAge) +
            (personalityScore * weightPersonality)
}

// Function to filter and sort matches
fun filterAndSortMatches(
    currentUser: UserFetch,
    candidates: List<UserFetch>
): List<Pair<UserFetch, Double>> {
    return candidates
        .filter { it.location == currentUser.location }
        .map { candidate -> candidate to calculateMatchScore(currentUser, candidate) }
        .sortedByDescending { it.second }
}