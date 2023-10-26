package com.example.nitcemagazine.ReviewUnpostedArticle;

import android.content.Context;
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
import com.example.nitcemagazine.PostUnpostedArticle.EditorPage;
import com.example.nitcemagazine.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewUnpostedArticleAdapter extends RecyclerView.Adapter<com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticleAdapter.ViewHolder>{

    List<ModelClass> articleList;
    Context articleContext;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public ReviewUnpostedArticleAdapter(List<ModelClass> articleList, Context articleContext) {
        this.articleList = articleList;
        this.articleContext = articleContext;
    }

    @NonNull
    @Override
    public com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_with_image,parent,false);

        return new com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.nitcemagazine.ReviewUnpostedArticle.ReviewUnpostedArticleAdapter.ViewHolder holder, int position) {
        String id = articleList.get(position).getId();

        reference.child("Article").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String title = snapshot.child("title").getValue().toString();
                    String desc = snapshot.child("description").getValue().toString();
//                String img = snapshot.child("Article Image").getValue().toString();
                    String rate= Math.round(Double.parseDouble(snapshot.child("Rating").getValue().toString())*10.0)/10.0+"/5";

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
                    holder.rating.setText(rate);

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
                Intent intent = new Intent(articleContext, ReviewerPage.class);
                intent.putExtra("ArticleIdIntent",id);
                articleContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
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
            rating =itemView.findViewById(R.id.textViewRating);
        }
    }
}

