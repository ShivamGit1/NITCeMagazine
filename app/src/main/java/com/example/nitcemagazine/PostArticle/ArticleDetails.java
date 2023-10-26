package com.example.nitcemagazine.PostArticle;

public class ArticleDetails {
    public String title,description,category,authorUid,ArticleImage,Rating;
    Long reviewCount;

    ArticleDetails(){}

    public ArticleDetails(String title, String description, String category, String authorUid,String ArticleImage) {
        this.title = title;
        this.description = description;
        this.authorUid = authorUid;
        this.category = category;
        this.ArticleImage = ArticleImage;
        this.Rating = "0";
    }
}
