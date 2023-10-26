package com.example.nitcemagazine.LoginAndSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpPage extends AppCompatActivity {

    EditText username,password,confirmPassword,name;
    Button signUp;
    CircleImageView profilePicture;
    LinearLayout logIn;

    boolean imgControl = false;
    Uri imageUri;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        username = findViewById(R.id.input_Email_Reg);
        name = findViewById(R.id.input_Full_name_reg);
        password = findViewById(R.id.input_password_Reg);
        confirmPassword = findViewById(R.id.input_Confirm_password_reg);
        signUp = findViewById(R.id.buttom_Register);
        profilePicture = findViewById(R.id.profilePictureSignUp);
        logIn = findViewById(R.id.LinearLayout_Login_reg);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmailId = username.getText().toString();
                String userPassword = password.getText().toString();
                String confirmPasswordUser = confirmPassword.getText().toString();
                String nameUser = name.getText().toString();

                if(userEmailId.isEmpty() && userPassword.isEmpty() && confirmPasswordUser.isEmpty() && nameUser.isEmpty())
                {
                    Toast.makeText(SignUpPage.this, "Please enter the required field", Toast.LENGTH_SHORT).show();
                } else if(userEmailId.isEmpty())
                {
                    Toast.makeText(SignUpPage.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();
                } else if(userPassword.isEmpty())
                {
                    Toast.makeText(SignUpPage.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                } else if (confirmPasswordUser.isEmpty()) {
                    Toast.makeText(SignUpPage.this, "Please enter confirm password.", Toast.LENGTH_SHORT).show();

                } else if (nameUser.isEmpty()) {
                    Toast.makeText(SignUpPage.this, "Please enter name", Toast.LENGTH_SHORT).show();

                } else if (!confirmPasswordUser.equals(userPassword)) {
                    Toast.makeText(SignUpPage.this, "Password does not match with confirm password", Toast.LENGTH_SHORT).show();

                } else if(Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches())
                {
                    boolean check = checkNitcEmail(userEmailId,nameUser);
                    if(check) {
                        signUpFirebase(userEmailId, userPassword);
                    }
                }
                else
                {
                    Toast.makeText(SignUpPage.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                }
            }

        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private Boolean checkNitcEmail(String userEmailId, String nameUser) {
        String email[] = userEmailId.split("@");
        if(!email[1].equals("nitc.ac.in")) {
            Toast.makeText(SignUpPage.this, "Please enter a NITC email id", Toast.LENGTH_SHORT).show();
            return false;
        } else{
            for(int i = 0; i < name.length(); i++)
            {
                if(!Character.isAlphabetic(nameUser.charAt(i)) && !Character.isSpaceChar(nameUser.charAt(i)))
                {
                    System.out.println(nameUser.charAt(i));
                    Toast.makeText(SignUpPage.this, "Your name contains non alphabetic character", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private void signUpFirebase(String userEmailId, String userPassword) {
        auth.createUserWithEmailAndPassword(userEmailId,userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SignUpPage.this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                            UserDetails user = new UserDetails(name.getText().toString(), username.getText().toString());
                            reference.child("Student").child(auth.getCurrentUser().getUid()).setValue(user);
                            reference.child("UserType").child(auth.getCurrentUser().getUid()).setValue("Student");
                            setProfilePicture();
                            Intent intent = new Intent(SignUpPage.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                Toast.makeText(SignUpPage.this, "Already existing user.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(SignUpPage.this, "There is a problem, Please try after sometime", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            signUp.setClickable(true);
                        }
                    }
                });
    }


    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePicture);
            imgControl = true;
        }
        else
        {
            imgControl = false;
        }
    }

    private void setProfilePicture() {
        if(imgControl)
        {
            UUID randomId = UUID.randomUUID();
            String imgName = "images/" + randomId + ".jpg";
            storageReference.child(imgName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference myStorageRef = storage.getReference(imgName);
                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String filePath = uri.toString();
                            reference.child("Student").child(auth.getCurrentUser().getUid()).child("profilePictures").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SignUpPage.this, "Success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpPage.this, "Fail", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpPage.this, "this fail", Toast.LENGTH_SHORT).show();
                }
            });

        }

        else
        {
            reference.child("Student").child(auth.getCurrentUser().getUid()).child("profilePictures").setValue("null");
        }
    }
}