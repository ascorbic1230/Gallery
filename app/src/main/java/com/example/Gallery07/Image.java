package com.example.Gallery07;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.String;
public class Image {
    private String imageUri;
    private boolean isChecked = false;
    private String date;
    private int type; // = 0 text, = 1 image
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Image(String imageUri, String date,int type) {
        this.imageUri = imageUri;
        this.date = date;
        this.type = type;
    }
    public int getType() {return this.type;}
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getDate() {
        return date;
    }
    public String getDateByMonthAndYear() {
        String m = date.substring(0,2);
        String y = date.substring(6,10);
        String result = m + y;
        return result;
    }
    public String getMonth() {
        return this.date.substring(0,2);
    }
    public String getYear() {
        return this.date.substring(6,10);
    }
}

