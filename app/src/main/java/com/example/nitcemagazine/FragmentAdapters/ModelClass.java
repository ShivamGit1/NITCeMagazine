package com.example.nitcemagazine.FragmentAdapters;

public class ModelClass {
    String title,desc,id,category,img,reviewCount,status,uid;

    public ModelClass(){}

    public ModelClass(String title, String desc,String category,String img) {
        this.title = title;
        this.category = category;
        this.desc = desc;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public String getReviewCount() {
//        return reviewCount;
//    }
//
//    public void setReviewCount(String reviewCount) {
//        this.reviewCount = reviewCount;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
