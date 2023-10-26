package com.example.nitcemagazine.FragmentAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.R;
import com.example.nitcemagazine.Fragments.ViewArticle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class EducationalAdapter extends RecyclerView.Adapter<EducationalAdapter.ViewHolder> {

    List<ModelClass> articleList;
    Context articleContext;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public EducationalAdapter(List<ModelClass> articleList,Context articleContext) {
        this.articleList = articleList;
        this.articleContext = articleContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_with_image,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            String id = articleList.get(position).getId();
            reference.child("PostedArticle").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = snapshot.child("title").getValue().toString();
                    String desc = snapshot.child("description").getValue().toString();
                    String img = snapshot.child("ArticleImage").getValue().toString();
                    String rate= Math.round(Double.parseDouble(snapshot.child("Rating").getValue().toString())*10.0)/10.0+"/5";

                    String uid = snapshot.child("authorUid").getValue().toString();

                    DatabaseReference ref = database.getReference();
                    DatabaseReference ref1 = database.getReference();
                    ref1.child("UserType").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String roleOfUser = snapshot.getValue().toString();
                            ref.child(roleOfUser).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String author = snapshot.child("name").getValue().toString();
                                    holder.authorName.setText(author);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.articelTitle.setText(title);
                    holder.articleDesc.setText(desc);
                    holder.rating.setText(rate);

                    if (!img.equalsIgnoreCase("null")) {
                        Picasso.get().load(img).into(holder.articleImageCard);
                    }
                    else
                    {
                        holder.articleImageCard.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        holder.articleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(articleContext, ViewArticle.class);
                intent.putExtra("ArticleIdIntent",id);
                intent.putExtra("AuthorName",holder.authorName.getText().toString());
                articleContext.startActivity(intent);
            }
        });


        }catch (Exception e)
        {
            System.out.println();
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < articleList.size(); i++)
        {
            if(articleList.get(i).getCategory().equalsIgnoreCase("Educational"))
            {
                count++;
            }
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView articelTitle,articleDesc,authorName,rating;
        ImageView articleImageCard;
        CardView articleCardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            articelTitle = itemView.findViewById(R.id.textViewTitleCard);
            articleDesc = itemView.findViewById(R.id.textViewDescription);
            articleImageCard = itemView.findViewById(R.id.imageViewArticleImageCard);
            authorName = itemView.findViewById(R.id.textViewAuthorNameCard);
            articleCardView = itemView.findViewById(R.id.articleCardView);
            rating=itemView.findViewById(R.id.textViewRating);
        }
    }




}
