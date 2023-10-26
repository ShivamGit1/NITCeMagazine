
package com.example.nitcemagazine.MainActivityPages;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nitcemagazine.LoginAndSignUp.LoginActivity;
import com.example.nitcemagazine.PostArticle.AddPostFragement;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
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

public class MainFragment extends Fragment {

    AlertDialog dialog;
    TabLayout tabLayout;
    TabItem home,sport,educational,fest,technical;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    Toolbar toolbar;
    FloatingActionButton addPost;

    EditText username,password;
    Button signin;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

//        toolbar = getView().findViewById(R.id.toolBar);
//        getActivity().setSupportActionBar(toolbar);
//
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        home = (TabItem) view.findViewById(R.id.home);
        sport = (TabItem) view.findViewById(R.id.sport);
        educational = (TabItem) view.findViewById(R.id.educational);
        fest = (TabItem) view.findViewById(R.id.fest);
        technical = (TabItem) view.findViewById(R.id.technical);

        addPost = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonAddPost);

        viewPager = (ViewPager) view.findViewById(R.id.fragment);

        pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), 5);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2 || tab.getPosition() == 3 || tab.getPosition() == 4 || tab.getPosition() == 5)
                {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null)
                {
//                    Intent intent = new Intent(getActivity(), AddPost.class);
//                    startActivity(intent);

                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.content, new AddPostFragement()).addToBackStack("home Page");
                    ft.commit();
                }

                else
                {
                    AlertDialog.Builder dialogLogin = new AlertDialog.Builder(getActivity());
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
                                Toast.makeText(getActivity(), "Please enter the Email id and Password", Toast.LENGTH_SHORT).show();
                            } else if (userEmailId.isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter a Email id", Toast.LENGTH_SHORT).show();

                            } else if (userPassword.isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter a password", Toast.LENGTH_SHORT).show();
                            } else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                                signInWithFirebase(userEmailId, userPassword);
                            } else {
                                Toast.makeText(getActivity(), "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

        return view;
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
                                Toast.makeText(getActivity(), "Sign in successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity2.class);
                                startActivity(intent);
                            }
                            else if(auth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(getActivity(), "Sign in successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity2.class);
                                startActivity(intent);
                            }
                            else {
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(getActivity(), "Please verify your email.", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getActivity(), "Invalid User", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(getActivity(), "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }
}