package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private EditText inputMessage;
    MessageAdapter messageAdapter;
    List<SendReceiveMessage>mChat;
    private ImageButton send,mic,camera,gps;
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;
    private Uri filepath;
    private RecyclerView recyclerView;
    DatabaseReference reference;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    Integer cameraRequest = 1, galleryRequest = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        inputMessage = findViewById(R.id.inputMessage);
        send = findViewById(R.id.send);
        mic = findViewById(R.id.mic);
        camera = findViewById(R.id.camera);
        gps = findViewById(R.id.gps);
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        send.setVisibility(View.INVISIBLE);
        mic.setVisibility(View.VISIBLE);

        final String num = getIntent().getStringExtra("name");
        final String userid = getIntent().getStringExtra("id");

        getSupportActionBar().setTitle(num);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        arrayAdapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1,myArrayList);

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textMessage = inputMessage.getText().toString().trim();
                if(textMessage.matches(""))
                {
                    send.setVisibility(View.INVISIBLE);
                    mic.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    gps.setVisibility(View.VISIBLE);
                }
                else
                {
                    send.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.INVISIBLE);
                    camera.setVisibility(View.INVISIBLE);
                    gps.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputMessage.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(firebaseUser.getUid(), userid, msg);
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "You can't send empty message!",Toast.LENGTH_SHORT).show();
                }
                inputMessage.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("People").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receiveMessage(firebaseUser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Select Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which].equals("Camera"))
                        {
                            startActivity(new Intent(MessageActivity.this, CameraActivity.class));
                        }
                        else if(items[which].equals("Gallery"))
                        {
                            startActivity(new Intent(MessageActivity.this, GallaryActivity.class));
                        }
                        else
                        {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message)
    {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        //hashMap.put("type",type);
        reference.child("Chats").push().setValue(hashMap);
    }

    private void receiveMessage(final String myid, final String userid)
    {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    SendReceiveMessage sendReceiveMessage = ds.getValue(SendReceiveMessage.class);
                    if(sendReceiveMessage.getReceiver().equals(myid) && sendReceiveMessage.getSender().equals(userid)
                    || sendReceiveMessage.getReceiver().equals(userid) && sendReceiveMessage.getSender().equals(myid))
                    {
                        mChat.add(sendReceiveMessage);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
