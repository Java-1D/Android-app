package com.example.myapplication2.objectmodel;

/*
 * Firebase Firestore Document Object Model for the Modules Collection
 * @field name: string
 */
public class ModuleModel {

    private static final String TAG = "Module Model";
    private String name;

    ModuleModel() {
    }

    ModuleModel(String name) {
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
