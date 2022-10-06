package com.esri.natmoapp.model;

public class The_Slide_Items_Model_Class {

    private int featured_image;
    private String the_caption_Title;
    private String the_caption_Title1;

    public The_Slide_Items_Model_Class(int hero, String title,String title1) {
        this.featured_image = hero;
        this.the_caption_Title = title;
        this.the_caption_Title1 = title1;
    }

    public int getFeatured_image() {
        return featured_image;
    }

    public String getThe_caption_Title1() {
        return the_caption_Title1;
    }

    public void setThe_caption_Title1(String the_caption_Title1) {
        this.the_caption_Title1 = the_caption_Title1;
    }

    public String getThe_caption_Title() {
        return the_caption_Title;
    }

    public void setFeatured_image(int featured_image) {
        this.featured_image = featured_image;
    }

    public void setThe_caption_Title(String the_caption_Title) {
        this.the_caption_Title = the_caption_Title;
    }
}