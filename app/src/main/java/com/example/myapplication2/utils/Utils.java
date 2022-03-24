package com.example.myapplication2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class  Utils {

   private static final String TAG = "UTILS";

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
}
