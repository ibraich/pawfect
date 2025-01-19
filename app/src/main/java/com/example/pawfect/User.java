package com.example.pawfect;

import com.google.firebase.firestore.GeoPoint;

public class User {
    Integer id;
    Integer profileImage;
    String dogName;

    String dogBreed;
    String statusText;
    String userInfo;
    String ownerName;
    Integer ownerAge;
    GeoPoint location;

    String dogPersonality;

    public User(Integer id,
                Integer profileImage,
                String dogName,
                String dogBreed,
                String statusText,
                String userInfo,
                String ownerName,
                Integer ownerAge,
                String dogPersonality) {
        this.id = id;
        this.profileImage = profileImage;
        this.dogName = dogName;
        this.dogBreed = dogBreed;
        this.statusText = statusText;
        this.userInfo = userInfo;
        this.ownerName = ownerName;
        this.ownerAge = ownerAge;
        this.dogPersonality = dogPersonality;
        this.location = new GeoPoint(37.7749, -122.4194);
    }
}
