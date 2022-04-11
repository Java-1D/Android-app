package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentId;

/*
 * Firebase Firestore Document Object Model for the Modules Collection
 * @ID documentId: string
 *
 * @field imagePath: string
 * @field name: string
 * @field pillar: string
 */
public class ModuleModel implements ObjectModel {

    public static final String TAG = "Module Model";
    public static final String COLLECTION_ID = "Modules";

    @DocumentId
    private String documentId;

    private String imagePath;
    private String name;
    private String pillar;

    public ModuleModel() {
    }

    public ModuleModel(String documentId, String imagePath, String name, String pillar) {
        this.documentId = documentId;
        this.imagePath = imagePath;
        this.name = name;
        this.pillar = pillar;
    }

    public static String getTAG() {
        return TAG;
    }

    public static String getCollectionId() {
        return COLLECTION_ID;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        if (name == null){
            return "Test";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public String getModuleName(){
        // get the element before the spacing
        String[] split = name.split(" ");
        String moduleName = split[0];
        return moduleName;
    }

    @Override
    public String toString() {
        return "ModuleModel{" +
                "documentId='" + documentId + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", name='" + name + '\'' +
                ", pillar='" + pillar + '\'' +
                '}';
    }
}
