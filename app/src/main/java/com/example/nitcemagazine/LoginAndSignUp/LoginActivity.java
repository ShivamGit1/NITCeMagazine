package com.example.nitcemagazine.LoginAndSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button signin;

    TextView signUp, forgotPassword;
    String token;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signin = findViewById(R.id.buttomSignIn);
        signUp = findViewById(R.id.textViewSignUp);
        forgotPassword = findViewById(R.id.textViewForgotPassword);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmailId = username.getText().toString();
                String userPassword = password.getText().toString();

                if (userEmailId.isEmpty() && userPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the Email id and Password", Toast.LENGTH_SHORT).show();
                } else if (userEmailId.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();

                } else if (userPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                } else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                    signInWithFirebase(userEmailId, userPassword);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpPage.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void signInWithFirebase(String userEmailId, String userPassword) {
        auth.signInWithEmailAndPassword(userEmailId,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String role = snapshot.getValue().toString();
                            if(role.equalsIgnoreCase("Admin"))
                            {
                                Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                            else if(auth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(LoginActivity.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }
}