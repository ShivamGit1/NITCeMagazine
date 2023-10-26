package com.example.nitcemagazine.DeleteArticle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DeleteArticle extends AppCompatActivity {

    RecyclerView unpostedArticle;
    DeleteArticleAdapter deleteArticleAdapter;
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
        setContentView(R.layout.activity_delete_article);


        unpostedArticle = findViewById(R.id.recyclerViewDeleteArticle);

        unpostedArticle.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        getArticle();
    }

    void getArticle()
    {
        reference.child("PostedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
                DatabaseReference ref = database.getReference();

                articleList.add(modelClass);
                modelClass.setId(snapshot.getKey());
                deleteArticleAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                modelClass = snapshot.getValue(ModelClass.class);
                DatabaseReference ref = database.getReference();

                if(!articleList.contains(modelClass)){
                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                }


                deleteArticleAdapter.notifyItemRemoved(0);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        deleteArticleAdapter = new DeleteArticleAdapter(articleList, DeleteArticle.this);
        unpostedArticle.setAdapter(deleteArticleAdapter);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeleteArticle.this, MainActivity2.class);
        startActivity(intent);
        finishAffinity();
        super.onBackPressed();
    }
}