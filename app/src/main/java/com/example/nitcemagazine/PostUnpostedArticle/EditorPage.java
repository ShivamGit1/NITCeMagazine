package com.example.nitcemagazine.PostUnpostedArticle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.Notification.FcmNotificationsSender;
import com.example.nitcemagazine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditorPage extends AppCompatActivity {

    ImageView articleImage;
    TextView articletitle;
    TextView description;
    RatingBar ratingBar;
    TextView ratingSummary;
    Button reject;
    Button post;
    String CHANNEL_ID = "1";

    String ArticleId;
    DatabaseReference dbreference;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    String imageUrl = "";
    String author;
    String category;
    int reviewCount = 0;
    Float prevRating;

    String mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_page);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

//
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//                        System.out.println("*****************"+token);
//                    }
//                });
        articleImage = (ImageView) findViewById(R.id.articleImage);
        articletitle = (TextView) findViewById(R.id.Title);
        description = (TextView) findViewById(R.id.articleText);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingSummary = (TextView) findViewById(R.id.rating);
        reject = (Button) findViewById(R.id.rejectPost);
        post = (Button) findViewById(R.id.postArticle);


        dbreference = FirebaseDatabase.getInstance().getReference();

        ArticleId = getIntent().getStringExtra("ArticleIdIntent");

        dbreference.child("Article").child(ArticleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get Values from database
                try {
                    imageUrl = snapshot.child("ArticleImage").getValue().toString();
                    prevRating = Float.parseFloat(snapshot.child("Rating").getValue().toString());
                    String content = snapshot.child("description").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    author = snapshot.child("authorUid").getValue(String.class);
                    reviewCount = Integer.parseInt(snapshot.child("reviewCount").getValue().toString());
                    category = snapshot.child("category").getValue().toString();

                    //set Values into Views
                    //if(imageUrl !=null)
                    //Toast.makeText(EditorPage.this, imageUrl.length(), Toast.LENGTH_SHORT).show();
                    if (imageUrl.equals("null")) {
                        articleImage.setMinimumHeight(0);
                        articleImage.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                    } else {
                        Picasso.get().load(imageUrl).into(articleImage);
                    }


                    articletitle.setText(title);
                    description.setText(content);
                    ratingBar.setRating(prevRating);
                    ratingBar.setEnabled(false);
                    double r = Math.round(prevRating * 10.0) / 10.0;
                    String sum = r + "/5 reviewed by " + reviewCount + " reviewers";
                    ratingSummary.setText(sum);
                } catch (Exception E) {
                    System.out.println();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbreference.child("Article").child(ArticleId).removeValue();
                View rejectArticle = LayoutInflater.from(EditorPage.this).inflate(R.layout.post_rejection_dialog_box, null);

                EditText msg = rejectArticle.findViewById(R.id.editTextEmailContent);
                Button ok = rejectArticle.findViewById(R.id.okButton);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditorPage.this);
                builder.setView(rejectArticle);
                AlertDialog dialog1 = builder.create();
                dialog1.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mesg="Hi,\nWe are sorry to inform you that the post requested by you '"+articletitle.getText()+"' has been rejected due to following reason:\n";
                        String reason=msg.getText().toString();
                        if(!reason.isEmpty()) {
                            //
                            Long timeStamp = new Date().getTime();
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", articletitle.getText());
                            map.put("description", description.getText());
                            map.put("ArticleImage", imageUrl);
                            map.put("authorUid", author);
                            map.put("category", category);
                            map.put("Rating", prevRating);
                            map.put("reviewCount", reviewCount);
                            map.put("Reason", reason);
                            map.put("DateTime", timeStamp);
                            dbreference.child("RejectedArticle").child(ArticleId)
                                    .setValue(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditorPage.this, "Article Rejected Successfully", Toast.LENGTH_SHORT).show();
                                            //delete from Articles Table
                                            dbreference.child("Article").child(ArticleId).removeValue();
                                            //Go somewhere
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditorPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //Send Email
                            mesg = mesg + reason+"\nIf you don't repost the article within 15 days your article will be permanently deleted.\n\nThanks and Regards,\nNITC_E_MAGAZINE.";
                            dbreference.child("UserType").child(author).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String roleOfUser = snapshot.getValue().toString();

                                    dbreference.child(roleOfUser).child(author).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            String email = snapshot.child("email").getValue().toString();
                                            String[] recipients = new String[]{email};
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/html");
                                            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Your Post has been Rejected");
                                            intent.putExtra(Intent.EXTRA_TEXT, mesg);
                                            startActivity(Intent.createChooser(intent, "Choose Email Client"));

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditorPage.this, "Give reason", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("title", articletitle.getText());
                map.put("description", description.getText());
                map.put("ArticleImage", imageUrl);
                map.put("authorUid", author);
                map.put("category", category);
                map.put("Rating", prevRating);
                map.put("reviewCount", reviewCount);

                //insert article in PostedArticleTable
                dbreference.child("PostedArticle").child(ArticleId)
                        .setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditorPage.this, "Article Posted Successfully", Toast.LENGTH_SHORT).show();
                                //delete from Articles Table
                                dbreference.child("Article").child(ArticleId).removeValue();
                                //delete from review Table
                                dbreference.child("Review").child(ArticleId).removeValue();

                                //Enter Empty Liker List in Like Table
                                ArrayList<String> lk=new ArrayList<String>();
                                DatabaseReference ref=dbreference.child("Like").child(ArticleId);
                                Map<String,Object> map=new HashMap<>();
                                map.put("articleid",ArticleId);
                                map.put("likers",lk);
                                ref.setValue(map);


//Notification new

                                dbreference.child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren())
                                        {
                                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(ds.getValue().toString(),"hello","bye",getApplicationContext(),EditorPage.this);
                                            notificationsSender.SendNotifications();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
//                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("eInz2ebPSXaDOzQycLU-uq:APA91bF9Y4We87b6MEY4o9H-qYdbPe9m9Vj8Ka_aRJQ5UrwdROHbKSPHYIlLyQa0GhfPBKTOivaA-Ajfu_QQU3SkQZD6v6j2Ad_fO19GLJnn4fI52e4WzsV_bMBsF6i4jmTQttu0pJdF","hello","bye",getApplicationContext(),EditorPage.this);
//
//                                notificationsSender.SendNotifications();



//Notification end
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditorPage.this, "error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                //Send Email
                dbreference.child("UserType").child(author).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String roleOfUser = snapshot.getValue().toString();

                        dbreference.child(roleOfUser).child(author).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String email = snapshot.child("email").getValue().toString();
                                String[] recipients = new String[]{email};
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/html");
                                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Your Post has been posted");
                                String msg="Hi,\n This email is to inform you that the post requested by you '"+articletitle.getText()+"' has been posted on the basis on good reviews.Now you can check your post on our Article page by logging into the app.\n\n Thanks and Regards,\nNITC_E_MAGAZINE.";
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                startActivity(Intent.createChooser(intent, "Choose Email Client"));

//                                Intent intents = new Intent(EditorPage.this, PostUnpostedArticles.class);
//                                startActivity(intents);
//                                finish();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditorPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                //Go to Unposted Articles page
            }

        });


    }

    public void pushNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "1", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);


            Notification.Builder builder = new Notification.Builder(EditorPage.this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.arrows_10_generated)
                    .setContentTitle("title")
                    .setContentText("hello").setPriority(Notification.PRIORITY_DEFAULT);

            NotificationManagerCompat compat = NotificationManagerCompat.from(EditorPage.this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            compat.notify(1, builder.build());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditorPage.this,PostUnpostedArticles.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

//    @Override
//    protected void onPostResume() {
//        Intent intent = new Intent(EditorPage.this,PostUnpostedArticles.class);
//        startActivity(intent);
//        finish();
//        super.onPostResume();
//    }

//    @Override
//    protected void onRestart() {
//        Intent intent = new Intent(EditorPage.this,PostUnpostedArticles.class);
//        startActivity(intent);
//        finish();
//        super.onResume();
//    }

    @Override
    protected void onRestart() {
        Intent intent = new Intent(EditorPage.this,PostUnpostedArticles.class);
        startActivity(intent);
        finish();
        super.onRestart();

    }


}