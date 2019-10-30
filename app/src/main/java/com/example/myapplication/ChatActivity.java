package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ListView listView;
    private Toolbar toolbar;
    DatabaseReference reference, reference1, reference2;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayList<String> myArrayList1 = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    long maxid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        listView = findViewById(R.id.ListView);
        reference2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                arrayAdapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1,myArrayList);
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                myArrayList.add(userProfile.getMobile());
                myArrayList1.add(userProfile.getToken());
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String a = myArrayList.get(position);
                        String b = myArrayList1.get(position);
                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                        intent.putExtra("name", a);
                        intent.putExtra("id",b);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu edit) {
        getMenuInflater().inflate(R.menu.menu,edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.friend:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Add Mobile");
                final EditText addFriend = new EditText(ChatActivity.this);
                addFriend.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(addFriend);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String myText = addFriend.getText().toString();
                        reference = FirebaseDatabase.getInstance().getReference("Profile_Activity").child(myText);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        maxid = dataSnapshot.getChildrenCount();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                arrayAdapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1,myArrayList);
                                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                arrayAdapter.notifyDataSetChanged();
                                listView.setAdapter(arrayAdapter);
                                reference1.child(String.valueOf(maxid+1)).setValue(userProfile);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }

            case R.id.logout:
            {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                break;
            }

            case R.id.profile:
            {
                startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
