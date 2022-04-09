package com.example.myapplication2.viewholder;

import com.example.myapplication2.objectmodel.ModuleModel;

public class ProfileViewModel {
    String imagePath;
    String subject;
    String module;

    public ProfileViewModel(ModuleModel model) {
        this.imagePath = model.getImagePath();
        this.subject = model.getPillar();
        this.module = model.getName();
    }

    public ProfileViewModel(String imagePath, String subject, String module) {
        this.imagePath = imagePath;
        this.subject = subject;
        this.module = module;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getSubject() {
        return subject;
    }

    public String getModule() {
        return module;
    }
}
