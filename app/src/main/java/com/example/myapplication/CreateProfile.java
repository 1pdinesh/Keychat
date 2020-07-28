package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfile extends AppCompatActivity {
     EditText user_name, mobile;
    ImageView uploadimg;
    Button CreateProfile;
    private String fname,fmobile,SaveCD,SaveT;
    private String productRandomKey, downloadImageUrl;
    FirebaseAuth mAuth;
    private StorageReference mRef;
    FirebaseDatabase firebaseDatabase;

    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    private FirebaseStorage firebaseStorage;
    DatabaseReference  reference1,reference2;
    Uri pickedImgUri;
    Uri resultUri;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mAuth=FirebaseAuth.getInstance();
        uploadimg =findViewById(R.id.uploadimg);
        user_name =findViewById(R.id.username);
        mobile =findViewById(R.id.mobile);
        CreateProfile= (Button) findViewById(R.id.btnCreateProfile);
        firebaseStorage = FirebaseStorage.getInstance();
        mRef=firebaseStorage.getReference();


        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >=22){

                    CheckPermission();
                }
                else {
                    openGallery();
                }
            }
        });

        CreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validate();

            }
        });


    }
    private void Validate() {


            storeStudentimg();

    }

    private void storeStudentimg() {
        Calendar Cal=Calendar.getInstance();

        SimpleDateFormat CurrentD=new SimpleDateFormat("yyyy-MM-dd");
        SaveCD =CurrentD.format(Cal.getTime());

        SimpleDateFormat CurrentT=new SimpleDateFormat("HH:mm:ss a");
        SaveT =CurrentT.format(Cal.getTime());

        productRandomKey = SaveCD + SaveT;


        final StorageReference filePath = mRef.child(resultUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(resultUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(CreateProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(CreateProfile.this, "Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();


                            SaveInfoToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void SaveInfoToDatabase() {
        firebaseAuth = FirebaseAuth.getInstance();
        fname=user_name.getText().toString();
        fmobile=mobile.getText().toString();
        String token = firebaseAuth.getUid();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap<String, Object> StudentMap = new HashMap<>();
                StudentMap.put("Imageurl", downloadImageUrl);
                StudentMap.put("userName", fname);
                StudentMap.put("mobile", fmobile);
                 StudentMap.put("token", token);
        firebaseDatabase = FirebaseDatabase.getInstance();
                reference1 = firebaseDatabase.getReference("Profile_Activity").child(fmobile);
            reference2= firebaseDatabase.getReference("Chat_Activity").child(currentuser);

                reference1.setValue(StudentMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    firebaseAuth.signOut();
                                    Intent intent = new Intent(CreateProfile.this, MainActivity.class);

                                    startActivity(intent);


                                    Toast.makeText(CreateProfile.this, "Welcome", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    String message = task.getException().toString();
                                    Toast.makeText(CreateProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        reference2.updateChildren(StudentMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            firebaseAuth.signOut();
                            Intent intent = new Intent(CreateProfile.this, MainActivity.class);

                            startActivity(intent);


                            Toast.makeText(CreateProfile.this, "Welcome", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            String message = task.getException().toString();
                            Toast.makeText(CreateProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }



    private void CheckPermission() {

        if (ContextCompat.checkSelfPermission(CreateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                SweetAlertDialog progressDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                progressDialog.setCancelable(false);
                progressDialog.setTitleText("Permission is needed. Please allow permission");
                progressDialog.setCancelText("Cancel");
                progressDialog.setConfirmText("Ok");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        ActivityCompat.requestPermissions(CreateProfile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PReqCode );
                    }
                });
                progressDialog.show();
            }

            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode );
            }
        }
        else
        {
            openGallery();
        }


    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data !=null){

            //succesfully picked an image
            //need to save ref to a uri
            pickedImgUri = data.getData();
            CropImage.activity(pickedImgUri)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                uploadimg.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




}
