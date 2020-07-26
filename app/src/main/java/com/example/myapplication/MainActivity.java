package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText Email, Password;
    private Button Login;
    private TextView welcome, forgetPassword;
    private Toolbar toolbar;
    private Button userRegistration;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    String name, password;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            //startActivity(new Intent(MainActivity.this, Fingerprint.class));
            // if user alrd logged in, redirect to fingerprint class
            //create biometric class and replace fingerprint class w new class
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome=findViewById(R.id.welcome);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Login = findViewById(R.id.btn_login);
        forgetPassword = findViewById(R.id.forgetPassword);
        userRegistration = findViewById(R.id.btn_register);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Email.getText().toString(), Password.getText().toString());

            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(new Intent(MainActivity.this, RegisterActivity.class));
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String> (welcome,"login");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(a,activityOptions.toBundle());
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed(){//when you enter back press button, it will exit your application
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private Boolean validating(){//it will validate your name and password
        Boolean result = false;

        name = Email.getText().toString();
        password = Password.getText().toString();


        if(name.isEmpty())
        {
            Email.setError("Email is Empty!");
        }
        else if(password.isEmpty())
        {
            Password.setError("Password is Empty!");
        }
        else{
            result = true;
        }

        return result;
    }

    private void validate(String userName, String userPassword) {//it will check ur username and password and if it is correct then you can enter to the system

        if(validating()) {
            //Upload data to the database
            String user_email = Email.getText().toString().trim();
            String user_password = Password.getText().toString().trim();



                firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }


    }


