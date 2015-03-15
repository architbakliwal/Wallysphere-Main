package com.wallysphere.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {

  private int result = Activity.RESULT_CANCELED;
  public static final String URL = "urlpath";
  public static final String FILENAME = "filename";
  public static final String FILEPATH = "filepath";
  public static final String RESULT = "result";
  public static final String NOTIFICATION = "com.vogella.android.service.receiver";

  public DownloadService() {
    super("DownloadService");
  }

  // will be called asynchronously by Android
  @Override
  protected void onHandleIntent(Intent intent) {
    StringBuilder urlString = new StringBuilder();
    urlString.append("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=night%2C+sky%2C+moon%2C+stars&text=night+sky+stars+moon&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1");

    System.out.println(urlString.toString());

    HttpURLConnection urlConnection = null;
    URL url = null;
    JSONObject object = null;
    String response = "";

    try
    {
        url = new URL(urlString.toString());
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();
        InputStream inStream = null;
        inStream = urlConnection.getInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
        String temp = "";
        while ((temp = bReader.readLine()) != null)
            response += temp;
        bReader.close();
        inStream.close();
        urlConnection.disconnect();
        // object = (JSONObject) new JSONTokener(response).nextValue();
        // JSONArray photos = object.getJSONArray("photos");
        System.out.println(response);
        // JSONArray photo = photos.getJSONArray("photo");
        // JSONObject attrs = photo.getJSONObject(0);
        // System.out.println(attrs.getString("title"));
        
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    // publishResults(output.getAbsolutePath(), result);
  }

  private void publishResults(String outputPath, int result) {
    
  }
} 