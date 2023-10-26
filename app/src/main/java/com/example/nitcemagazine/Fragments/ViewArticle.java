package com.example.nitcemagazine.Fragments;

import static android.os.Environment.DIRECTORY_ALARMS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitcemagazine.Comment.CommentAdapter;
import com.example.nitcemagazine.Comment.CommentDetailClass;
import com.example.nitcemagazine.Comment.CommentModelClass;
import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewArticle extends AppCompatActivity {
    TextView articelTitle,articleDesc,comment;
    ImageView articleImageCard,downloadButton,nav;
    Button addComment;
    RecyclerView commentRecyclerView;
    AlertDialog dialog;

    ArrayList <String > imgfile = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user= auth.getCurrentUser();
    private static final int STORAGE_CODE = 1000;

    List<CommentModelClass> commentList;
    CommentAdapter adapter;
    CommentModelClass commentModelClass = new CommentModelClass();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String id,authorName;
    EditText username,password;
    Button signin;

    int count;

    Button like;
    TextView likeno;

    Bitmap bmp, scaledBmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);

        like=findViewById(R.id.likeButton);
        likeno=findViewById(R.id.likeno);

        articelTitle = findViewById(R.id.TitleArticleView);
        articleDesc = findViewById(R.id.articleTextArticleView);
        articleImageCard = findViewById(R.id.articleImageArticleView);
        comment = findViewById(R.id.commentArticleView);
        addComment = findViewById(R.id.buttonAddCommentArticleView);
        commentRecyclerView = findViewById(R.id.recyclerViewArticleView);
        downloadButton = findViewById(R.id.downloadButton);
        nav = findViewById(R.id.navigationDrawerIcon);


        downloadButton.setVisibility(View.VISIBLE);
        nav.setVisibility(View.GONE);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();


        id = getIntent().getStringExtra("ArticleIdIntent");
        authorName = getIntent().getStringExtra("AuthorName");


        getComment();

        //get article info and set like button and like nos
        DatabaseReference likers_ref=reference.child("Like").child(id).child("likers");
        likers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String>  l=new ArrayList<String>();
                for (DataSnapshot childsnapshot:snapshot.getChildren()){
                    String r_id=childsnapshot.getValue().toString();
                    l.add(r_id);
                }
                count=l.size();
                likeno.setText(count+" likes");
                if(user==null){
                    like.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                    like.setEnabled(false);
                }
                else {
                    if (l.contains(user.getUid())) {
                        like.setBackgroundResource(R.drawable.baseline_favorite_24);
                    } else {
                        like.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                like.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                likeno.setText(0+" likes");
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String>  l=new ArrayList<String>();
                        for (DataSnapshot childsnapshot:snapshot.getChildren()){
                            String r_id=childsnapshot.getValue().toString();
                            l.add(r_id);
                        }
                        if(l.contains(user.getUid())){
                            l.remove(user.getUid());
                            count=l.size();
                            likeno.setText(count+" likes");
                            likers_ref.setValue(l);
                            like.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                        }
                        else{
                            l.add(user.getUid());
                            count=l.size();
                            likeno.setText(count+" likes");
                            likers_ref.setValue(l);
                            like.setBackgroundResource(R.drawable.baseline_favorite_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    System.out.println(imgfile.get(0));
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, STORAGE_CODE);
                        } else {
                            try {
                                createPdf();
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(ViewArticle.this, e+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    imgfile.add(img);
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


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = auth.getCurrentUser();
                if(user != null) {
                    String commentText = comment.getText().toString();
                    if(commentText.length() != 0) {

                        DatabaseReference ref = database.getReference();

                        String commentKey = ref.child("ArticleComment").child(id).push().getKey();
                        CommentDetailClass commentDetailClass = new CommentDetailClass(commentText, auth.getCurrentUser().getUid(), id);
                        ref.child("ArticleComment").child(id).child(commentKey).setValue(commentDetailClass);

                        comment.setText("");
                    }
                }
                else {
                    AlertDialog.Builder dialogLogin = new AlertDialog.Builder(ViewArticle.this);
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
                                Toast.makeText(ViewArticle.this, "Please enter the Email id and Password", Toast.LENGTH_SHORT).show();
                            } else if (userEmailId.isEmpty()) {
                                Toast.makeText(ViewArticle.this, "Please enter a Email id", Toast.LENGTH_SHORT).show();

                            } else if (userPassword.isEmpty()) {
                                Toast.makeText(ViewArticle.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                            } else if (Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()) {
                                signInWithFirebase(userEmailId, userPassword);
                            } else {
                                Toast.makeText(ViewArticle.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void getComment() {
        reference.child("ArticleComment").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                commentModelClass = snapshot.getValue(CommentModelClass.class);
                commentList.add(commentModelClass);
                adapter.notifyDataSetChanged();
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
        adapter = new CommentAdapter(commentList,id);
        commentRecyclerView.setAdapter(adapter);
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, id+".pdf");


        Document doc = new Document();



        try {
//            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(doc,new FileOutputStream(file));

            doc.open();

            String desc = articleDesc.getText().toString();
            String title = articelTitle.getText().toString();
            doc.addAuthor("NITC e-Magazine");
            Font fontSize_10 =  FontFactory.getFont(FontFactory.TIMES, 18f);
            Paragraph p = new Paragraph(title,fontSize_10);
            doc.add(p);
            doc.add(new Paragraph("Author Name :- "+ authorName));
            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph(desc));
            doc.close();

            Toast.makeText(ViewArticle.this, "Pdf downloaded successfully", Toast.LENGTH_SHORT).show();

//            String urlOfImage = imgfile.get(0);
//
//
//            //Set absolute position for image in PDF (or fixed)
//            image.setAbsolutePosition(100f, 500f);
//
//            //Scale image's width and height
//            image.scaleAbsolute(200f, 200f);
//
//            //Scale image's height
//            image.scaleAbsoluteWidth(200f);
//            //Scale image's width
//            image.scaleAbsoluteHeight(200f);
//
//            doc.add(Image.getInstance(imgfile.get(0))); //Add image to document


        }
        catch (Exception e)
        {
            System.out.println(e);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        createPdf();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (DocumentException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

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
                                Toast.makeText(ViewArticle.this, "Sign in successful", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(ViewArticle.this, MainActivity2.class);
//                                startActivity(intent);
                                dialog.cancel();

                            }
                            else if(auth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(ViewArticle.this, "Sign in successful", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(ViewArticle.this, MainActivity2.class);
//                                startActivity(intent);
                                dialog.cancel();

                            }
                            else {
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(ViewArticle.this, "Please verify your email.", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ViewArticle.this, "Invalid User", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(ViewArticle.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }
}