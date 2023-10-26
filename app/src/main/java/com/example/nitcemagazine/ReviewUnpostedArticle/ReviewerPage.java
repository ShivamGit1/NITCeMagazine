package com.example.nitcemagazine.ReviewUnpostedArticle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewerPage extends AppCompatActivity {


    ImageView articleImage;
    TextView articletitle;
    TextView description;
    RatingBar ratingBar;
    Button submitButton;

    String ArticleId;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbreference = database.getReference();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    Long reviewCount= Long.valueOf(0);
    Long reviewCnt;
    float prevRating=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer_page);
        articleImage=(ImageView) findViewById(R.id.articleImage);
        articletitle=(TextView) findViewById(R.id.Title);
        description=(TextView) findViewById(R.id.articleText);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        submitButton=(Button)findViewById(R.id.submitRating);

//        dbreference= FirebaseDatabase.getInstance().getReference();

        ArticleId = getIntent().getStringExtra("ArticleIdIntent");

        dbreference.child("Article").child(ArticleId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                System.out.println(ArticleId);
                String imageUrl = snapshot.child("ArticleImage").getValue(String.class);
                String rating = snapshot.child("Rating").getValue().toString();
                prevRating=Float.parseFloat(rating);
                String content = snapshot.child("description").getValue(String.class);
                String title = snapshot.child("title").getValue(String.class);
                System.out.println(title);
                reviewCnt =(Long) snapshot.child("reviewCount").getValue();
//                reviewCount =Integer.parseInt(reviewCnt);

                //set Values into Views
                Picasso.get().load(imageUrl).into(articleImage);
                articletitle.setText(title);
                description.setText(content);
                ratingBar.setRating(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewerPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the current rating from the rating bar
                float currentRating = ratingBar.getRating();

                // calculate the new average rating
                float newRating = ((currentRating + (reviewCount * prevRating)) / (reviewCount + 1));

                // update the review count
                dbreference.child("Article").child(ArticleId).child("reviewCount").setValue(reviewCnt + 1);

                // update the review rating
                dbreference.child("Article").child(ArticleId).child("Rating").setValue(newRating);

                // show a toast message to indicate that the review has been submitted
                Toast.makeText(ReviewerPage.this, "Review submitted", Toast.LENGTH_SHORT).show();

                //Add user in reviewerList in Review Table
                String Reviewer_id=user.getUid().toString();
                DatabaseReference reviewers_ref=dbreference.child("Review").child(ArticleId).child("reviewers");
                reviewers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String>  l=new ArrayList<String>();
                        for (DataSnapshot childsnapshot:snapshot.getChildren()){
                            String r_id=childsnapshot.getValue().toString();
                            l.add(r_id);
                        }
                        l.add(Reviewer_id);
                        reviewers_ref.setValue(l).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(ReviewerPage.this,ReviewUnpostedArticles.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                /*reviewers_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String>  l=new ArrayList<String>();
                        for (DataSnapshot childsnapshot:snapshot.getChildren()){
                            String r_id=childsnapshot.getValue().toString();
                            l.add(r_id);
                        }
                        l.add(Reviewer_id);
                        reviewers_ref.setValue(l);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dbreference.child("Review").child(ArticleId).child("reviewers");*/

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReviewerPage.this,ReviewUnpostedArticles.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}