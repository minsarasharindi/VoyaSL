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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class AddTaxiActivity extends AppCompatActivity {



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

    private ImageView mImage1;
    private Spinner mSpinnerTaxitype;
    private EditText mName;
    private EditText mVehicalnumber;
    private EditText mFee;
    private AppCompatButton mAddbutton;

    String Vehicaltype;

    Uri uri;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_taxi);
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
                CropImage.startPickImageActivity(AddTaxiActivity.this);
                IMG_NUMBER = "n1";
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vehicale_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTaxitype.setAdapter(adapter);
        mSpinnerTaxitype.setSelection(0, false);
        mSpinnerTaxitype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ensure the prompt item is not considered as a valid selection
                if (position != 0) {
                    String selectedGender = parent.getItemAtPosition(position).toString();
                    // Display the selected gender in a Toast message
                    //Toast.makeText(getApplicationContext(), "Selected Gender: " + selectedGender, Toast.LENGTH_SHORT).show();
                    Vehicaltype = selectedGender;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mAddbutton.setOnClickListener(v -> {
            String ID = generateRandomString(5);
            String userID = firebaseUser.getUid();
            String Name = mName.getText().toString();
            String Vehicalnumber = mVehicalnumber.getText().toString();



            showLoagingDialog(AddTaxiActivity.this);

            AddTaxiModel addTaxiModel = new AddTaxiModel(ID, userID, Name, Vehicalnumber,Vehicaltype, ImgUrl_1);
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Added Taxi");
            referenceProfile.child(ID).setValue(addTaxiModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        TastyToast.makeText(getApplicationContext(), "Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        Intent intent = new Intent(AddTaxiActivity.this, MainActivity.class);
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
                    showLoagingDialog(AddTaxiActivity.this);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void uploadImage1() {

        if (filePath != null) {

            StorageReference ref = storageReference.child("Taxi_Image/" + UUID.randomUUID().toString());
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
        mSpinnerTaxitype = findViewById  (R.id.spinner_taxitype);
        mName = findViewById (R.id.Name);
        mVehicalnumber = findViewById  (R.id.Vehicalnumber);
        mFee = findViewById  (R.id.Fee);
        mAddbutton = findViewById  (R.id.addbutton);
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