package com.cy.voyasl;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

 
    private FirebaseAuth authProfile;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView mUname;
    private Dialog dialog;
    private DatabaseReference mDatabase;

    private TextView mEmail;
    private TextView mNumber;
    private AppCompatButton mLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();


        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        mLogout = findViewById (R.id.logout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        showLoagingDialog(ProfileActivity.this);

        mLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            editor.putBoolean("login", false);
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        mDatabase.child("Registered Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String fname = dataSnapshot.child("firstName").getValue(String.class);
                String lname = dataSnapshot.child("lastName").getValue(String.class);

                String email = dataSnapshot.child("email").getValue(String.class);
                String number = dataSnapshot.child("mobile").getValue(String.class);


                mUname.setText(fname + " " + lname);
                mEmail.setText(email);
                mNumber.setText(number);



                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });






    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        //mHome = findViewById  (R.id.home0);


        mUname = findViewById  (R.id.uname);
        mEmail = findViewById  (R.id.email);
        mNumber = findViewById  (R.id.Number);
        mLogout = findViewById  (R.id.logout);
    }

    private void showLoagingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}