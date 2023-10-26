package com.example.nitcemagazine.PostUnpostedArticle;

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

import java.util.ArrayList;
import java.util.List;

public class PostUnpostedArticles extends AppCompatActivity {

    RecyclerView unpostedArticle;
    PostUnpostedArticleAdapter postUnpostedArticleAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    List<ModelClass> articleList = new ArrayList<>();
    List<ModelClass> articleList2 = new ArrayList<>();
    List<String > reviewCount;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unposted_articles);

        unpostedArticle = findViewById(R.id.recyclerViewUnpostedArticle);

        unpostedArticle.setLayoutManager(new LinearLayoutManager(this));


        getArticle();
    }

    void getArticle()
    {


        reference.child("Article").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
                DatabaseReference ref = database.getReference();
                if((Long)snapshot.child("reviewCount").getValue()>=1) {
                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                    postUnpostedArticleAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                modelClass = snapshot.getValue(ModelClass.class);
                if(!articleList.contains(modelClass) ) {
                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());

                }
                postUnpostedArticleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postUnpostedArticleAdapter = new PostUnpostedArticleAdapter(articleList, PostUnpostedArticles.this);
        unpostedArticle.setAdapter(postUnpostedArticleAdapter);
    }

}