package com.example.nitcemagazine.RejectedArticle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RejectedArticleViewArticle extends AppCompatActivity {

    ImageView articleImageCard,downloadButton;
    EditText articelTitle,articleDesc,comment;
    Button repost;
    String item[] = {"Select Category","Home","Educational","Technical","Sport","Fest"};
    ArrayAdapter<String > arrayAdapter;
    Spinner autoCompleteTextView;


//    ArrayList<String > imgfile = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String id;
    String title,desc,img,cat,categorySelected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_article_view_article);

        articelTitle = findViewById(R.id.TitleArticleView);
        articleDesc = findViewById(R.id.articleTextArticleView);
        articleImageCard = findViewById(R.id.articleImageArticleView);
        repost = findViewById(R.id.buttonRepost);
        autoCompleteTextView = findViewById(R.id.categoryRejectedArticle);

        arrayAdapter = new ArrayAdapter<String >(this,R.layout.category_drop_down_menu,item);
        autoCompleteTextView.setAdapter(arrayAdapter);


        id = getIntent().getStringExtra("ArticleIdIntent");

        reference.child("RejectedArticle").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 title = snapshot.child("title").getValue().toString();
                 desc = snapshot.child("description").getValue().toString();
                 img = snapshot.child("ArticleImage").getValue().toString();
                 cat = snapshot.child("category").getValue().toString();


                articelTitle.setText(title);
                articleDesc.setText(desc);

                if (!img.equalsIgnoreCase("null")) {
                    Picasso.get().load(img).into(articleImageCard);
//                    imgfile.add(img);
                }
                else
                {
                    articleImageCard.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categorySelected.equals("Select Category"))
                {
                    Toast.makeText(RejectedArticleViewArticle.this, "Please select the category", Toast.LENGTH_SHORT).show();
                }
                else {
                    HashMap<String, String> mp = new HashMap<>();
                    mp.put("ArticleImage", img);
                    mp.put("title", title);
                    mp.put("description", desc);
                    mp.put("authorUid", auth.getUid());
                    mp.put("Rating", "0");
                    mp.put("category", cat);

                    reference.child("Article").child(id).setValue(mp);

                    reference.child("Article").child(id).child("reviewCount").setValue(0);
                    reference.child("RejectedArticle").child(id).removeValue();

                    Intent intent = new Intent(RejectedArticleViewArticle.this,RejectedArticle.class);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RejectedArticleViewArticle.this,RejectedArticle.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}