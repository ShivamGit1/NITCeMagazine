package com.example.nitcemagazine.RejectedArticle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RejectedArticle extends AppCompatActivity {

    List<ModelClass> articleList;
    RejectedArticleAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_article);

        recyclerView = findViewById(R.id.recyclerViewRejectedArticle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        getArticle();
    }

    void getArticle() {
        reference.child("RejectedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    modelClass = snapshot.getValue(ModelClass.class);
                    if (snapshot.child("authorUid").getValue().toString().equalsIgnoreCase(user.getUid())) {
                        articleList.add(modelClass);
                        modelClass.setId(snapshot.getKey());
                        System.out.println("hii " + modelClass.getId());

                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new RejectedArticleAdapter(articleList, RejectedArticle.this);
        recyclerView.setAdapter(adapter);
    }


}
