package com.example.nitcemagazine.LoginAndSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView userProfileImage;
    EditText name;
    TextView email,role;
    Button save;

    DatabaseReference dbreference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String roleOfUser;

    String rl,imgurl,nm;

    boolean imgControl = false;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userProfileImage=(CircleImageView) findViewById(R.id.profileImage);
        name=(EditText) findViewById(R.id.editTextProfileName);
        email=(TextView) findViewById(R.id.textViewProfileEmail);
        role=(TextView)findViewById(R.id.textViewProfileRole);
        save=(Button) findViewById(R.id.save_profile_button);

        dbreference= FirebaseDatabase.getInstance().getReference();
        dbreference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roleOfUser = snapshot.getValue().toString();
                dbreference.child(roleOfUser).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Get User Data
                        nm=snapshot.child("name").getValue().toString();
                        String eml=snapshot.child("email").getValue().toString();
                        rl=snapshot.child("role").getValue().toString();
                        imgurl=snapshot.child("profilePictures").getValue().toString();

                        //Set the data to View
                        if(!imgurl.equals("null")) {
                            Picasso.get().load(imgurl).into(userProfileImage);
                        }
                        else{
                            userProfileImage.setImageResource(R.drawable.img_1);
                        }
                        name.setText(nm);
                        email.setText(eml);
                        role.setText(rl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If empty name
                if(name.getText().toString().isEmpty()){
                    Toast.makeText(UserProfile.this, "Enter Name", Toast.LENGTH_SHORT).show();
                }
                //If name is not alphabetic
                else if (!isAlpha(name.getText().toString())) {
                    Toast.makeText(UserProfile.this, "Your name contains non alphabetic character", Toast.LENGTH_SHORT).show();
                }
                //Change name and profile pic
                else{
                    if((!name.getText().toString().equals(nm)) && imgControl==false){
                        dbreference.child(roleOfUser).child(user.getUid()).child("name").setValue(name.getText().toString());
                    }
                    else if(name.getText().toString().equals(nm) && imgControl==true){
                        setProfilePicture();
                    }
                    else if((!name.getText().toString().equals(nm)) && imgControl==true){
                        dbreference.child(roleOfUser).child(user.getUid()).child("name").setValue(name.getText().toString());

                        //change profilePicture
                        setProfilePicture();
                    }
                    //Change name



                    Toast.makeText(UserProfile.this, "Data Changed Successfully", Toast.LENGTH_SHORT).show();
                    //Relaod the page
                    finish();
                    startActivity(getIntent());
                }

            }
        });


    }
    public static boolean isAlpha(String s) {
        return s.matches("^[a-zA-Z ]*$");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(userProfileImage);
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
                            dbreference.child(roleOfUser).child(user.getUid()).child("profilePictures").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UserProfile.this, "Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UserProfile.this, MainActivity2.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserProfile.this, "Fail", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfile.this, "this fail", Toast.LENGTH_SHORT).show();
                }
            });

        }

        else
        {
            dbreference.child("Student").child(auth.getCurrentUser().getUid()).child("profilePictures").setValue("null");
        }
    }
}