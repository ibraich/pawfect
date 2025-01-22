package com.example.pawfect;

import java.util.Arrays;
import java.util.List;

public enum Personality {
    ADVENTUROUS_EXPLORER,
    BRAINY_STRATEGIST,
    CHILL_COUCH_POTATO,
    INDEPENDENT_LONE_WOLF,
    PLAYFUL_FETCHER,
    PROTECTIVE_GUARDIAN,
    SOCIAL_BUTTERFLY;

    public static List<Personality> getAllPersonalities() {
        return Arrays.asList(values());
    }

}
