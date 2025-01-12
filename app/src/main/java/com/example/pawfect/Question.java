package com.example.pawfect;

import java.util.ArrayList;
import java.util.List;

public class Question {
    String question;
    List<String> answers = new ArrayList<>();

    public Question(String question, String ans1, String ans2, String ans3) {
        this.question = question;
        this.answers.add(ans1);
        this.answers.add(ans2);
        this.answers.add(ans3);
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
