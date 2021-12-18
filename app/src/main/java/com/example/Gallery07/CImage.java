package com.example.Gallery07;
import java.lang.String;

public class CImage {
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

    public CImage(String imageUri, String date, int type) {
        this.imageUri = imageUri;
        this.date = date;
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

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
        return date.substring(0, 8);
    }

    public String getDay() {
        return this.date.substring(6, 8);
    }

    public String getMonth() {
        return this.date.substring(4, 6);
    }

    public String getYear() {
        return this.date.substring(2, 4);
    }
}

