package com.example.Gallery07;


public class CFolder {
    private String folderName;
    private int folderImage;

    public CFolder(String folderName, int folderImage) {
        this.folderName = folderName;
        this.folderImage = folderImage;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderImage() {
        return folderImage;
    }

    public void setFolderImage(int folderImage) {
        this.folderImage = folderImage;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "folderName='" + folderName + '\'' +
                ", folderImage=" + folderImage +
                '}';
    }
}
