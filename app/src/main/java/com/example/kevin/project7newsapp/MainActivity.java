package com.example.kevin.project7newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<List<Article>>{

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String GUARDIAN_API_KEY = "test";
    private static final String ARTICLES_REQUEST_URL = "https://content.guardianapis.com/search";
    private static final int ARTICLE_LOADER_ID = 1;

    private ArticleAdapter articleAdapter;
    private TextView emptyStateTextView;
    private SearchView searchView;
    private String articleQuery = "";
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView articlesListView = findViewById(R.id.list);

        emptyStateTextView = findViewById(R.id.empty_view);
        articlesListView.setEmptyView(findViewById(R.id.empty_view));

        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        articlesListView.setAdapter(articleAdapter);

        isConnected = checkNetworkStatus();

        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this).forceLoad();
        } else {
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(GONE);
            emptyStateTextView.setText("No internet connection!");
        }

        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current article that was clicked on
                Article currentArticle = articleAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        Log.d("ON_CREATE_LOADER", "onCreateLoader was just called");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_relevance_value)
        );

        String orderDate = sharedPrefs.getString(
                getString(R.string.settings_order_date_key),
                getString(R.string.settings_order_date_by_published_value)
        );

        Uri baseUri = Uri.parse(ARTICLES_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", articleQuery);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("order-date", orderDate);
        uriBuilder.appendQueryParameter("api-key", GUARDIAN_API_KEY);
        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        Log.d("ON_LOAD_FINISHED", "onLoadFinished was just called");
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(GONE);

        if (isConnected) {
            emptyStateTextView.setText("Begin search up top.");
        } else {
            emptyStateTextView.setText("No internet connection!");
        }

        articleAdapter.clear();

        System.out.println(articles);
        if (articles != null && !articles.isEmpty()) {
            articleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.d("ON_LOADER_RESET", "onLoaderReset was just called");
        articleAdapter.clear();
    }

    public boolean checkNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = new SearchView(this);
        searchView.setOnQueryTextListener(this);
        searchItem.setActionView(searchView);
        searchView.clearFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("ON_QUERY_TEXT_SUBMIT", "onQueryTextSubmit was just called");
        articleQuery = !TextUtils.isEmpty(query) ? query.replace(" ", "%") : null;

        isConnected = checkNetworkStatus();

        getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, this).forceLoad();

        searchView.clearFocus();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
