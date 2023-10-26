package com.example.nitcemagazine.DeleteArticle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.Fragments.ViewArticle;
import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.PostUnpostedArticle.EditorPage;
import com.example.nitcemagazine.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DeleteArticleAdapter extends RecyclerView.Adapter<DeleteArticleAdapter.ViewHolder>{

    List<ModelClass> articleList;
    Context articleContext;
    List<String > reviewCount;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public DeleteArticleAdapter(List<ModelClass> articleList, Context articleContext) {
        this.articleList = articleList;
        this.articleContext = articleContext;
    }

    @NonNull
    @Override
    public DeleteArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_with_image,parent,false);

        return new DeleteArticleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteArticleAdapter.ViewHolder holder, int position) {
        int x = position;
        String id = articleList.get(x).getId();

        reference.child("PostedArticle").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String title = snapshot.child("title").getValue().toString();
                    String desc = snapshot.child("description").getValue().toString();
//                String img = snapshot.child("Article Image").getValue().toString();

                    String uid = snapshot.child("authorUid").getValue().toString();

                    DatabaseReference ref = database.getReference();
                    DatabaseReference ref1 = database.getReference();
                    ref1.child("UserType").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String roleOfUser = snapshot.getValue().toString();
                            ref.child(roleOfUser).child(uid).addValueEventListener(new ValueEventListener() {
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


                    holder.articleImageCard.setVisibility(View.GONE);
                }
                catch (Exception e)
                {
                    System.out.println("");
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

        holder.articleCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(articleContext)
                        .setTitle("Are you sure")
                        .setMessage("Do you want to delete this article")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.child("PostedArticle").child(id).removeValue();


                                Intent intent = new Intent(articleContext, DeleteArticle.class);
                                articleContext.startActivity(intent);
                                ((Activity)articleContext).finish();
                            }
                        }).setNegativeButton("No",null)
                        .show();


                return true;
            }
        });



    }

    @Override
    public int getItemCount() {

        return articleList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView articelTitle,articleDesc,authorName;
        ImageView articleImageCard;
        CardView articleCardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            articelTitle = itemView.findViewById(R.id.textViewTitleCard);
            articleDesc = itemView.findViewById(R.id.textViewDescription);
            articleImageCard = itemView.findViewById(R.id.imageViewArticleImageCard);
            authorName = itemView.findViewById(R.id.textViewAuthorNameCard);
            articleCardView = itemView.findViewById(R.id.articleCardView);

        }
    }
}

