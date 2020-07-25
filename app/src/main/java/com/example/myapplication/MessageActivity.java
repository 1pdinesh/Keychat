package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private EditText inputMessage;
    MessageAdapter messageAdapter;
    List<SendReceiveMessage> mChat;
    private ImageButton send, voice, camera, gps;
    protected FirebaseUser firebaseUser;
    private Toolbar toolbar;
    FirebaseAuth firebaseAuth;

    CircleImageView img;
    TextView Name;
    private String uniqueId = UUID.randomUUID().toString();
    private FirebaseAuth mFirebaseAuth;
    private Uri filepath;
    private RecyclerView recyclerView;
    private boolean notify = false;
    Uri image_rui = null;
    DatabaseReference reference;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    Integer cameraRequest = 1, galleryRequest = 2;
    private static final int CAMERA_REQUEST_CODE = 5;
    String timeStamp, mCurrentPhotoPath;
    ;
    //private static final int CAMERA_REQUEST_CODE = 100;
    //private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    File photoFile = null;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final String REQUEST_MICROPHONE = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder m;
    protected String audioName = null;
    private static final String LOG_TAG = "Record_log";
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    public StorageReference audioStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        send = findViewById(R.id.send);
        camera = findViewById(R.id.camera);

        inputMessage = findViewById(R.id.inputMessage);
        recyclerView = findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        gps = findViewById(R.id.gps);
        voice = findViewById(R.id.mic);
        send.setVisibility(View.INVISIBLE);
        voice.setVisibility(View.VISIBLE);

        audioName = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioName += "/recorded_audio.3gp";
        audioStorage = FirebaseStorage.getInstance().getReference();


        final String name = getIntent().getStringExtra("name");
        final String userid = getIntent().getStringExtra("id");


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar ab=getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        LayoutInflater li =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action=li.inflate(R.layout.custom_bar,null);
        ab.setCustomView(action);
        img=findViewById(R.id.profileee);
        Name=findViewById(R.id.realname);
        Name.setText(name);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chat_Activity").child(firebaseUser.getUid());
        if(checkConnection() == false) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserRegistration userRegistration = dataSnapshot.getValue(UserRegistration.class);

                    Picasso.get().load(userRegistration.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(img, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(userRegistration.getImageurl()).into(img);
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MessageActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        arrayAdapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1, myArrayList);







        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textMessage = inputMessage.getText().toString().trim();
                if (textMessage.matches("")) {
                    send.setVisibility(View.INVISIBLE);
                    voice.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    gps.setVisibility(View.VISIBLE);
                } else {
                    send.setVisibility(View.VISIBLE);
                    voice.setVisibility(View.INVISIBLE);
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
                if (!msg.equals("")) {


                    sendMessage(firebaseUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message!", Toast.LENGTH_SHORT).show();
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
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gt = new GpsTracker(getApplicationContext());
                Location l = gt.getLocation();
                if (l == null) {
                    Toast.makeText(getApplicationContext(), "Permission needed", Toast.LENGTH_SHORT).show();
                } else {
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", firebaseUser.getUid());
                    hashMap.put("receiver", getIntent().getStringExtra("id"));
                    hashMap.put("longg", lon );
                    hashMap.put("lat", lat );
                    hashMap.put("timestamp", timeStamp );
                    hashMap.put("type", "Location");

                    databaseReference.child("Chats").push().setValue(hashMap);
                }
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
                        if (items[which].equals("Camera")) {


                            if (checkPermissionREAD_EXTERNAL_STORAGE(MessageActivity.this)) {
                                pickFromCamera();
                            }

                        } else if (items[which].equals("Gallery")) {
                            //startActivity(new Intent(MessageActivity.this, GallaryActivity.class));

                            pickFromGallery();

                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }

        });





        voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    requestAudioPermissions();
                }

                 else if (event.getAction() == MotionEvent.ACTION_UP) {


                    stopRecording();


                }


                return false;
            }


        });

    }


    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {


                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            startRecording();
        }
    }

    //Handling callback

    public boolean checkPermissionREAD_EXTERNAL_STORAGE (final Context context){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog ( final String msg, final Context context,
                             final String permission){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void pickFromCamera()
    {



        ContentValues cv = new ContentValues();
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }







    private void pickFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void startRecording() {
        m = new MediaRecorder();
        m.setAudioSource(MediaRecorder.AudioSource.MIC);
        m.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        m.setOutputFile(audioName);
        m.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            m.prepare();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        m.start();

    }

    private void stopRecording() {
        m.stop();
        m.release();
        m = null;


        uploadaudio();
    }



    protected void uploadaudio() {

        final StorageReference filepath = audioStorage.child("audio").child( uniqueId+ ".3gp");
        final Uri uri = Uri.fromFile(new File(audioName));
        final String downloadURl1 = filepath.getPath();
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloadURI = uriTask.getResult().toString();

                if (uriTask.isSuccessful())
                {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", firebaseUser.getUid());
                    hashMap.put("receiver", getIntent().getStringExtra("id"));
                    hashMap.put("message", downloadURI);
                    hashMap.put("timestamp", timeStamp );
                    hashMap.put("type", "voice");
                    hashMap.put("contentLocation",downloadURl1);






                    databaseReference.child("Chats").push().setValue(hashMap);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void sendMessage(String sender, String receiver, String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");
        reference.child("Chats").push().setValue(hashMap);
    }

    private void receiveMessage(final String myid, final String userid) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SendReceiveMessage sendReceiveMessage = ds.getValue(SendReceiveMessage.class);
                    if (sendReceiveMessage.getReceiver().equals(myid) && sendReceiveMessage.getSender().equals(userid)
                            || sendReceiveMessage.getReceiver().equals(userid) && sendReceiveMessage.getSender().equals(myid)) {
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

    private void sendCameraMessage() throws IOException {
        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
        progressDialog.setTitle("sending image from camera");
        progressDialog.show();


        final String timeStamp = ""+System.currentTimeMillis();
        String fileNameAndPath = "ChatImages/"+"post_"+timeStamp;
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_rui);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloadURI = uriTask.getResult().toString();
                if (uriTask.isSuccessful())
                {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", firebaseUser.getUid());
                    hashMap.put("receiver", getIntent().getStringExtra("id"));
                    hashMap.put("message", downloadURI);
                    hashMap.put("timestamp", timeStamp );
                    hashMap.put("type", "image");

                    databaseReference.child("Chats").push().setValue(hashMap);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendImageMessage(Uri image_rui) throws IOException {

        notify = true;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image...");
        progressDialog.show();

        final String timeStamp = ""+System.currentTimeMillis();
        String fileNameAndPath = "ChatImages/"+"post_"+timeStamp;
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_rui);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                String downloadURI = uriTask.getResult().toString();
                if (uriTask.isSuccessful())
                {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", firebaseUser.getUid());
                    hashMap.put("receiver", getIntent().getStringExtra("id"));
                    hashMap.put("message", downloadURI);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "image");
                    databaseReference.child("Chats").push().setValue(hashMap);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    pickFromCamera();
                }
                else
                {
                    Toast.makeText(this, "GET_ACCOUNTS Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startRecording();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {

                try {
                    image_rui = data.getData();
                    sendImageMessage(image_rui);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                try {
                    sendCameraMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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


