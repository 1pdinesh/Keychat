package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageActivity extends AppCompatActivity {

    private TextView User_mobile;
    private EditText inputMessage;
    private ImageButton camera;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        User_mobile = findViewById(R.id.User_mobile);
        inputMessage = findViewById(R.id.inputMessage);
        camera = findViewById(R.id.camera);

        User_mobile.setText("To: " + getIntent().getStringExtra("name"));

    }
}
