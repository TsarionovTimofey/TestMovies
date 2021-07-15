package com.example.testmovies.pojo.critic;

import com.example.testmovies.adapters.items.ItemType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Critic implements ItemType {

    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("sort_name")
    @Expose
    private String sortName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("seo_name")
    @Expose
    private String seoName;
    @SerializedName("multimedia")
    @Expose
    private Multimedia multimedia;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    @Override
    public int getItemType() {
        return ItemType.TYPE_MUTABLE;
    }
}
