package com.wallysphere.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;


public class DownloadAndResize{

    public JSONObject params = new JSONObject();
    private static Context context;
    private ScreenProperties screenProperties;

    public DownloadAndResize(Context context) {
    	DownloadAndResize.context = context;
        if(getSharedPreferencesForScreen().contains("WallphereScreenProperties")) {
        	Gson gson = new Gson();
            String screenPropertiesJson = getSharedPreferencesForScreen().getString("WallphereScreenProperties", "");
            screenProperties = gson.fromJson(screenPropertiesJson, ScreenProperties.class);
        } else {
        	System.out.println("WallphereScreenProperties not found");
        }
    }

    private Bitmap getBitmap(String imageData, BitmapFactory.Options options) throws IOException, URISyntaxException {
        Bitmap bmp;
        
        URL url = new URL(imageData);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.connect();
        InputStream input = connection.getInputStream();
        bmp = BitmapFactory.decodeStream(input, null, options);
        input.close();
        connection.disconnect();
        
        return bmp;
    }
    
    private String storeImage(JSONObject params, Bitmap bmp) throws JSONException, IOException, URISyntaxException {
        
        int quality = 100;
        String filename = params.getString("filename");
        String folderName = params.getString("directory");
        File folder = new File(Environment.getExternalStorageDirectory()+"/"+folderName);
        if(!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, filename + ".jpeg");
        if (file.exists ()) file.delete (); 

        OutputStream outStream = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, outStream);

        outStream.flush();
        outStream.close();
        
        return file.getAbsolutePath();
    }

	public String download(String url, String filename) {
		try {
            params.put("filename", filename);
            params.put("directory", "Wallysphere");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            getBitmap(url, options);
            float[] sizes = calculateFactors(options.outWidth, options.outHeight);
            float reqWidth = options.outWidth * sizes[0];
            float reqHeight = options.outHeight * sizes[1];
            int inSampleSize = calculateInSampleSize(options, (int)reqWidth, (int)reqHeight);
    
            options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            Bitmap bmp = getBitmap(url, options);
            if (bmp == null) {
                throw new IOException("The image file could not be opened.");
            }
            
            sizes = calculateFactors(options.outWidth, options.outHeight);
            bmp = getResizedBitmap(bmp, sizes[0], sizes[1]);

            String filePath = storeImage(params, bmp);
            return filePath;
                    
        } catch (JSONException e) {
            Log.d("PLUGIN", e.getMessage());
            return "";
            // callbackContext.error(e.getMessage());
        } catch (IOException e) {
            Log.d("PLUGIN", e.getMessage());
            return "";
            // callbackContext.error(e.getMessage());
        } catch (URISyntaxException e) {
            Log.d("PLUGIN", e.getMessage());
            return "";
            // callbackContext.error(e.getMessage());
        }
    }

    private Bitmap getResizedBitmap(Bitmap bm, float widthFactor, float heightFactor) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(widthFactor, heightFactor);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
    
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
    
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
    
        return inSampleSize;
    }
    
    private float[] calculateFactors(int width, int height) throws JSONException {
        float widthFactor;
        float heightFactor;
        String resizeType = "maxPixelResize";
        float desiredWidth = (float)screenProperties.getScreenWidth();
        float desiredHeight = (float)screenProperties.getScreenHeight();
        
        if (resizeType.equals("minPixelResize")) {
            widthFactor = desiredWidth / (float)width;
            heightFactor = desiredHeight / (float)height;
            if (widthFactor > heightFactor && widthFactor <= 1.0) {
                heightFactor = widthFactor;
            } else if (heightFactor <= 1.0) {
                widthFactor = heightFactor;
            } else {
                widthFactor = 1.0f;
                heightFactor = 1.0f;
            }
        } else if (resizeType.equals("maxPixelResize")) {
            widthFactor = desiredWidth / (float)width;
            heightFactor = desiredHeight / (float)height;
            if (widthFactor == 0.0) {
                widthFactor = heightFactor;
            } else if (heightFactor == 0.0) {
                heightFactor = widthFactor;
            } else if (widthFactor > heightFactor) {
                widthFactor = heightFactor; // scale to fit height
            } else {
                heightFactor = widthFactor; // scale to fit width
            }
        } else {
            widthFactor = desiredWidth;
            heightFactor = desiredHeight;
        }

        //pixelDensity
        /*DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (metrics.density > 1) {
            if (widthFactor * metrics.density < 1.0 && heightFactor * metrics.density < 1.0) {
                widthFactor *= metrics.density;
                heightFactor *= metrics.density;
            } else {
                widthFactor = 1.0f;
                heightFactor = 1.0f;
            }
        }*/
        
        double screenDensity = screenProperties.getScreenDensity();
        if (screenDensity > 1) {
            if (widthFactor * screenDensity < 1.0 && heightFactor * screenDensity < 1.0) {
                widthFactor *= screenDensity;
                heightFactor *= screenDensity;
            } else {
                widthFactor = 1.0f;
                heightFactor = 1.0f;
            }
        }
        
        float[] sizes = {widthFactor, heightFactor};
        return sizes;
    }
    
    /**
     * Set the application context if not already set.
     */
    public static void setContext (Context context) {
        if (DownloadAndResize.context == null) {
        	DownloadAndResize.context = context;
        }
    }
    
    /**
     * The Local storage for the application setting.
     */
    protected static SharedPreferences getSharedPreferencesForSettings () {
        return context.getSharedPreferences("Wallysphere", Context.MODE_PRIVATE);
    }

    /**
     * The Local storage for the application screen properties.
     */
    protected static SharedPreferences getSharedPreferencesForScreen () {
        return context.getSharedPreferences("WallysphereScreen", Context.MODE_PRIVATE);
    }
}