package com.example.dia;

public class DocumentModel {
    private String type;
    private String title;
    private String number;
    private String birthday;
    private String name;
    private String birthPlace;
    private String photoPath;

    public DocumentModel(String type, String title, String number, String birthday, String name, String birthPlace, String photoPath) {
        this.type = type;
        this.title = title;
        this.number = number;
        this.birthday = birthday;
        this.name = name;
        this.birthPlace = birthPlace;
        this.photoPath = photoPath;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getNumber() { return number; }
    public String getBirthday() { return birthday; }
    public String getName() { return name; }
    public String getBirthPlace() { return birthPlace; }
    public String getPhotoPath() { return photoPath; }
}