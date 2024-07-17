package com.cy.voyasl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class viewLocationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private TextView mTitle;
    private TextView mDescription;
    private ImageView mTexi;
    private ImageSlider mImageSlider;
    String MKIDD ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Create a query to search for the Title
        String tt = Prefs.getString("KTitle","");
        Query query = mDatabase.child("Added Location").orderByChild("Title").equalTo(tt);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String title = snapshot.child("Title").getValue(String.class);
                        String description = snapshot.child("Description").getValue(String.class);
                        MKIDD = snapshot.child("ID").getValue(String.class);

                        /*imageLinks[0] = snapshot.child("ImgUrl_1").getValue(String.class);
                        imageLinks[1] = snapshot.child("ImgUrl_2").getValue(String.class);
                        imageLinks[2] = snapshot.child("ImgUrl_3").getValue(String.class);
*/
                        ArrayList<SlideModel> imageList = new ArrayList<>();


                        imageList.add(new SlideModel(snapshot.child("ImgUrl_1").getValue(String.class), ScaleTypes.FIT));
                        imageList.add(new SlideModel(snapshot.child("ImgUrl_2").getValue(String.class), ScaleTypes.FIT));
                        imageList.add(new SlideModel(snapshot.child("ImgUrl_3").getValue(String.class), ScaleTypes.FIT));

                        mImageSlider.setImageList(imageList, ScaleTypes.FIT);


                        mTitle.setText(title);
                        mDescription.setText(description);

                       /* new Thread(() -> {
                            String translatedDescription = TranslationUtils.translateToSinhala(description);
                            runOnUiThread(() -> {

                            });
                        }).start();*/

                       // Picasso.get().load(Imagelink1).into(mImg1);



                        System.out.println("Description: " + description);
                    }
                } else {
                    System.out.println("No data matching the specified title.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                System.err.println("Failed to read value: " + databaseError.toException());
            }
        });


        mTexi.setOnClickListener(v -> {

            Intent intent = new Intent(this,ChooseRideActivity.class);
            Prefs.putString("MKIDD",MKIDD);
            startActivity(intent);

        });

    }

    private void initView() {

        mTitle = findViewById(R.id.title);
        mDescription = findViewById(R.id.Description);
        mTexi = findViewById(R.id.texi);
        mImageSlider = findViewById  (R.id.image_slider);
    }



}