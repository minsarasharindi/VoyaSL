package com.cy.voyasl;

import static com.cy.voyasl.RandomStringGenerator.generateRandomString;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {


    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mLocation;
    private AppCompatButton mAddbutton;


    public static FirebaseAuth authProfile;
    FirebaseUser firebaseUser;

    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    String imageUrl1;
    private File photoFile1;
    private static final int REQUEST_CAMERA_CAPTURE = 101;
    private Dialog dialog;

    private  String IMG_NUMBER;

    private  String ImgUrl_1;
    private  String ImgUrl_2;
    private  String ImgUrl_3;
    private EditText mLatLang;

    Uri uri;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mImage1.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 200);
            } else {
                CropImage.startPickImageActivity(AddActivity.this);
                IMG_NUMBER = "n1";
            }
        });

        mImage2.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 200);
            } else {
                CropImage.startPickImageActivity(AddActivity.this);
                IMG_NUMBER = "n2";
            }
        });

        mImage3.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
            } else {
                CropImage.startPickImageActivity(AddActivity.this);
                IMG_NUMBER = "n3";
            }
        });



        mAddbutton.setOnClickListener(v -> {
            String ID = generateRandomString(5);
            String userID = firebaseUser.getUid();
            String Title = mTitle.getText().toString();
            String Description = mDescription.getText().toString();
            String Location = mLocation.getText().toString();
            String Approval = "no";

            showLoagingDialog(AddActivity.this);

            AddLocationModel addLocationModel = new AddLocationModel(ID, userID, Title, Description, Location, Approval, ImgUrl_1, ImgUrl_2, ImgUrl_3, mLatLang.getText().toString().trim());
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Added Location");
            referenceProfile.child(ID).setValue(addLocationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        TastyToast.makeText(getApplicationContext(), "Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        Intent intent = new Intent(AddActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        TastyToast.makeText(getApplicationContext(), "failed . Please try again", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }
                    // mProgesBar.setVisibility(View.GONE);

                }
            });



        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, "com.cy.voyasl.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    private void cropImage(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(195, 130)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK){

            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this,imageuri))
            {
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
            } else {
                cropImage(imageuri);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =  CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){


                filePath = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //mImage1.setImageBitmap(bitmap);

                    uploadImage1();
                    showLoagingDialog(AddActivity.this);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void uploadImage1() {

        if (filePath != null) {

            StorageReference ref = storageReference.child("Location_Image/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrl1 = uri.toString();
                                            // Toast.makeText(AddlocationActivity.this, imageUrl1, Toast.LENGTH_SHORT).show();
                                            Log.d("linkkk",imageUrl1);

                                            if (IMG_NUMBER.equals("n1")){
                                                Picasso.get().load(imageUrl1).into(mImage1);
                                                ImgUrl_1 = imageUrl1;
                                            } else if (IMG_NUMBER.equals("n2")){
                                                Picasso.get().load(imageUrl1).into(mImage2);
                                                ImgUrl_2 = imageUrl1;
                                            }else if (IMG_NUMBER.equals("n3")){
                                                Picasso.get().load(imageUrl1).into(mImage3);
                                                ImgUrl_3 = imageUrl1;
                                            }

                                        }
                                    });
                                }
                            }
                            dialog.dismiss();

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            //progressDialog.dismiss();
                            TastyToast.makeText(getApplicationContext(), "Failed " + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    });



        }

    }

    private void initView() {
        mImage1 = findViewById  (R.id.image1);
        mImage2 = findViewById  (R.id.image2);
        mImage3 = findViewById  (R.id.image3);
        mTitle = findViewById  (R.id.Title);
        mDescription = findViewById  (R.id.Description);
        mLocation = findViewById  (R.id.Location);
        mAddbutton = findViewById  (R.id.addbutton);
        mLatLang = findViewById  (R.id.Latitude);

    }
    private void showLoagingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        dialogView.setFocusable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}