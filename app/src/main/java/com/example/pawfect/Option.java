package com.example.pawfect;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Option {

    private String text;
    private List<String> personalities;

    // Default constructor for JSON parsing
    public Option() {
    }

    // Parameterized constructor
    public Option(String text, List<String> personalities) {
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

    public List<String> getPersonalities() {
        return personalities;
    }

    public void setPersonalities(List<String> personalities) {
        this.personalities = personalities;
    }

}
