package com.example.nasa_api;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import okhttp3.Headers;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity2 extends AppCompatActivity {
    private String apikey = "nz2Zd4F0QhIFOSsiHIjjM2ISQ02FewqG5VVNJN4X";
    private long timestamp = System.currentTimeMillis();
    private String baseurl = "https://api.nasa.gov/planetary/apod";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView pic = findViewById(R.id.nasapic);
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        Button next = findViewById(R.id.next);

        getNextData(pic, text1, text2, next);
    }

    private void getNextData(ImageView pic, TextView t1, TextView t2, Button next) {
        next.setOnClickListener(v -> {
            int randId = new Random().nextInt(1001);
            getURL(pic, t1, t2, randId);
        });
    }

    private void getURL(ImageView pic, TextView t1, TextView t2, int randnum) {
        AsyncHttpClient client = new AsyncHttpClient();

        // need to include timestamp later?
        String url = baseurl + "?api_key=" + apikey + "&count=1";
        Log.d("NASAFetch", "fetching " + url);

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // Parse the JSON response
                    JSONObject obj = json.jsonArray.getJSONObject(0);
                    String imageUrl = obj.getString("url");
                    String explanation = obj.getString("explanation");
                    String title = obj.getString("title");

                    // Update the ImageView with the APOD image
                    // You can use libraries like Glide or Picasso for image loading
                    // For simplicity, let's assume you have a method setImageUrl() to load the image
                    loadImage(imageUrl, pic);

                    // Update TextViews with relevant information
                    t1.setText(title);
                    t2.setText(explanation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                // Handle failure
                Log.e("APIRequest", "Failed to fetch data: " + response);
            }
        });
    }

    private String generateHash(long timestamp) {
        String input = timestamp + apikey;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder result = new StringBuilder();

            for (byte b : digest) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .into(imageView);
    }
}
