package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    Button button;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        SharedPreferences sp = this.getSharedPreferences("SP", 0);
        if(sp.getString("username", null) != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            button = findViewById(R.id.button);
            username = findViewById(R.id.username);
            DbTimeHelper dbTimeHelper = new DbTimeHelper(this);
            dbTimeHelper.addTimeObject("Monday", 0);
            dbTimeHelper.addTimeObject("Tuesday", 0);
            dbTimeHelper.addTimeObject("Wednesday", 0);
            dbTimeHelper.addTimeObject("Thursday", 0);
            dbTimeHelper.addTimeObject("Friday", 0);
            dbTimeHelper.addTimeObject("Saturday", 0);
            dbTimeHelper.addTimeObject("Sunday", 0);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sp.edit();
                    String name = username.getText().toString();
                    editor.putString("username", name );
                    editor.commit();
                    changeClass();

                }
            });



        }
    }

    void changeClass(){
        Toast.makeText(this, "Lets go!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();

    }
}