package com.example.nitcemagazine.ReviewUnpostedArticle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticleAdapter;
import com.example.nitcemagazine.PostUnpostedArticle.PostUnpostedArticles;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewUnpostedArticles extends AppCompatActivity {

    int i = 0;
    RecyclerView unpostedArticle;
    ReviewUnpostedArticleAdapter reviewUnpostedArticleAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    List<ModelClass> articleList;

    ArrayList<String> articleListFromReview;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    ArrayList<String> unReviewedArticle=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_unposted_articles);

        unpostedArticle = findViewById(R.id.recyclerViewReviewUnpostedArticle);

        unpostedArticle.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        //getArticle();
        //Yaha se add kiya/*


        String uid=user.getUid().toString();
        DatabaseReference ref=reference.child("Review");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("review "+i++);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){

                    String artId = childSnapshot.child("articleid").getValue().toString();
                    List<String> reviewers = childSnapshot.child("reviewers").getValue(new GenericTypeIndicator<List<String>>() {});

                    if(reviewers == null || !reviewers.contains(uid)){
                        unReviewedArticle.add(artId);
                        System.out.println("unreviewed article "+i++);
                    }
                }
                System.out.println(unReviewedArticle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //yaha tak
        getArticle();
    }

    void getArticle()
    {

//        reference.child("Article").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                modelClass = snapshot.getValue(ModelClass.class);
////                articleList.add(modelClass);
////                modelClass.setId(snapshot.getKey());
////                reviewUnpostedArticleAdapter.notifyDataSetChanged();
//
//                String key=snapshot.getKey();
//                System.out.println("xyz "+i++);
//
//                if(unReviewedArticle.contains(key)){
//                    modelClass = snapshot.getValue(ModelClass.class);
//                    articleList.add(modelClass);
//                    modelClass.setId(snapshot.getKey());
//                    reviewUnpostedArticleAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    //yaha se
        reference.child("Article").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    String key=childSnapshot.getKey();
                    System.out.println(key);
                    if(unReviewedArticle.contains(key)){
                        modelClass = childSnapshot.getValue(ModelClass.class);
                        articleList.add(modelClass);
                        modelClass.setId(childSnapshot.getKey());
                        reviewUnpostedArticleAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //yaha tak

        reviewUnpostedArticleAdapter= new ReviewUnpostedArticleAdapter(articleList, ReviewUnpostedArticles.this);
        unpostedArticle.setAdapter(reviewUnpostedArticleAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReviewUnpostedArticles.this, MainActivity2.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}