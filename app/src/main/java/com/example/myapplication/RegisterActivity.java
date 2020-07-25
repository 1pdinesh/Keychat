package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    EditText  user_email, user_password, user_confirmPassword;
    Button btn_register;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, reference1;
    String email, password, confirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        user_email = findViewById(R.id.email);
        user_password = findViewById(R.id.password);
        user_confirmPassword = findViewById(R.id.etRePassword);
        btn_register = findViewById(R.id.signup);


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
                                Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }else{
                                checkConnection();
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


        email = user_email.getText().toString();
        password = user_password.getText().toString();
        confirmPwd = user_confirmPassword.getText().toString();


        if(email.isEmpty())
        {
            user_email.setError("Email Empty");
        }
        else if(password.isEmpty())
        {
            user_password.setError("Password Empty");
        }
        else if(confirmPwd.isEmpty())
        {
            user_confirmPassword.setError("Confirm Password Empty");
        }
        else if(!confirmPwd.equals(password))
        {
            user_confirmPassword.setError("Password do not match!");
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
      //  reference1 = firebaseDatabase.getReference("Profile_Activity").child(mobile);
        String token = firebaseAuth.getUid();
        UserRegistration userRegistration = new UserRegistration(email, password, token);
        reference.setValue(userRegistration);
       // reference1.setValue(userProfile);
    }

    private void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork == null)
        {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Email already exist!", Toast.LENGTH_SHORT).show();
        }
    }
}
