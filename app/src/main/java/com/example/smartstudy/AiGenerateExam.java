package com.example.smartstudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartstudy.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiGenerateExam extends AppCompatActivity {

    private static final String OPENAI_API_KEY = "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";
    EditText topicInput, languageInput;
    Button startExam;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_generate_exam);
        topicInput = findViewById(R.id.topicInput);
        languageInput = findViewById(R.id.languageInput);
        startExam = findViewById(R.id.startExamBtn);
        setListeners();
    }

    private void setListeners() {
        startExam.setOnClickListener(v -> {
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
                JSONArray functionArray = new JSONArray();
                JSONObject functionObj = new JSONObject();
                functionObj.put("name", "getExamQuestionsAndAnswers");
                functionObj.put("description", "Get the exam questions, the 4 possible anwers a,b,c,d and which of them are the correct ones");
                JSONObject parameters = new JSONObject();
                parameters.put("type", "object");
                JSONObject properties = new JSONObject();
                JSONObject questions = new JSONObject();
                questions.put("type", "string[]");
                questions.put("description", "A List of the exam questions");
                properties.put("questions", questions);
                JSONObject answersA = new JSONObject();
                answersA.put("type", "string[]");
                answersA.put("description", "A List of the A answer possibilities");
                properties.put("answersA", answersA);
                JSONObject answersB = new JSONObject();
                answersB.put("type", "string[]");
                answersB.put("description", "A List of the B answer possibilities");
                properties.put("answersB", answersB);
                JSONObject answersC = new JSONObject();
                answersC.put("type", "string[]");
                answersC.put("description", "A List of the C answer possibilities");
                properties.put("answersC", answersC);
                JSONObject answersD = new JSONObject();
                answersD.put("type", "string[]");
                answersD.put("description", "A List of the D answer possibilities");
                properties.put("answersD", answersD);
                JSONObject correctAnswers = new JSONObject();
                correctAnswers.put("type", "char[]");
                correctAnswers.put("description", "A List of the correct Answers. Select a char a or b or c or d");
                properties.put("correctAnswers", correctAnswers);
                parameters.put("properties", properties);
                functionObj.put("parameters", parameters);
                functionArray.put(functionObj);
                jsonObject.put("functions", functionArray);
                JSONObject function_name = new JSONObject();
                function_name.put("name", "getExamQuestionsAndAnswers");
                jsonObject.put("function_call", function_name);
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
                        System.out.println(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = null;
                                jsonArray = jsonObject.getJSONArray("choices");
//                                String result = jsonArray.getJSONObject(0)
//                                        .getJSONObject(Constants.KEY_PROMPT)
//                                        .getString("content");
                                JSONObject responseMessage = jsonArray.getJSONObject(0)
                                        .getJSONObject(Constants.KEY_PROMPT);
                                JSONObject arguments = responseMessage.getJSONObject("function_call").getJSONObject("arguments");
                                ArrayList<String> questions = (ArrayList<String>) arguments.get("questions");
                                ArrayList<String> answersA = (ArrayList<String>) arguments.get("answersA");
                                ArrayList<String> answersB = (ArrayList<String>) arguments.get("answersB");
                                ArrayList<String> answersC = (ArrayList<String>) arguments.get("answersC");
                                ArrayList<String> answersD = (ArrayList<String>) arguments.get("answersD");
                                ArrayList<CorrectAnswer> correctAnswers = (ArrayList<CorrectAnswer>) arguments.get("correctAnswers");
                                getExamQuestionsAndAnswers(questions, answersA, answersB, answersC, answersD, correctAnswers);
                            }catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            System.out.println("Unsuccesful");
                        }
                }
            });
        });
    }


    private void getExamQuestionsAndAnswers(ArrayList<String> questions, ArrayList<String> answersA, ArrayList<String> answersB, ArrayList<String> answersC, ArrayList<String> answersD, ArrayList<CorrectAnswer> correctAnswers) {
        Intent intent = new Intent(this, AiExam.class);
        intent.putExtra(Constants.KEY_QUESTIONS, questions);
        intent.putExtra(Constants.KEY_ANSWERSA, answersA);
        intent.putExtra(Constants.KEY_ANSWERSB, answersB);
        intent.putExtra(Constants.KEY_ANSWERSC, answersC);
        intent.putExtra(Constants.KEY_ANSWERSD, answersD);
        intent.putExtra(Constants.KEY_CORRECT_ANSWERS, correctAnswers);
        startActivity(intent);
    }
}