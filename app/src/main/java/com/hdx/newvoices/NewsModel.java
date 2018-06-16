package com.hdx.newvoices;

public class NewsModel {

    private String postTitle;
    private String postAddress;
    private String postDate;
    private String postCategory;
    private String postUserName;
    private String postDescription;
    private String postImage;

    public NewsModel() {
    }

    public NewsModel(String postTitle, String postAddress, String postDate, String postCategory, String postUserName, String postDescription, String postImage) {
        this.postTitle = postTitle;
        this.postAddress = postAddress;
        this.postDate = postDate;
        this.postCategory = postCategory;
        this.postUserName = postUserName;
        this.postDescription = postDescription;
        this.postImage = postImage;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
