package com.example.nitcemagazine.Comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {

    List<CommentModelClass> commentList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    String articleId;

    public CommentAdapter(List<CommentModelClass> commentList, String articleId) {
        this.commentList = commentList;
        this.articleId = articleId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = commentList.get(position).getUserId();


        holder.commentText.setText(commentList.get(position).getCommnet());
        DatabaseReference ref = database.getReference();

        ref.child("UserType").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String roleOfUser = snapshot.getValue().toString();
                reference.child(roleOfUser).child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.commentUserName.setText(snapshot.child("name").getValue().toString());
                        String img = snapshot.child("profilePictures").getValue().toString();


                        if (!img.equalsIgnoreCase("null")) {
                            Picasso.get().load(img).into(holder.commentUserImage);
                        }
                        else
                        {
//                            Picasso.get().load(R.drawable.baseline_account_circle_24).into(holder.commentUserImage);
                            holder.commentUserImage.setImageResource(R.drawable.baseline_account_circle_24);
                        }

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

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView commentText,commentUserName;
        CircleImageView commentUserImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentText = itemView.findViewById(R.id.commentText);
            commentUserImage = itemView.findViewById(R.id.circleImageViewCommentUser);
            commentUserName = itemView.findViewById(R.id.commentUserName);


        }
    }
}
