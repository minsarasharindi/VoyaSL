package com.cy.voyasl;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity {


    private AppCompatButton mAddlocation;
    private AppCompatButton mAddtaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        mAddlocation.setOnClickListener(v -> {
            Intent intent = new Intent(this,AddActivity.class);
            startActivity(intent);

        });

        mAddtaxi.setOnClickListener(v -> {
            Intent intent = new Intent(this,AddTaxiActivity.class);
            startActivity(intent);

        });

    }

    private void initView() {
        mAddlocation = findViewById  (R.id.addlocation);
        mAddtaxi = findViewById  (R.id.addtaxi);
    }
}