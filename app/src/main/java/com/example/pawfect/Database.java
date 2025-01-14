package com.example.pawfect;

import com.example.appinterface.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static HashMap<Integer, User> allUsers = new HashMap<>();

    static {
        allUsers.put(0, new User(
                0,
                R.drawable.frenchbuldog,
                "Appa",
                "French Bulldog",
                "Such nice weather to go for a walk!",
                "I love avatar the last air, bender and sticks. In that order",
                "Richard",
                24)
        );
        allUsers.put(1, new User(
                1,
                R.drawable.chihuahua,
                "Anakin",
                "Chihuahua",
                "Such nice weather to go for a walk!",
                "Friendly and loves cuddles!",
                "Laura",
                20)
        );
        allUsers.put(2, new User(
                2,
                R.drawable.griffon,
                "Chappi",
                "Griffon",
                "Ready to play fetch!",
                "Energetic and playful.",
                "Bob",
                23
                )
        );
        allUsers.put(3, new User(
                        3,
                        R.drawable.pug,
                        "Mumu",
                        "Pug",
                        "Let's go for a run!",
                        "Loves to explore new places.",
                        "Katharine",
                        25
                )
        );
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    public static User getUserById(Integer id) {
        return allUsers.get(id);
    }

    public static HashMap<Integer, List<Integer>> friendLists = new HashMap<>();

    static {
        friendLists.put(0, Arrays.asList(1, 2, 3));
    }

    public static List<Integer> getUserFriends(Integer id) {
        return friendLists.get(id);
    }

    public static List<Question> listOfQuestions = Question.getListOfQuestions();

    public static Map<String, List<Question>> getCategorizedQuestions() {
        return Question.getQuestionsByCategory();
    }

    public static HashMap<Integer, List<Message>> usersMessages = new HashMap<>();
    public static List<Message> messages = new ArrayList<>();

    static {
        messages.add(new Message("12:01", "Hello! Nice to meet you!"));
        messages.add(new Message("12:02", "Hi! Nice to meet you too!"));
        messages.add(new Message("12:03", "Do we have any plans?"));
        messages.add(new Message("12:04", "Let me think"));

        usersMessages.put(1, messages);
    }
}