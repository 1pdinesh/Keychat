package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {

    Button buttonupload,button;
    ImageView imageView;
    String s;
    Button gonext;
    File photoFile = null ;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    private FirebaseAuth firebaseAuth;
    private TextView url;
    private FirebaseAuth mAuth;
    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "Image";
    private StorageReference mStorageRef;
    private DatabaseReference reference;
    String timeStamp;
    private String downloadImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView =  findViewById(R.id.picture);
        url = findViewById(R.id.url);
        button = findViewById(R.id.capture);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonupload = findViewById(R.id.save_cloud);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Uploads");


        buttonupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(CameraActivity.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                File f = new File(mCurrentPhotoPath);
                Uri photoFile = Uri.fromFile(f);
                StorageReference storageReference = mStorageRef.child("Photos").child(photoFile.getLastPathSegment());
                final UploadTask uploadTask = storageReference.putFile(photoFile);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(CameraActivity.this, "Success",Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(CameraActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                            }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");

                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null)
                {
                    try
                    {
                        photoFile = createImageFile();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                    if (photoFile != null)
                    {
                        Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
                                "com.example.myapplication.fileprovider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            File imgFile = new  File(mCurrentPhotoPath);
            if(imgFile.exists())            {
                Picasso.get().load(imgFile).into(imageView);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

}
