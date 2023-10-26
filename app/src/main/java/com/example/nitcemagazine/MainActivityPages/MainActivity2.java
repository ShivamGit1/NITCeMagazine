package com.example.nitcemagazine.MainActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.AddReviewerAndEditor.AddEditor;
import com.example.nitcemagazine.AddReviewerAndEditor.AddReviewer;
import com.example.nitcemagazine.AddReviewerAndEditor.DeleteReviewer;
import com.example.nitcemagazine.DeleteArticle.DeleteArticle;
import com.example.nitcemagazine.LoginAndSignUp.LoginActivity;
import com.example.nitcemagazine.LoginAndSignUp.UserProfile;
import com.example.nitcemagazine.MyArticles.MyArticle;
import com.example.nitcemagazine.PostArticle.AddPostFragement;
import com.example.nitcemagazine.R;
import com.example.nitcemagazine.LoginAndSignUp.SignUpPage;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticles;
import com.example.nitcemagazine.RejectedArticle.RejectedArticle;
import com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticles;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    AlertDialog dialog;
    NavigationView navigationView;
    ImageView navigationDrawerIcon;
    ActionBarDrawerToggle toggle;

    EditText username,password;
    Button signin;

    TextView emailId,role;
    CircleImageView profileProfilePicture;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    Menu menuView;

    String token;
    MenuItem signIn,signUp,logout,myarticle,rejectedArticle,changePassword,Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main2);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationDrawerIcon = findViewById(R.id.navigationDrawerIcon);

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            // Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        token = task.getResult();
//                        reference.child("Tokens").push().setValue(token);
//                        System.out.println("*****************"+token);
//                    }
//                });

        View view = navigationView.getHeaderView(0);


        menuView = navigationView.getMenu();



        emailId = view.findViewById(R.id.textViewEmailNavDrawer);
        role = view.findViewById(R.id.textViewRoleNavDrawer);
        profileProfilePicture = view.findViewById(R.id.profilePictureNavDraver);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content, new MainFragment());
        ft.commit();


        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        headerDetails();

        navigationDrawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

//        System.out.println(user.getUid());


        findRole();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.signInNavDrawer)
                {
                    Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                    startActivity(intent);
                } else if (id == R.id.signUpNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, SignUpPage.class);
                    startActivity(intent);
                } else if (id == R.id.addPostNavDrawer) {
                    if(user!= null)
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                        ft.commit();
                    }
                    else {
                        AlertDialog.Builder dialogLogin = new AlertDialog.Builder(MainActivity2.this);
                        View loginView = getLayoutInflater().inflate(R.layout.dialog_login,null);

                        username = loginView.findViewById(R.id.editTextDialogUsername);
                        password = loginView.findViewById(R.id.editTextDialogPassword);
                        signin = loginView.findViewById(R.id.buttonDialogLogin);
                        dialogLogin.setView(loginView);
                        dialog = dialogLogin.create();
                        dialog.show();
                        signin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userEmailId = username.getText().toString();
                                String userPassword = password.getText().toString();

                                if (userEmailId.isEmpty() && userPassword.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter the Email id and Password", Toast.LENGTH_SHORT).show();
                                } else if (userEmailId.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();

                                } else if (userPassword.isEmpty()) {
                                    Toast.makeText(MainActivity2.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                                } else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                                    signInWithFirebase(userEmailId, userPassword);
                                } else {
                                    Toast.makeText(MainActivity2.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else if (id == R.id.addReviewerNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, AddReviewer.class);
                    startActivity(intent);

                }
                else if (id == R.id.addEditorNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, AddEditor.class);
                    startActivity(intent);

                }else if (id == R.id.logoutNavDrawer){
                    auth.signOut();
                    Intent intent = new Intent(MainActivity2.this,MainActivity2.class);
                    startActivity(intent);
                    finishAffinity();
                } else if (id == R.id.postArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, PostUnpostedArticles.class);
                    startActivity(intent);
                } else if (id == R.id.reviewArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, ReviewUnpostedArticles.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.deleteArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, DeleteArticle.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.removeReviewerNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, DeleteReviewer.class);
                    startActivity(intent);
                } else if (id == R.id.changePasswordNavDrawer) {
                    updatePassword();
                } else if (id == R.id.userProfileNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, UserProfile.class);
                    startActivity(intent);
                }
                else if (id == R.id.myArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, MyArticle.class);
                    startActivity(intent);
                } else if (id == R.id.rejectedArticleNavDrawer) {
                    Intent intent = new Intent(MainActivity2.this, RejectedArticle.class);
                    startActivity(intent);
                }
                else if(id ==R.id.contact){
                    String email = "enitc10@gmail.com";
                    String[] recipients = new String[]{email};
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    startActivity(Intent.createChooser(intent, "Choose Email Client"));
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    void headerDetails()
    {
        if(user != null) {

            reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String roleOfUser = snapshot.getValue().toString();

                    DatabaseReference ref = database.getReference();

                    ref.child(roleOfUser).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            emailId.setText(snapshot1.child("email").getValue().toString());
                            role.setText(snapshot1.child("role").getValue().toString());
                            String profilePicture = snapshot1.child("profilePictures").getValue().toString();
                            System.out.println(profilePicture);
                            if(profilePicture.equals("null") )
                            {
                                profileProfilePicture.setImageResource(R.drawable.img_1);
                            }
                            else
                            {
                                Picasso.get().load(profilePicture).into(profileProfilePicture);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            auth.signOut();
        }

        else {
            profileProfilePicture.setImageResource(R.drawable.img_1);
            emailId.setText("");
            role.setText("Guest");
        }
    }



    private void inflateMenu(char userRole) {
        if(userRole == 'S')
        {
            navigationView.inflateMenu(R.menu.navigation_item_user);
        }
        else if(userRole == 'A')
        {
            navigationView.inflateMenu(R.menu.navigation_item_admin);
        } else if (userRole == 'R') {
            navigationView.inflateMenu(R.menu.navigation_item_reviewer);
        } else if (userRole == 'E') {
            navigationView.inflateMenu(R.menu.navigation_item_editor);
        }

        setMenu();
    }

    void findRole()
    {

        if(user != null) {
            char[] userRole = new char[1];
            reference.child("UserType").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userRole[0] = snapshot.getValue().toString().charAt(0);
                    inflateMenu(userRole[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
            inflateMenu('S');
    }

    void setMenu()
    {
        signIn = menuView.findItem(R.id.signInNavDrawer);
        signUp = menuView.findItem(R.id.signUpNavDrawer);
        logout = menuView.findItem(R.id.logoutNavDrawer);
        myarticle = menuView.findItem(R.id.myArticleNavDrawer);
        rejectedArticle = menuView.findItem(R.id.rejectedArticleNavDrawer);
        changePassword = menuView.findItem(R.id.changePasswordNavDrawer);
        Profile =menuView.findItem(R.id.userProfileNavDrawer);
        if(user != null) {
            signIn.setVisible(false);
            signUp.setVisible(false);
            logout.setVisible(true);
        }
        else {
            signIn.setVisible(true);
            signUp.setVisible(true);
            logout.setVisible(false);
            myarticle.setVisible(false);
            rejectedArticle.setVisible(false);
            changePassword.setVisible(false);
            Profile.setVisible(false);
        }
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
                                Toast.makeText(MainActivity2.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                                ft.commit();
                            }
                            else if(auth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(MainActivity2.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.content, new AddPostFragement()).addToBackStack("add post");
                                ft.commit();
                            }
                            else {
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(MainActivity2.this, "Please verify your email.", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(MainActivity2.this, "Invalid User", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(MainActivity2.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    void updatePassword()
    {
        View changePasswodView = LayoutInflater.from(MainActivity2.this).inflate(R.layout.change_password_dialog_box,null);

        EditText oldPassword = changePasswodView.findViewById(R.id.oldPassword);
        EditText newPassword = changePasswodView.findViewById(R.id.newPassword);
        EditText confirmPassword = changePasswodView.findViewById(R.id.confirmPassword);

        Button changePassword = changePasswodView.findViewById(R.id.ChangePassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setView(changePasswodView);
        AlertDialog dialog1 =  builder.create();
        dialog1.show();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPass = oldPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if(oldPass.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "please enter your old password", Toast.LENGTH_SHORT).show();
                } else if (newPass.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "please enter a new password", Toast.LENGTH_SHORT).show();
                } else if (confirmPass.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "please enter confirm password", Toast.LENGTH_SHORT).show();
                } else if (!newPass.equals(confirmPass)) {
                    Toast.makeText(MainActivity2.this, "password and confirm password does not match", Toast.LENGTH_SHORT).show();
                } else {
                    dialog1.dismiss();
                    changeOldPassword(oldPass,newPass);
                }

            }
        });
    }

    void changeOldPassword(String oldPass, String newPass)
    {
        FirebaseUser user1 = auth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user1.getEmail(),oldPass);

        user1.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user1.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "Password Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Password not updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    protected void onStart() {

        FirebaseAuth auth1 = FirebaseAuth.getInstance();
        FirebaseUser user1 = auth1.getCurrentUser();
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = database1.getReference();
        ArrayList<String> ar = new ArrayList<>();
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        SimpleDateFormat dtf1 = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
//        String time = sdf.format(timeStamp);
        if (user1 != null) {
            reference1.child("RejectedArticle").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("authorUid").getValue().toString().equals(user1.getUid())) {
                            Long time = (Long) ds.child("DateTime").getValue();
                            try {
                                String date1 = sdf.format(time);
                                System.out.println(date1);
                                Long timeStamp = new Date().getTime();
                                String dt2 = sdf.format(timeStamp);


                                Date date = dtf.parse(date1);
                                Date date2 = dtf.parse(dt2);
                                long diff = date2.getTime() - date.getTime();
                                int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.DAYS);
                                System.out.println("min: " + TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS));

                                if (day >= 7) {
                                    reference.child("RejectedArticle").child(ds.getKey()).removeValue();
                                }

//                                String date1m = sdf1.format(time);
////                                System.out.println(date1);
//                                Long timeStampm = new Date().getTime();
//                                String dt2m = sdf1.format(timeStampm);
////
//                                Date datem = dtf1.parse(date1m);
//                                Date date2m = dtf1.parse(dt2m);
//                                long diff1 = date2m.getTime() - datem.getTime();
//                                int daym = (int) TimeUnit.DAYS.convert(diff1, TimeUnit.MINUTES);
//                                System.out.println("min: " + TimeUnit.MINUTES.convert(diff1, TimeUnit.MINUTES));
//
//                                if (daym >= 2) {
//                                    reference.child("RejectedArticle").child(ds.getKey()).removeValue();
//                                }


                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}