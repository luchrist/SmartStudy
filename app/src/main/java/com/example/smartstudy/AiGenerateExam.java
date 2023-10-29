package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartstudy.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiGenerateExam extends BaseActivity {

    private static final String OPENAI_API_KEY = "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";
    EditText topicInput, languageInput;
    Button startExam;
    ProgressBar progressBar;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_generate_exam);
        topicInput = findViewById(R.id.topicInput);
        languageInput = findViewById(R.id.languageInput);
        startExam = findViewById(R.id.startExamBtn);
        progressBar = findViewById(R.id.apiProgress);
        setListeners();
        builder.connectTimeout(120, TimeUnit.SECONDS);
        builder.readTimeout(120, TimeUnit.SECONDS);
        builder.writeTimeout(120, TimeUnit.SECONDS);
        client = builder.build();
    }

    private void setListeners() {
        startExam.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            String topic = topicInput.getText().toString().trim();
            String language = languageInput.getText().toString().trim();
            String prompt = String.format("Create an multiple choice Exam about %s with 4 possible answers a,b,c,d per question and provide the right answer." +
                    " The exam should be in the language %s", topic, language);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("model", "gpt-3.5-turbo");
                JSONArray promptArray = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("role", "user");
                obj.put("content", prompt);
                promptArray.put(obj);
                jsonObject.put(Constants.KEY_PROMPT ,promptArray);
            }catch (JSONException e){
                throw new RuntimeException(e);
            }

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AiGenerateExam.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    });
                    startActivity(new Intent(AiGenerateExam.this, AiGenerateExam.class));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                runOnUiThread(() -> {
                                    Toast.makeText(AiGenerateExam.this, "Successful", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                });
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONArray jsonArray = jsonObject.getJSONArray("choices");
                                JSONObject message = jsonArray.getJSONObject(0);
                                JSONObject message1 = message.getJSONObject("message");
                                String content = message1.getString("content");

                                Intent intent = new Intent(AiGenerateExam.this, AiExam.class);
                                intent.putExtra(Constants.KEY_RESPONSE, content);
                                startActivity(intent);
                            }catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(AiGenerateExam.this, "Response is unsuccessful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                            });
                        }
                }
            });
        });
    }
}