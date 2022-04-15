package com.example.myapplication2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

    private static final String TAG = "UTILS";

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean haveNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        Log.i(TAG, "Active Network: " + haveNetwork);
        return haveNetwork;
    }

    public static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    public static void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url).into(imageView);
    }

    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public static String getDocumentFromPath(String documentId){
        return documentId.substring(documentId.lastIndexOf("/") + 1);
    }

    public static DocumentReference getCurrentUser(FirebaseFirestore db){
        DocumentReference user = LoggedInUser.getInstance().getUserDocRef(); // singleton
        if (user != null) {
            return user;
        }
        user = db.document("/Users/Test4");
        return user;
    }

    public static String getProfileID(String path){
        // get the element before the spacing
        Log.i(TAG, "STRING" + path.substring(path.lastIndexOf('/') + 1));
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Entry validation
     * https://www.c-sharpcorner.com/UploadFile/1e5156/validation/
     */
    public static boolean invalidData(EditText editText) {
        if (editText.getText().toString().length() == 0) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
            return true;
        } else {
            return false;
        }
    }


    public static void disableButton(MaterialButton button){
        button.setEnabled(false);
        button.setClickable(false);
        button.setVisibility(View.GONE);
        Log.d(TAG, "button : " + button + " disabled");
    }

    public static void disableButton(Button button){
        button.setEnabled(false);
        button.setClickable(false);
        button.setVisibility(View.GONE);
        Log.d(TAG, "button : " + button + " disabled");
    }

    public static void enableButton(MaterialButton button){
        button.setEnabled(true);
        button.setClickable(true);
        button.setVisibility(View.VISIBLE);
    }
}
