package com.example.pawfect;

public class User {
    Integer id;
    Integer profileImage;
    String dogName;

    String dogBreed;
    String statusText;
    String userInfo;
    String ownerName;
    Integer ownerAge;

    public User(Integer id,
                Integer profileImage,
                String dogName,
                String dogBreed,
                String statusText,
                String userInfo,
                String ownerName,
                Integer ownerAge) {
        this.id = id;
        this.profileImage = profileImage;
        this.dogName = dogName;
        this.dogBreed = dogBreed;
        this.statusText = statusText;
        this.userInfo = userInfo;
        this.ownerName = ownerName;
        this.ownerAge = ownerAge;
    }
}
