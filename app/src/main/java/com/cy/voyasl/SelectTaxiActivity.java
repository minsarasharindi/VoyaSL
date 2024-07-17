package com.cy.voyasl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectTaxiActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private TaxiAdapter productAdapter;
    private List<AddTaxiModelview> productList;
    private LottieAnimationView mAnimationView;
    private AppCompatButton mPaymentOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_taxi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        initView();

        SearchView searchView = findViewById(R.id.productSearchView);
        productRecyclerView = findViewById(R.id.RecyclerView);
        productList = new ArrayList<>();
        productAdapter = new TaxiAdapter(this, productList);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(productAdapter);

        mPaymentOptions.setOnClickListener(v -> {
            Intent intent = new Intent(this,PaymentOptionsActivity.class);
            startActivity(intent);

        });

        FirebaseDatabase.getInstance().getReference().child("Added Taxi")
                //.orderByChild("Category").equalTo(Prefs.getString("KCategory",""))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AddTaxiModelview product = snapshot.getValue(AddTaxiModelview.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                        mAnimationView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private void initView() {
        mAnimationView = findViewById  (R.id.animationView);
        mPaymentOptions = findViewById  (R.id.PaymentOptions);
    }
}