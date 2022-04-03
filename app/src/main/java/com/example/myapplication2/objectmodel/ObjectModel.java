package com.example.myapplication2.objectmodel;

import java.io.Serializable;

public interface ObjectModel extends Serializable {
    String getDocumentId();

    void setDocumentId(String documentId);
}
