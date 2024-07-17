package com.cy.voyasl;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.sdsmdg.tastytoast.TastyToast;

public class LoginActivity extends AppCompatActivity {


    private EditText mEmail;
    private EditText mPassword;
    private AppCompatButton mLoginbtn;
    private AppCompatButton mRegister;

    private FirebaseAuth authProfile;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Dialog dialog;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();


        mLoginbtn.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            } else if  (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, CAMERA_PERMISSION_REQUEST_CODE);
            }else if (!isLocationEnabled()) {
                requestLocationEnabled();
            } else {


                String TextEmail = mEmail.getText().toString();
                String  textPwd = mPassword.getText().toString();

                if (TextUtils.isEmpty(TextEmail)){
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    mEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(TextEmail).matches()) {
                    Toast.makeText(this, "Please re- enter your email", Toast.LENGTH_SHORT).show();
                    mEmail.setError("Valid email is required");
                    mEmail.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    mPassword.setError("Password is required");
                    mPassword.requestFocus();
                } else {

                    //mProgressBar.setVisibility(View.VISIBLE);
                    loginUser(TextEmail ,textPwd);
                    showLoagingDialog(LoginActivity.this);
                }

            }

            // Intent intent = new Intent(this,ProfilePictureActivity.class);
            //  startActivity(intent);
        });

        mRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this,RegisteActivity.class);
            startActivity(intent);
        });


        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String userMail;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userMail= null;
            } else {
                userMail= extras.getString("usermail");
            }
        } else {
            userMail= (String) savedInstanceState.getSerializable("usermail");
        }

        String userPw;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userPw= null;
            } else {
                userPw= extras.getString("userPw");
            }
        } else {
            userPw= (String) savedInstanceState.getSerializable("userPw");
        }

        mEmail.setText(userMail);
        mPassword.setText(userPw);

        // mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();


    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();


                    TastyToast.makeText(getApplicationContext(), "Login is successful !", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    editor.putBoolean("flogin", false);
                    editor.apply();



                }else {
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthInvalidUserException e){
                        dialog.dismiss();
                        mEmail.setError("User does not exist or is no longer valid. Please register again.");
                        mEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        dialog.dismiss();
                        mPassword.setError("check and re-enter.");
                        mPassword.requestFocus();

                    }catch (Exception e){
                        Log.e("tag",e.getMessage());
                        dialog.dismiss();
                    }
                    //TastyToast.makeText(getApplicationContext(), "Something went wrong!", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                }
                //  mProgressBar.setVisibility(View.GONE);

            }
        });
    }

    private void initView() {
        mEmail = findViewById  (R.id.Email);
        mPassword = findViewById  (R.id.Password);
        mLoginbtn = findViewById  (R.id.loginbtn);
        mRegister = findViewById  (R.id.register);
    }
    @Override
    protected void onStart() {
        super.onStart();

        boolean isFirstOpen = sharedPreferences.getBoolean("flogin", true);
        if (isFirstOpen) {

        } else {

            if (authProfile.getCurrentUser() != null) {
                // Toast.makeText(this, "Alredy loged In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                //  Toast.makeText(this, "not  loged In", Toast.LENGTH_SHORT).show();
            }
        }
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

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestLocationEnabled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location services are disabled. Do you want to enable them?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}