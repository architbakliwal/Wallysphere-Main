package com.wallysphere.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.os.AsyncTask;

public class DownloadFile extends AsyncTask<String, Void, String>
{
    private static Context context;
    int apiSelector = 0;
    public DownloadAndResize dwr = new DownloadAndResize(context);

    public DownloadFile(Context context) {
    	DownloadFile.context = context;
    }

    @Override
    protected String doInBackground(String... params)
    {
    	System.out.println("************************ " + params[0]);
    	String filePath = "";
    	String fileUrl = "";
    	String apiUrl = "";
        
        StringBuilder urlString = new StringBuilder();
        
        apiUrl = getUrlForApiSelector(params[0]);
        
//        urlString.append("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=night%2C+sky%2C+moon%2C+stars&text=night+sky+stars+moon&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1");
        urlString.append(apiUrl);
        
        fileUrl = httpDownload(urlString);
        filePath = dwr.download(fileUrl, params[0]);
        return (filePath);
    }

	@Override
    protected void onPostExecute(String filePath)
    {
    	File file = new File(filePath);
    	MediaScannerConnectionClient client = new MyMediaScannerConnectionClient(DownloadFile.context, file, null);
        super.onPostExecute(filePath);
    }
	
	protected String getUrlForApiSelector(String dayOrNight) {
		String FLICKR_MORNING = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=morning%2C+fresh%2C+beach%2C+sea&text=morning+beach+sea&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1";
		String FLICKR_NIGHT = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=night%2C+sky%2C+moon%2C+stars&text=night+sky+stars+moon&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1";
		String FIVEHUNDREDPX_MORNING = "https://api.500px.com/v1/photos/search?term=morning%2C%20beach%2C%20fresh%2C%20sea&rpp=10&only=Landscapes%2C%20Nature&image_size=4&sort=highest_rating&consumer_key=QbKA0F86Jx6xvOpxzmciYlRbBQQoIykCuXFuFKOX";
		String FIVEHUNDREDPX_NIGHT = "https://api.500px.com/v1/photos/search?term=night%2C%20sky%2C%20moon%2C%20stars&rpp=10&only=Landscapes%2C%20Nature&image_size=4&sort=highest_rating&consumer_key=QbKA0F86Jx6xvOpxzmciYlRbBQQoIykCuXFuFKOX";
		
//		String FIVEHUNDREDPX_MORNING = "https://api.500px.com/";
		
		Random rand = new Random();
		apiSelector = rand.nextInt((1 - 0) + 1) + 0;
		
		apiSelector = 0; //TODO remove this
		
//		0 is Flickr, 1 is 500px
        switch (apiSelector) {
		case 0:
			if(dayOrNight.equalsIgnoreCase("morning")) {
	        	return FLICKR_MORNING;
	        } else if(dayOrNight.equalsIgnoreCase("night")) {
	        	return FLICKR_NIGHT;
	        }
			break;
		case 1:
			if(dayOrNight.equalsIgnoreCase("morning")) {
	        	return FIVEHUNDREDPX_MORNING;
	        } else if(dayOrNight.equalsIgnoreCase("night")) {
	        	return FIVEHUNDREDPX_NIGHT;
	        }
			break;
		}
        return FLICKR_MORNING;
	}
    
    protected String httpDownload(StringBuilder urlString) {
    	HttpsURLConnection urlConnection = null;
        URL url = null;
        String response = "";
        String fileUrl = "";

        try
        {
        	System.out.println("0");
            url = new URL(urlString.toString());
            System.out.println("1");
            urlConnection = (HttpsURLConnection) url.openConnection();
            System.out.println("2");
            urlConnection.setRequestMethod("GET");
            System.out.println("3");
            urlConnection.setDoOutput(true);
            System.out.println("4");
            urlConnection.setConnectTimeout(30000);
            System.out.println("5");
            urlConnection.setDoInput(true);
            System.out.println("6");
            urlConnection.connect();
            System.out.println("7");
            InputStream inStream = null;
            System.out.println("8");
            inStream = urlConnection.getInputStream();
            System.out.println("9");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            System.out.println("10");
            String temp = "";
            while ((temp = bReader.readLine()) != null)
                response += temp;
            bReader.close();
            System.out.println("11");
            inStream.close();
            System.out.println("12");
            urlConnection.disconnect();
            System.out.println("13");
            if(apiSelector == 0) {
            	fileUrl = parseResponseForFlickr(response);
            } else if(apiSelector == 1) {
            	fileUrl = parseResponseFor500Px(response);
            }
            System.out.println("14");
            
        }
        catch (Exception e)
        {
            System.out.println("http download error: " + e.getMessage() + " : " + e.toString());
        }
        
        return fileUrl;
    }
    
    protected String parseResponseForFlickr(String response) {
    	String flickrUrl = "";
    	try {
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

            flickrUrl = "https://farm" + flickrfarmid + ".staticflickr.com/" + flickrserverid + "/" + flickrid + "_" + flickrsecret + "_b.jpg";
            System.out.println(flickrUrl);
            
    	}
    	catch (Exception e)
        {
    		System.out.println("parse error for flickr: " + e.getMessage());
        }
    	
    	return flickrUrl;
    	
    }
    
    protected String parseResponseFor500Px(String response) {
    	String fivehundredpxUrl = "";
    	try {
        	Random rand = new Random();
            int randomNum = rand.nextInt((9 - 0) + 1) + 0;

            JSONObject obj = new JSONObject(response);
            JSONArray photos = obj.getJSONArray("photos");
            JSONObject attrs = photos.getJSONObject(randomNum);

            fivehundredpxUrl = attrs.getString("image_url");
            System.out.println(fivehundredpxUrl);
            
    	}
    	catch (Exception e)
        {
    		System.out.println("parse error for 500px: " + e.getMessage());
        }
    	
    	return fivehundredpxUrl;
    	
    }
    
    public static void setContext (Context context) {
        if (DownloadFile.context == null) {
        	DownloadFile.context = context;
        }
    }
}