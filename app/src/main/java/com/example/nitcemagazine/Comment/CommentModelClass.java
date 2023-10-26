package com.example.nitcemagazine.Comment;

public class CommentModelClass {
    public String commnet, userId, commentKey;

    public CommentModelClass() {
    }

    public CommentModelClass(String commnet, String userId, String commentKey) {
        this.commnet = commnet;
        this.userId = userId;
        this.commentKey = commentKey;
    }

    public String getCommnet() {
        return commnet;
    }

    public void setCommnet(String commnet) {
        this.commnet = commnet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }
}
