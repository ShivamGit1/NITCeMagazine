package com.example.nitcemagazine.LoginAndSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText username;
    Button reset;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = findViewById(R.id.editTextEmailAddressForReset);
        reset = findViewById(R.id.ResetPassword);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmailId = username.getText().toString();
                if (userEmailId.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();
                }
                else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                    resetPasswordWithFirebase(userEmailId);
                } else {
                    Toast.makeText(ForgotPassword.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void resetPasswordWithFirebase(String userId) {
        auth.sendPasswordResetEmail(userId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPassword.this, "Email Sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}