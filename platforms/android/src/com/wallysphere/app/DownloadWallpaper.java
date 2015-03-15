package com.wallysphere.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

	
public class DownloadWallpaper {

//	public DownloadAndResize dwr = new DownloadAndResize();

	public String downloadFlickr() {
		StringBuilder urlString = new StringBuilder();
        urlString.append("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=night%2C+sky%2C+moon%2C+stars&text=night+sky+stars+moon&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1");

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
            // System.out.println(response);
            Random rand = new Random();
		    int randomNum = rand.nextInt((9 - 0) + 1) + 0;

            JSONObject obj = new JSONObject(response);
    		JSONObject photos = obj.getJSONObject("photos");
    		JSONArray photo = photos.getJSONArray("photo");
    		JSONObject attrs = photo.getJSONObject(randomNum);

            String flickrfarmid = attrs.getString("farm");
            String flickrserverid = attrs.getString("server");
            String flickrid = attrs.getString("id");
            String flickrsecret = attrs.getString("secret");

            String flickrUrl = "https://farm" + flickrfarmid + ".staticflickr.com/" + flickrserverid + "/" + flickrid + "_" + flickrsecret + "_b.jpg";

            System.out.println(flickrUrl);
//            dwr.download(flickrUrl, "morning");
            
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

        return (response);
	}
}