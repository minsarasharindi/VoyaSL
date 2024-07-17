package com.cy.voyasl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

public class RegisteActivity extends AppCompatActivity {


    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mMobile;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private AppCompatButton mRegisterbutton;

    private DatabaseReference mDatabase;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mRegisterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String mobile = mMobile.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisteActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    mConfirmPassword.setError("Password does not match");
                }

                registerUser(firstName,lastName,email,mobile,password,confirmPassword);


            }
        });

    }

    private void registerUser(String firstName, String lastName, String email, String mobile, String password, String confirmPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisteActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            TastyToast.makeText(getApplicationContext(), "User Registered Successful ", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firstName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            User user = new User(firstName, lastName ,email, mobile);
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        TastyToast.makeText(getApplicationContext(), "User Registered Successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                                        // firebaseUser.sendEmailVerification();


                                        Intent intent = new Intent(RegisteActivity.this, LoginActivity.class);
                                        intent.putExtra("usermail",mEmail.getText().toString());
                                        intent.putExtra("userPw",mPassword.getText().toString());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();


                                    } else {
                                        dialog.dismiss();

                                        TastyToast.makeText(getApplicationContext(), "User Registered failed . Please try again", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                    }


                                }
                            });


                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mPassword.setError("Your Password is too weak");
                                mPassword.requestFocus();
                                dialog.dismiss();

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mEmail.setError("Your email is invalid or already in use");
                                mEmail.requestFocus();
                                dialog.dismiss();
                            } catch (FirebaseAuthUserCollisionException e) {
                                mEmail.setError("User is already registered with this email");
                                mEmail.requestFocus();
                                dialog.dismiss();
                            } catch (Exception e) {
                                Log.e("", e.getMessage());
                            }
                            // mProgressBar1.setVisibility(View.GONE);
                        }

                    }
                });
    }


    private void initView() {
        mFirstName = findViewById  (R.id.FirstName);
        mLastName = findViewById  (R.id.LastName);
        mEmail = findViewById  (R.id.Email);
        mMobile = findViewById  (R.id.Mobile);
        mPassword = findViewById  (R.id.Password);
        mConfirmPassword = findViewById  (R.id.ConfirmPassword);
        mRegisterbutton = findViewById  (R.id.registerbutton);
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