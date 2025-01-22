package com.example.pawfect;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Gemini {

    private GenerativeModelFutures getModel() {
        String api_key = "AIzaSyDm27cC05NwVaRfZXCvfam1Ci65h80iVos";
        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.LOW_AND_ABOVE);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        GenerationConfig generationConfig = configBuilder.build();

        try {
            GenerativeModel gm = new GenerativeModel(
                    "gemini-1.5-pro",           // Model name
                    api_key,                // API key
                    generationConfig,       // Configuration settings
                    Collections.singletonList(harassmentSafety) // Safety settings
            );

            Log.d(TAG, "Gemini model initialized successfully.");
            return GenerativeModelFutures.from(gm);

        } catch (Exception e) {
            // Catch any exception during initialization or interaction with the model
            Log.e(TAG, "Error initializing or using Gemini model: ", e);
        }


        return null;
    }

    public void getResponse(String query, ResponseCallback callback) {
        GenerativeModelFutures model = getModel();

        Content content = new Content.Builder().addText(query).build();
        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                Log.d(TAG, "Response onSuccess: " + result.getText());

                String resultText = result.getText();
                callback.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Response onFailure.");

                throwable.printStackTrace();
                callback.onError(throwable);
            }
        }, executor);

    }

    public void getResponseForImages(String query, List<Bitmap> imageFiles, ResponseCallback callback) {
        GenerativeModelFutures model = getModel();

        Content.Builder contentBuilder = new Content.Builder().addText(query);

        for (Bitmap file : imageFiles) {
            if (file != null) {
                contentBuilder.addImage(file);
            }
        }

        Content content = contentBuilder.build();
        Executor executor = Runnable::run;

        // Generate response
        assert model != null;
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                Log.d(TAG, "Response onSuccess: " + result.getText());

                String resultText = result.getText();
                callback.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Response onFailure.");

                throwable.printStackTrace();
                callback.onError(throwable);
            }
        }, executor);
    }

}
