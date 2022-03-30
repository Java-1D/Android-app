package com.example.myapplication2.objectmodel;

/*
 * Firebase Firestore Document Object Model for the Modules Collection
 * @field name: string
 */
public class ModuleModel {

    public static final String TAG = "Module Model";
    public static final String collectionId = "Modules";

    private String name;

    public ModuleModel() {
    }

    public ModuleModel(String name) {
        this.name = name;
    }

    public String getModule() {
        return name;
    }

    public void setModule(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ModuleModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
