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

public class AddEditor extends AppCompatActivity {
    EditText student_id;
    Button addEditor,getUser;
    CircleImageView pic;
    TextView nameTest,roleTest;
    ConstraintLayout cl;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //    DatabaseReference dbreference = database.getReference();
    DatabaseReference studentRef;
    ArrayList<String > stdList = new ArrayList<>();
    ArrayList<String> stdRole = new ArrayList<>();
    ArrayList<String > currentEditor = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_editor);
        getUser = findViewById(R.id.get_user);
        addEditor=(Button) findViewById(R.id.buttom_add_editor);
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
                            String id = ds.getKey();

                            if(roleOfUser.equalsIgnoreCase("editor"))
                            {
                                System.out.println("**********");
                                if(!currentEditor.contains(id))
                                {
                                    currentEditor.add(id);
                                }
                            }

                            ref1.child(roleOfUser).child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    System.out.println(roleOfUser);
                                    try {
                                        if (snapshot.child("email").getValue().toString().equalsIgnoreCase(student_email)) {
                                            if (!stdList.contains(ds.getKey())) {
                                                stdList.add(ds.getKey());
                                                stdRole.add(roleOfUser);

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
                                            }

                                        }


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
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        addEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getValues of given email
//                System.out.println(stdList.size());

                if(stdRole.get(0).equalsIgnoreCase("student"))
                {
                    Toast.makeText(AddEditor.this, "Successfully added a Editor", Toast.LENGTH_SHORT).show();
                    DatabaseReference ref = database.getReference();

                    if(currentEditor.size() >= 1)
                    {
                        System.out.println("++++++++++++++++++");
                        ref.child("Editor").child(currentEditor.get(0)).addValueEventListener(new ValueEventListener() {
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


                                    ref1.child("Student").child(currentEditor.get(0)).setValue(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            DatabaseReference ref2 = database.getReference();
                                            ref2.child("UserType").child(currentEditor.get(0)).setValue("Student").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    ref2.child("Editor").child(currentEditor.get(0)).removeValue();
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
                    }

                    ref.child("Student").child(stdList.get(0)).addValueEventListener(new ValueEventListener() {
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
                                mp.put("role", "Editor");

                                DatabaseReference ref1 = database.getReference();


                                ref1.child("Editor").child(stdList.get(0)).setValue(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DatabaseReference ref2 = database.getReference();
                                        ref2.child("UserType").child(stdList.get(0)).setValue("Editor").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                ref2.child("Student").child(stdList.get(0)).removeValue();
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

                } else if (stdRole.get(0).equalsIgnoreCase("Reviewer")) {
                    Toast.makeText(AddEditor.this, "Successfully added a Editor", Toast.LENGTH_SHORT).show();
                    DatabaseReference ref = database.getReference();


                    if(currentEditor.size() >= 1)
                    {
                        System.out.println("++++++++++++++++++");
                        ref.child("Editor").child(currentEditor.get(0)).addValueEventListener(new ValueEventListener() {
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


                                    ref1.child("Student").child(currentEditor.get(0)).setValue(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            DatabaseReference ref2 = database.getReference();
                                            ref2.child("UserType").child(currentEditor.get(0)).setValue("Student").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    ref2.child("Editor").child(currentEditor.get(0)).removeValue();
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
                    }


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
                                mp.put("role", "Editor");

                                DatabaseReference ref1 = database.getReference();


                                ref1.child("Editor").child(stdList.get(0)).setValue(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DatabaseReference ref2 = database.getReference();
                                        ref2.child("UserType").child(stdList.get(0)).setValue("Editor").addOnSuccessListener(new OnSuccessListener<Void>() {
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
                }
                else if (stdRole.get(0).equalsIgnoreCase("Editor")) {
                    Toast.makeText(AddEditor.this, "Already a Editor", Toast.LENGTH_SHORT).show();
                }
                else if (stdRole.get(0).equalsIgnoreCase("Admin")) {
                    Toast.makeText(AddEditor.this, "Already a Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}