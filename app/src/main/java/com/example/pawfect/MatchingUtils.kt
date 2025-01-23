package com.example.pawfect

import kotlin.math.abs

// Extension function to safely convert a String to an Int
fun String.toSafeInt(defaultValue: Int = 0): Int {
    return this.toIntOrNull() ?: defaultValue
}

// Function to calculate the match score
fun calculateMatchScore(
    currentUser: UserFetch,
    potentialMatch: UserFetch,
    weightDogAge: Double = 0.5, // Weight for dog age similarity
    weightOwnerAge: Double = 0.5 // Weight for owner age similarity
): Double {
    val currentDogAge = currentUser.dogAge.toSafeInt()
    val matchDogAge = potentialMatch.dogAge.toSafeInt()

    val currentOwnerAge = currentUser.ownerAge.toSafeInt()
    val matchOwnerAge = potentialMatch.ownerAge.toSafeInt()

    val dogAgeScore = 1.0 / (1 + abs(currentDogAge - matchDogAge))
    val ownerAgeScore = 1.0 / (1 + abs(currentOwnerAge - matchOwnerAge))

    return (dogAgeScore * weightDogAge) + (ownerAgeScore * weightOwnerAge)
}

// Function to filter and sort matches
fun filterAndSortMatches(
    currentUser: UserFetch,
    candidates: List<UserFetch>
): List<Pair<UserFetch, Double>> {
    return candidates
        .filter { it.location == currentUser.location } // Filter by location
        .map { candidate -> candidate to calculateMatchScore(currentUser, candidate) }
        .sortedByDescending { it.second } // Sort by match score
}
