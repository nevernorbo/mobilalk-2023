package com.example.foodoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final int SECRET_KEY = 99;

    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = findViewById(R.id.editTextUserName);
        passwordET = findViewById(R.id.editTextPassword);
    }

    public void login(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}