package com.example.pawfect;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {

    private int id;
    private String question;
    private String category;
    private List<Option> options;

    private static List<Question> listOfQuestions = new ArrayList<>();

    public Question() {
    }

    public Question(int id, String question, String category, List<Option> options) {
        this.id = id;
        this.question = question;
        this.category = category;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    // Load questions from JSON file during initialization
    public static void loadQuestions(Context context) {
        if (listOfQuestions.isEmpty()) {
            try {
                InputStreamReader reader = new InputStreamReader(context.getAssets().open("questions.json"));
                JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                JsonArray questionsArray = jsonObject.getAsJsonArray("questions");
                Type listType = new TypeToken<List<Question>>() {}.getType();
                listOfQuestions = new Gson().fromJson(questionsArray, listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Retrieve all loaded questions
    public static List<Question> getListOfQuestions() {
        return listOfQuestions;
    }

    public static Map<String, List<Question>> getQuestionsByCategory() {
        Map<String, List<Question>> categorizedQuestions = new HashMap<>();
        for (Question question : listOfQuestions) {
            categorizedQuestions
                    .computeIfAbsent(question.category, k -> new ArrayList<>())
                    .add(question);
        }
        return categorizedQuestions;
    }

}
