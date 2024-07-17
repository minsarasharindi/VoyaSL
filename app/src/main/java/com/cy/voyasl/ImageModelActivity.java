package com.cy.voyasl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cy.voyasl.ml.ModelVoyasl;
import com.pixplicity.easyprefs.library.Prefs;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageModelActivity extends AppCompatActivity {

    private ImageView mImg1;
    int imageSize = 224;
    private Bitmap img;
    private TextView mTitle;
    private AppCompatButton mViewFull;
    private File photoFile;
    private Uri filePath;
    String imageUrl;



    private static final int REQUEST_CAMERA_CAPTURE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_model);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // startActivityForResult(cameraIntent, 1);

            dispatchTakePictureIntent();

        } else {
            dispatchTakePictureIntent();
        }


        mViewFull.setOnClickListener(v -> {
            Intent intent = new Intent(this, viewLocationActivity.class);
            Prefs.putString("KTitle",mTitle.getText().toString());
            startActivity(intent);
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                .setAspectRatio(1, 1)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_CAPTURE && resultCode == RESULT_OK && photoFile != null) {
            Uri photoUri = Uri.fromFile(photoFile);
            cropImage(photoUri);

        } else if (requestCode == CropImage.CROP
        _IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                Uri resultUri = result.getUri();
                filePath = result.getUri();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);

                    classifyImage(image);

                    mImg1.setImageBitmap(image);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load the cropped image", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void classifyImage(Bitmap image) {

        try {
            ModelVoyasl model = ModelVoyasl.newInstance(ImageModelActivity.this);

            //loads the image into a ByteBuffer and processes it using the model
            //creates a TensorBuffer called inputFeature0 with a specific size (1, 224, 224, 3) and data type (FLOAT32).
            //it input for model
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;

            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            //Load the Input Data
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            //ML model classifies the input image
            ModelVoyasl.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //extracts the confidence scores for each class
            float[] confidences = outputFeature0.getFloatArray();

            //classification results are displayed using a bar chart

            String[] classes = {
                    "Lahugala Magul Maha Viharaya",
                    "muhudu maha viharaya",
                    "Nuwaragala Mountain",
                    "Abayagiriya Viharaya",
                    "Dambulla Royal Cave Temple and Golden Temple",
                    "Isurumuniya Royal Temple",
                    "Jaya Sri Maha Bodhi",
                    "Ruwanweli Maha Seya",
                    "Adisham Bungalow (St.Benedict's Monastery)",
                    "Diyaluma Falls",
                    "Nine Arch Bridge",
                    "Batticaloa Fort",
                    "Batticaloa Lighthouse",
                    "Colombo Lotus Tower",
                    "Colombo National Museum",
                    "Gangaramaya (Vihara) Buddhist Temple",
                    "Galle fort",
                    "Talpe Beach",
                    "Underwater Museum",
                    "maligatenna rajamaha viharaya",
                    "Agro Technology Park",
                    "Birds Park- Birds Research Center Hambantota",
                    "Tissamaharama Raja Maha Vihara",
                    "Dambakola Patuna Sangamitta Temple",
                    "Jaffna Fort",
                    "Nallur Kandaswamy Kovil",
                    "Kalutara Bodhiya (Ihala Maluwa)",
                    "lankatilaka vihara temple",
                    "Royal Botanical Gardens-Perandeniya Botanical Gard",
                    "Sri Dalada Maligawa-Temple Of The Truth",
                    "Kitulgala Water Rafting",
                    "Pinnawala elephant orphanage",
                    "Elephant Pass War Memorial",
                    "Iranamadu Tank",
                    "Rambadagalla viharaya",
                    "Yapahuwa Rock Fortress",
                    "Talaimannar Lighthouse",
                    "ThiruKetheeswaram Kovil",
                    "Rathna Ella Waterfall",
                    "Sigiriya",
                    "Matara Clock Tower",
                    "Uthpalawanna Sri Vishnu Maha Dewalaya",
                    "Galabedda Archaeological Site",
                    "Kataragama Kiri Vehera",
                    "Kurundi Temple",
                    "Puthukkudiyiruppu War Museum",
                    "Ambewela Farm",
                    "kadadora viharaya",
                    "Seetha Amman Kovil",
                    "Gal Vihara",
                    "Lankatilaka Temple",
                    "Polonnaruwa Vatadage",
                    "Munneshwaram Hindu Temple",
                    "St Anne s Church",
                    "Elle wala",
                    "Ranmudu Waterfall",
                    "Sampoor Lighthouse",
                    "seruwila mangala raja maha vihara",
                    "Nallur kandasamy kovil"
            };


            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            //result.setText(classes[maxPos]);

            final Handler handler = new Handler(Looper.getMainLooper());
            int finalMaxPos = maxPos;
            mTitle.setText(classes[finalMaxPos] + "");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 5000);


           /* //Control UI Elements Based on Classification
            if (classes[maxPos].equals("Ear mites")) {


            } else if (classes[maxPos].equals("Fleas")) {


            } else if (classes[maxPos].equals("Hair Loss")) {


            } else if (classes[maxPos].equals("Paralysis")) {


            } else if (classes[maxPos].equals("Ringworm")) {


            }*/


            //contains detailed information about each class's confidence percentage
            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
                // Toast.makeText(this, s+"", Toast.LENGTH_SHORT).show();
                //mDisease.setText(s+""); //displayed percentage in text view
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void initView() {
        mTitle = findViewById (R.id.title);
        mViewFull = findViewById  (R.id.ViewFull);
        mImg1 = (ImageView) findViewById(R.id.img1);

    }
}