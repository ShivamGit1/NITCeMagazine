package com.example.nitcemagazine.AddReviewerAndEditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeleteReviewer extends AppCompatActivity {

    EditText student_id;
    Button addReviewer,getUser;
    CircleImageView pic;
    TextView nameTest,roleTest;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //    DatabaseReference dbreference = database.getReference();
    DatabaseReference studentRef;
    ConstraintLayout cl;
    ArrayList<String > stdList = new ArrayList<>();
    ArrayList<String> stdRole = new ArrayList<>();
    ArrayList<String > currentEditor = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_reviewer);
        getUser = findViewById(R.id.get_user);
        addReviewer=(Button) findViewById(R.id.buttom_add_editor);
        student_id=(EditText) findViewById(R.id.input_Email_add_editor);
        nameTest = findViewById(R.id.textView5);
        roleTest = findViewById(R.id.textView6);
        pic = findViewById(R.id.circleImageView);
        cl = findViewById(R.id.frmelayout_user_details);

        cl.setVisibility(View.INVISIBLE);

        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentRef = database.getReference();
                DatabaseReference ref1 = database.getReference();
                String student_email = student_id.getText().toString();

                studentRef.child("UserType").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            String roleOfUser = ds.getValue().toString();

//                            toStringref1.child()

                            ref1.child(roleOfUser).child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    System.out.println(roleOfUser);
                                    try {
                                        if (snapshot.child("email").getValue().toString().equalsIgnoreCase(student_email)) {
                                            if (!stdList.contains(ds.getKey())) {
                                                stdList.add(ds.getKey());
                                                stdRole.add(ds.getValue().toString());

                                                cl.setVisibility(View.VISIBLE);

                                                nameTest.setText(snapshot.child("name").getValue().toString());
                                                roleTest.setText(roleOfUser);
                                                String img = snapshot.child("profilePictures").getValue().toString();
                                                if (!img.equalsIgnoreCase("null")) {
                                                    Picasso.get().load(img).into(pic);
                                                }
                                                else
                                                {
                                                    pic.setImageResource(R.drawable.baseline_account_circle_24);
//                                                    Picasso.get().load(R.drawable.baseline_account_circle_24).into(pic);
                                                }


//                                                FragmentManager fm = getSupportFragmentManager();
//                                                FragmentTransaction ft = fm.beginTransaction();
//                                                ft.add(R.id.frmelayout_user_details, new userFragment());
//                                                ft.commit();

//                                                FragmentManager fm = getSupportFragmentManager();
//                                                FragmentTransaction ft = fm.beginTransaction();
//                                                ft.add(R.id.frmelayout_user_details, new userFragment());
//                                                ft.commit();
                                            }

                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        System.out.println(e);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        addReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getValues of given email
//                System.out.println(stdList.size());

                if(stdRole.get(0).equalsIgnoreCase("student"))
                {
                    Toast.makeText(DeleteReviewer.this, "Not a Reviewer", Toast.LENGTH_SHORT).show();

                } else if (stdRole.get(0).equalsIgnoreCase("Reviewer")) {

                    DatabaseReference ref = database.getReference();

                    ref.child("Reviewer").child(stdList.get(0)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                String email = snapshot.child("email").getValue().toString();
                                String name = snapshot.child("name").getValue().toString();
                                String profilePictures = snapshot.child("profilePictures").getValue().toString();

                                HashMap<String, String> mp = new HashMap<>();
                                mp.put("name", name);
                                mp.put("profilePictures", profilePictures);
                                mp.put("email", email);
                                mp.put("role", "Student");

                                DatabaseReference ref1 = database.getReference();


                                ref1.child("Student").child(stdList.get(0)).setValue(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DatabaseReference ref2 = database.getReference();
                                        ref2.child("UserType").child(stdList.get(0)).setValue("Student").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                ref2.child("Reviewer").child(stdList.get(0)).removeValue();
                                            }
                                        });
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                System.out.println();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(DeleteReviewer.this, "Removed a reviewer", Toast.LENGTH_SHORT).show();
                }
                else if (stdRole.get(0).equalsIgnoreCase("Editor")) {
                    Toast.makeText(DeleteReviewer.this, "Already a Editor", Toast.LENGTH_SHORT).show();
                }
                else if (stdRole.get(0).equalsIgnoreCase("Admin")) {
                    Toast.makeText(DeleteReviewer.this, "Already a Admin", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}