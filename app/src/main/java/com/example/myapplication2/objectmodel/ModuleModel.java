package com.example.myapplication2.objectmodel;

/*
 * Firebase Firestore Document Object Model for the Modules Collection
 * @field imagePath: string
 * @field name: string
 * @field pillar: string
 */
public class ModuleModel {

    public static final String TAG = "Module Model";
    public static final String collectionId = "Modules";

    private String imagePath;
    private String name;
    private String pillar;

    public ModuleModel() {
    }

    public ModuleModel(String imagePath, String name, String pillar) {
        this.imagePath = imagePath;
        this.name = name;
        this.pillar = pillar;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getModule() {
        return name;
    }

    public void setModule(String name) {
        this.name = name;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    @Override
    public String toString() {
        return "ModuleModel{" +
                "imagePath='" + imagePath + '\'' +
                ", name='" + name + '\'' +
                ", pillar='" + pillar + '\'' +
                '}';
    }
}
