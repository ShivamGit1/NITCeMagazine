package com.example.nitcemagazine.RejectedArticle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RejectedArticleAdapter extends RecyclerView.Adapter<RejectedArticleAdapter.ViewHolder>{
    List<ModelClass> articleList;
    Context articleContext;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String title,desc,img,rate;

    public RejectedArticleAdapter(List<ModelClass> articleList, Context articleContext) {
        this.articleList = articleList;
        this.articleContext = articleContext;
    }

    @NonNull
    @Override
    public RejectedArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_with_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RejectedArticleAdapter.ViewHolder holder, int position) {
        try {

            String id = articleList.get(position).getId();
            reference.child("RejectedArticle").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    title = snapshot.child("title").getValue().toString();
                    desc = snapshot.child("description").getValue().toString();
                    img = snapshot.child("ArticleImage").getValue().toString();
                    rate= Math.round(Double.parseDouble(snapshot.child("Rating").getValue().toString())*10.0)/10.0+"/5";

                    String uid = snapshot.child("authorUid").getValue().toString();

                    DatabaseReference ref = database.getReference();
                    DatabaseReference ref1 = database.getReference();
                    ref1.child("UserType").child(uid).addValueEventListener(new ValueEventListener() {
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
                    Intent intent = new Intent(articleContext, RejectedArticleViewArticle.class);
                    intent.putExtra("ArticleIdIntent",id);
                    articleContext.startActivity(intent);
                    ((Activity)articleContext).finish();
                }
            });





        } catch (Exception e) {
            System.out.println();
        }

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView articelTitle, articleDesc, authorName, rating;
        ImageView articleImageCard;
        CardView articleCardView;
        Button repost;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            articelTitle = itemView.findViewById(R.id.textViewTitleCard);
            articleDesc = itemView.findViewById(R.id.textViewDescription);
            articleImageCard = itemView.findViewById(R.id.imageViewArticleImageCard);
            authorName = itemView.findViewById(R.id.textViewAuthorNameCard);
            articleCardView = itemView.findViewById(R.id.articleCardView);
            rating = itemView.findViewById(R.id.textViewRating);
        }
    }
}
