package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private TextView User_mobile;
    private EditText inputMessage;
    private ImageButton send;
    private Toolbar toolbar;
    private ListView listView;
    DatabaseReference reference, reference1;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputMessage = findViewById(R.id.inputMessage);
        send = findViewById(R.id.send);
        listView = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        arrayAdapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1,myArrayList);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Profile_Activity").child(getIntent().getStringExtra("name"));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference1 = FirebaseDatabase.getInstance().getReference("Message").child("Send_Message")
                                .child(getIntent().getStringExtra("name"));
                        String sender = inputMessage.getText().toString();
                        String reciever = "Message";
                        SendReceiveMessage sendReceiveMessage = new SendReceiveMessage(sender, reciever);
                        reference1.setValue(sendReceiveMessage);
                        myArrayList.add(sender);
                        listView.setAdapter(arrayAdapter);
                        inputMessage.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        reference1 = FirebaseDatabase.getInstance().getReference("Message").child("Send_Message")
                .child(getIntent().getStringExtra("name"));
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final SendReceiveMessage sendReceiveMessage = dataSnapshot.getValue(SendReceiveMessage.class);
                myArrayList.add(sendReceiveMessage.getSender());
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu edit) {
        getMenuInflater().inflate(R.menu.icon,edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.camera:
            {
                break;
            }

            case R.id.gallery:
            {
                break;
            }

            case R.id.gps:
            {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
