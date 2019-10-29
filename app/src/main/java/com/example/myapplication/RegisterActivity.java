package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText user_name, user_email, user_password, user_confirmPassword, user_mobile;
    Button btn_register;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, reference1;
    String username, email, password, confirmPwd, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name = findViewById(R.id.username);
        user_email = findViewById(R.id.email);
        user_password = findViewById(R.id.password);
        user_confirmPassword = findViewById(R.id.confirmPwd);
        btn_register = findViewById(R.id.btn_register);
        user_mobile = findViewById(R.id.mobile);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegisterActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Boolean validate(){
        Boolean result = false;

        username = user_name.getText().toString();
        email = user_email.getText().toString();
        password = user_password.getText().toString();
        confirmPwd = user_confirmPassword.getText().toString();
        mobile = user_mobile.getText().toString();

        if(username.isEmpty())
        {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty())
        {
            Toast.makeText(this, "Please enter your valid email", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty())
        {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.isEmpty())
        {
            Toast.makeText(this, "Please enter your mobile", Toast.LENGTH_SHORT).show();
        }
        else if(confirmPwd.isEmpty())
        {
            Toast.makeText(this, "Please enter your confirm password", Toast.LENGTH_SHORT).show();
        }
        else if(!confirmPwd.equals(password))
        {
            Toast.makeText(this, "Password do not match. Please type correct password!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            result = true;
        }

        return result;
    }

    private void sendUserData(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Chat_Activity").child(firebaseAuth.getUid());
        reference1 = firebaseDatabase.getReference("Profile_Activity").child(mobile);
        String token =  firebaseAuth.getUid();
        UserProfile userProfile = new UserProfile(email, username, password, token, mobile);
        reference.setValue(userProfile);
        reference1.setValue(userProfile);
    }

}
