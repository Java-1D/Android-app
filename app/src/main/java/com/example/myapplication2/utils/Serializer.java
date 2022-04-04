package com.example.myapplication2.utils;
import com.example.myapplication2.objectmodel.ObjectModel;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Json Serializer/Deserializer.
 * Not functioning properly
 */
@Singleton
public class Serializer {

    private final Gson gson;

    @Inject
    Serializer() {
        // Custom serializer that accepts a DocumentReference data type and returns the
        // reference path as a string.
        JsonSerializer<DocumentReference> referenceSerializer = (src, type, context) ->
                src == null ? null : new JsonPrimitive(src.getPath());
        // Custom deserializer that accepts a json string for the DocumentReference data type and
        // returns a new DocumentReference that is created using the string reference path.
        JsonDeserializer<DocumentReference> referenceDeserializer = (JsonElement json, Type type,
                                                                     JsonDeserializationContext context) ->
                json == null ? null : FirebaseFirestore.getInstance().document(json.getAsString());
        // Builds the gson object using our custom DocumentReference serializer/deserializer above.
        gson = new GsonBuilder()
                .registerTypeAdapter(DocumentReference.class, referenceSerializer)
                .registerTypeAdapter(DocumentReference.class, referenceDeserializer).create();
    }

    /**
     * Serialize an object to Json.
     *
     * @param entity Object to serialize.
     * @param clazz Type of the entity to serialize.
     */
    public <T> String serialize(T entity, Class<T> clazz) {
        return gson.toJson(entity, clazz);
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param string Entity json string to deserialize.
     * @param clazz Type of the entity to deserialize.
     */
    public <T> T deserialize(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }

    /**
     * Serialize an object to Json String.
     *
     * @param entity Object to serialize.
     */
    public <T> String serialize(T entity) {
        return gson.toJson(entity);
    }

}
