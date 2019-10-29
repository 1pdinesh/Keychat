package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText username, email, password,token,mobile;
    private Button update;
    private CheckBox show_password;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String token_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        update = findViewById(R.id.btn_update);
        token = findViewById(R.id.token);
        show_password = findViewById(R.id.show_password);
        mobile = findViewById(R.id.mobile);

        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(show_password.isChecked())
                {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chat_Activity").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                username.setText(userProfile.getUserName());
                email.setText(userProfile.getUserEmail());
                password.setText(userProfile.getUserPassword());
                mobile.setText(userProfile.getMobile());
                token.setText(userProfile.getToken());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu edit) {
        getMenuInflater().inflate(R.menu.edit,edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.edit:
            {
                updateProfile();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile()
    {
        update.setVisibility(View.VISIBLE);
        username.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        mobile.setEnabled(true);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                String user_email = email.getText().toString();
                String passwords = password.getText().toString();
                String userMobile = mobile.getText().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat_Activity").child(firebaseUser.getUid());
                String token_id = firebaseUser.getUid();
                UserProfile userProfile = new UserProfile(user_email, name, passwords, token_id, userMobile);
                reference.setValue(userProfile);

                update.setVisibility(View.INVISIBLE);
                username.setEnabled(false);
                email.setEnabled(false);
                mobile.setEnabled(false);
                password.setEnabled(false);
            }
        });

    }
}
