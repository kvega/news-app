package com.example.kevin.project7newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Kevin on 11/14/2017.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private static final String LOG_TAG = ArticleLoader.class.getName();

    private String url;

    // Constructor for a valid url
    public ArticleLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d("ON_START_LOADING", "onStartLoading was just called");
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        Log.d("LOAD_IN_BACKGROUND", "loadInBackground was just called");
        if (this.url == null) {
            return null;
        }
        // Load articles
        List<Article> articles = QueryUtils.fetchArticles(this.url);

        return articles;
    }
}
