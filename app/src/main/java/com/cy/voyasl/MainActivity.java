package com.cy.voyasl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager2 viewPager2;
    private SliderAdapter adapter;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = viewPager2.getCurrentItem();
            if (currentPosition == adapter.getItemCount() - 1) {
                viewPager2.setCurrentItem(0);
            } else {
                viewPager2.setCurrentItem(currentPosition + 1);
            }
        }
    };
    private ImageView mCameramodel;
    private ImageView mHome;
    private ImageView mPlace;
    private ImageView mProfile;
    private TextView mEmergencyHotline;
    private Handler handler = new Handler();
    private String[] textArray = {"Police Emergency Hotline \n118 / 119", "Ambulance / Fire & rescue \n110", "Accident Service-General Hospital-Colombo \n011-2691111", "Tourist Police \n011-2421052", "Police Emergency \n011-2433333", "Government Information Center \n1919", "Report Crimes \n011-2691500", "Emergency Police Mobile Squad \n011-5717171", "Fire & Ambulance Service \n011-2422222" };
    private int arrayIndex = 0;
    private ImageView mMenuicon;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView mtranslatable;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        startRepeatingTask();

        drawerLayout = findViewById(R.id.drawer_layout1);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navigationView = findViewById(R.id.nav1);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        mMenuicon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        mCameramodel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageModelActivity.class);
            startActivity(intent);
        });

        mHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaxiActivity.class);
            startActivity(intent);
        });

        mPlace.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaceActivity.class);
            startActivity(intent);
        });

        mProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        mtranslatable.setOnClickListener(v -> {
            Intent intent = new Intent(this, TranslateActivity.class);
            startActivity(intent);
        });



        List<ImageItem> imageItems = new ArrayList<>();
        // Replace URLs and descriptions with your actual image URLs and text
        imageItems.add(new ImageItem("https://media.istockphoto.com/id/1406974336/photo/train-passing-over-nine-arch-bridge.jpg?s=612x612&w=0&k=20&c=vcgZUUaLWpzdFg9Rw4hUTx5g1evILmY-CYtCe1G7SPc=", "Description 1", 1));
        imageItems.add(new ImageItem("https://media.istockphoto.com/id/1181382649/photo/colombo-sri-lanka-december-05-2018-view-of-the-colombo-city-skyline-with-modern-architecture.jpg?s=612x612&w=0&k=20&c=XIS9COAwhGXkQYqGKHcabMEpc64B_uwT2utuonAoWl0=", "Description 2", 2));
        imageItems.add(new ImageItem("https://media.istockphoto.com/id/1288609237/photo/spectacular-view-of-the-lion-rock-surrounded-by-green-rich-vegetation-picture-taken-from.jpg?s=612x612&w=0&k=20&c=Rkmk3T6SxqzMPyIOcSkeTLrMlb6aHo3gaQpqCrxBeZM=", "Description 3", 3));

        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        adapter = new SliderAdapter(this, imageItems);
        viewPager2.setAdapter(adapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // Slide duration 3 seconds
            }
        });

        // Important layout adjustments
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

// Use a CompositePageTransformer to combine multiple effects
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(0));  // Adjust margin as needed

// Scale transformation to make the center page larger and reduce the sides
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

// Set the transformer to ViewPager2
        viewPager2.setPageTransformer(transformer);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        int offsetPx = getResources().getDimensionPixelOffset(R.dimen.offset);

        ViewPager2 viewPager2 = findViewById(R.id.viewPagerImageSlider);
        viewPager2.addItemDecoration(new HorizontalMarginItemDecoration(pageMarginPx));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setPadding(offsetPx, 0, offsetPx, 0);


    }

    private Runnable textChanger = new Runnable() {
        @Override
        public void run() {
            mEmergencyHotline.setText(textArray[arrayIndex]);
            arrayIndex++; // Move to the next index in the array
            if (arrayIndex == textArray.length) arrayIndex = 0; // Reset index if it's at the end

            handler.postDelayed(this, 5000); // Schedule the task to run again in 5 seconds
        }
    };


    private void startRepeatingTask() {
        textChanger.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(textChanger); // Stop the repeating task when the activity is destroyed
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable); // Stop the slider when activity is not visible
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Resume the slider automatically
    }

    private void initView() {
        mCameramodel = findViewById (R.id.cameramodel);
        mHome = findViewById  (R.id.home);
        mPlace = findViewById  (R.id.place);
        mProfile = findViewById  (R.id.profile);
        mEmergencyHotline = findViewById  (R.id.EmergencyHotline);
        mMenuicon = findViewById  (R.id.menuicon);
        mtranslatable = findViewById  (R.id.translate);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.admin) {
            Intent intent = new Intent(this,AdminActivity.class);
            startActivity(intent);

        }

/*
        else if (id == R.id.addloc) {


        }

        else if (id == R.id.logut) {
            FirebaseAuth.getInstance().signOut();
            editor.putBoolean("login", false);
            editor.apply();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else if (id == R.id.Adminl) {

        }*/

        return true;
    }
}