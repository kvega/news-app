package com.example.kevin.project7newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Kevin on 11/7/2017.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int SLEEP_TIMEOUT = 2000;

    private QueryUtils() {
    }

    public static ArrayList<Article> fetchArticles(String requestUrl) {
        try {
            Thread.sleep(SLEEP_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Create an empty ArrayList that we can start adding articles to
        ArrayList<Article> articles = extractArticlesFromJson(jsonResponse);

        return articles;
    }

    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    // Remember to change back to private after debugging
    public static ArrayList<Article> extractArticlesFromJson(String articlesJSON) {
        Log.d("EXTRACT_ARTICLES_JSON", "extractArticlesFromJson was just called");
        ArrayList<Article> articles = new ArrayList<>();
        if (TextUtils.isEmpty(articlesJSON)) {
            return null;
        }

        try {
            /* Parse the JSON response and build up a list of Article objects.
             * Convert the response string into a JSONObject
             * */
            JSONObject jsonObject = new JSONObject(articlesJSON);
            JSONObject response = jsonObject.getJSONObject("response");

            // Check for "results" JSONArray
            if (response.has("results")) {
                // Extract "results" JSONArray
                JSONArray results = response.optJSONArray("results");
                System.out.println(results);
                // Loop through each item in the array
                for (int i = 0; i < results.length(); i++) {
                    Article article;
                    JSONObject result = results.getJSONObject(i);
                    String section = result.getString("sectionName");
                    String title = result.getString("webTitle");
                    String date = result.getString("webPublicationDate");
                    String url = result.getString("webUrl");
                    JSONArray tags = result.optJSONArray("tags");
                    ArrayList<String> authors = new ArrayList<>();
                    for (int j = 0; j < tags.length(); j++) {
                        JSONObject tag = tags.getJSONObject(j);
                        String firstName = tag.getString("firstName");
                        String lastName = tag.getString("lastName");
                        authors.add(firstName + " " + lastName);
                    }



                    article = new Article(title, authors, section, date, url);

                    articles.add(article);
                }
            }

            // Check for
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the article JSON results", e);
        }
        return articles;
    }
}
