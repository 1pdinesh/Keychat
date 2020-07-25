package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText username, email, password,mobile;
    private Button update;
    private CheckBox show_password;
    private Toolbar toolbar;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String name, user_email, passwords, userMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        update = findViewById(R.id.btn_update);
        show_password = findViewById(R.id.show_password);
        mobile = findViewById(R.id.mobile);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        if(checkConnection() == false) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserRegistration userRegistration = dataSnapshot.getValue(UserRegistration.class);
//                    username.setText(userProfile.getUserName());
                    email.setText(userRegistration.getUserEmail());
                    password.setText(userRegistration.getUserPassword());
                 //   mobile.setText(userProfile.getMobile());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private void updateProfile() {
        if(checkConnection() == false)
        {
            update.setVisibility(View.VISIBLE);
           // username.setEnabled(true);
            password.setEnabled(true);
           // mobile.setEnabled(true);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  name = username.getText().toString();
                    user_email = email.getText().toString();
                    passwords = password.getText().toString();
                  //  userMobile = mobile.getText().toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat_Activity").child(firebaseUser.getUid());
                    String token = firebaseUser.getUid();
                    UserRegistration userRegistration = new UserRegistration(user_email,  passwords, token);
                    reference.setValue(userRegistration);

                    update.setVisibility(View.INVISIBLE);
                 //   username.setEnabled(false);
                    mobile.setEnabled(false);
                   // password.setEnabled(false);
                }
            });
        }

    }

    private Boolean checkConnection() {
        Boolean result = true;
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork == null)
        {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            result = false;
        }
        return result;
    }
}
