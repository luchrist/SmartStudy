package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.smartstudy.models.User;
import com.example.smartstudy.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
    EditText topicInput, languageInput;
    Button startExam;
    ProgressBar progressBar;
    private Spinner difficultySpinner;
    private AppCompatImageView back;
    private TextView points, progressStatus;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_generate_exam);
        topicInput = findViewById(R.id.topicInput);
        languageInput = findViewById(R.id.languageInput);
        startExam = findViewById(R.id.generateExamBtn);
        progressBar = findViewById(R.id.apiProgress);
        progressStatus = findViewById(R.id.progressStatus);
        back = findViewById(R.id.backNavBtn);
        points = findViewById(R.id.points);
        difficultySpinner = findViewById(R.id.difficultySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
        difficultySpinner.setSelection(1);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        displayCurrentUserPoints();

        setListeners();
        builder.connectTimeout(120, TimeUnit.SECONDS);
        builder.readTimeout(120, TimeUnit.SECONDS);
        builder.writeTimeout(120, TimeUnit.SECONDS);
        client = builder.build();
    }

    private void displayCurrentUserPoints() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(user.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        points.setText(String.valueOf(user.points));
                    }
                });
    }

    private void setListeners() {
        back.setOnClickListener(v -> onBackPressed());

        startExam.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressStatus.setVisibility(TextView.VISIBLE);
            String topic = topicInput.getText().toString().trim();
            String language = languageInput.getText().toString().trim();
            String difficulty = "";
            if(difficultySpinner.getSelectedItemPosition() != 1) {
                difficulty = difficultySpinner.getSelectedItemPosition() == 0 ? "easy" : "difficult";
            }
            String prompt = String.format("Create a %s multiple choice quiz in %s about %s" +
                    " with 4 possible answers a,b,c,d and provide the right answer. " +
                    "Only pick questions with one correct answer.", difficulty, language, topic);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("model", "gpt-3.5-turbo-instruct");
                /*JSONArray promptArray = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("role", "user");
                obj.put("content", prompt);
                promptArray.put(obj);*/
                jsonObject.put(Constants.KEY_PROMPT , prompt);
                jsonObject.put("max_tokens", 1000);
            }catch (JSONException e){
                throw new RuntimeException(e);
            }

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/completions")
                    .header("Authorization", "Bearer " + BuildConfig.OPEN_AI_API_KEY)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AiGenerateExam.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressStatus.setVisibility(TextView.INVISIBLE);
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
                                    progressStatus.setVisibility(TextView.INVISIBLE);
                                });
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONArray jsonArray = jsonObject.getJSONArray("choices");
                                JSONObject message = jsonArray.getJSONObject(0);
                                //JSONObject message1 = message.getJSONObject("message");
                                //String content = message1.getString("content");
                                String content = message.getString("text");

                                Intent intent = new Intent(AiGenerateExam.this, AiExam.class);
                                intent.putExtra(Constants.KEY_RESPONSE, content);
                                intent.putExtra(Constants.KEY_TOPIC, topic);
                                intent.putExtra(Constants.KEY_LANGUAGE, language);
                                startActivity(intent);
                            }catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(AiGenerateExam.this, "Response is unsuccessful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                progressStatus.setVisibility(TextView.INVISIBLE);
                            });
                        }
                }
            });
        });
    }
}