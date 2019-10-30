package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private EditText inputMessage;
    MessageAdapter messageAdapter;
    List<SendReceiveMessage>mChat;
    private ImageButton send,voice;
    private MediaRecorder mrecorder;
    private String audioName=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private static final String LOG_TAG = "AudioRecord";
    DatabaseReference reference, reference1, reference2;
    StorageReference  audio;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        inputMessage = findViewById(R.id.inputMessage);

    //voice
        voice=findViewById(R.id.mic);
        audioName= Environment.getExternalStorageDirectory().getAbsolutePath();
        audioName +="/recorded_audio.3gp";
        audio= FirebaseStorage.getInstance().getReference();
   //voice//

        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        inputMessage = findViewById(R.id.inputMessage);

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputMessage.getText().toString();

                if(!msg.equals(""))
                {
                    voice.setVisibility(View.INVISIBLE);
                    sendMessage(firebaseUser.getUid(), userid, msg);

                }
                else
                {
                    voice.setVisibility(View.VISIBLE);
                    Toast.makeText(MessageActivity.this, "You can't send empty message!",Toast.LENGTH_SHORT).show();
                }
                inputMessage.setText("");
            }
        });

        //voice//

        voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)v.getLayoutParams();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());

                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    layoutParams.width = width + 5;
                    layoutParams.height = height + 5;
                    voice.setLayoutParams(layoutParams);
                    voice.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    startRecording();


                }
               else if(event.getAction() == MotionEvent.ACTION_UP)
                {

                    layoutParams.width = width ;
                    layoutParams.height = height;
                    voice.setLayoutParams(layoutParams);
                    stopRecording();


                }



                return false;
            }
        });
//voice//



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

    }
//voice
    private void startRecording() {
        mrecorder = new MediaRecorder();
        mrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mrecorder.setOutputFile(audioName);
        mrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mrecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mrecorder.start();
    }


    private void stopRecording() {
        mrecorder.stop();
        mrecorder.release();
        mrecorder=null;


        uploadaudio();

    }

    private void uploadaudio() {

        StorageReference filepath =audio.child("audio").child("audio_new"+System.currentTimeMillis() +"audio.3gp");
        Uri uri=Uri.fromFile(new File(audioName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"audio recorded",Toast.LENGTH_SHORT).show();
            }
        });
    }
//voice

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

    private void sendMessage(String sender, String receiver, String message)
    {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
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
