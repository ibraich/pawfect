package com.example.pawfect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Option {

    private String text;
    private List<Personality> personalities;

    // Default constructor for JSON parsing
    public Option() {
    }

    // Parameterized constructor
    public Option(String text, List<Personality> personalities) {
        this.text = text;
        this.personalities = personalities;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Personality> getPersonalities() {
        return personalities;
    }

    public void setPersonalities(List<Personality> personalities) {
        this.personalities = personalities;
    }

}
