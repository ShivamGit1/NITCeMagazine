package com.example.nitcemagazine.MyArticles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitcemagazine.Comment.CommentAdapter;
import com.example.nitcemagazine.Comment.CommentModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewMyArticle extends AppCompatActivity {
    TextView articelTitle,articleDesc,comment;
    ImageView articleImageCard,downloadButton;
    Button addComment;
    RecyclerView commentRecyclerView;

//    ArrayList<String > imgfile = new ArrayList<>();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String id,status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_article);
        articelTitle = findViewById(R.id.TitleArticleView);
        articleDesc = findViewById(R.id.articleTextArticleView);
        articleImageCard = findViewById(R.id.articleImageArticleView);

        id = getIntent().getStringExtra("ArticleIdIntent");
        status = getIntent().getStringExtra("ArticleStatus");

        if(status.equalsIgnoreCase("posted")){
        reference.child("PostedArticle").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue().toString();
                String desc = snapshot.child("description").getValue().toString();
                String img = snapshot.child("ArticleImage").getValue().toString();


                articelTitle.setText(title);
                articleDesc.setText(desc);


                if (!img.equalsIgnoreCase("null")) {
                    Picasso.get().load(img).into(articleImageCard);
//                    imgfile.add(img);
                } else {
                    articleImageCard.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
        else {
            reference.child("Article").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = snapshot.child("title").getValue().toString();
                    String desc = snapshot.child("description").getValue().toString();
                    String img = snapshot.child("ArticleImage").getValue().toString();


                    articelTitle.setText(title);
                    articleDesc.setText(desc);


                    if (!img.equalsIgnoreCase("null")) {
                        Picasso.get().load(img).into(articleImageCard);
//                    imgfile.add(img);
                    } else {
                        articleImageCard.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}