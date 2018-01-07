package com.example.kevin.project7newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kevin on 11/6/2017.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    public static final String LOG_TAG = ArticleAdapter.class.getName();

    public ArticleAdapter(Activity context, List<Article> articles) {
        super(context,0,articles);
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the Article object located at this position in the list
        Article currentArticle = getItem(position);

        // Get the title for the current article
        String title = currentArticle.getTitle();
        // Find the TextView in the list_item.xml layout with the ID title_view
        TextView titleTV = (TextView) convertView.findViewById(R.id.title_view);
        // Set this title on the TextView
        titleTV.setText(title);

        // Get the section for the current article
        String section = currentArticle.getSection();
        // Find the TextView in the list_item.xml layout with the ID section_view
        TextView sectionTV = (TextView) convertView.findViewById(R.id.section_view);
        // Set this section on the TextView
        sectionTV.setText(section);

        // Get the date for the current article
        System.out.println(currentArticle.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(currentArticle.getDate());
            sdf.applyPattern("dd MMM yyyy hh:mm");
            // Find the TextView in the list_item.xml layout with the ID date_view
            TextView dateTV = (TextView) convertView.findViewById(R.id.date_view);
            // Set this date on the TextView
            dateTV.setText(sdf.format(date));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing date", e);
        }

        // Check whether or not the current article has the author(s) listed
        if (currentArticle.getAuthors() != null) {
            // Get the author data for the current article
            ArrayList<String> authorList = currentArticle.getAuthors();
            // Compose the authors string
            String authors = "By ";
            if (authorList.size() == 0) {
                authors = "";
            } else if (authorList.size() == 1) {
                authors = authors + authorList.get(0);
            } else {
                for (int i = 0; i < authorList.size() - 1; i++) {
                    authors = authors + authorList.get(i);
                }
                authors = authors + " and " + authorList.get(authorList.size() - 1);
            }
            // Find the TextView in the list_item.xml layout with the ID author_view
            TextView authorTV = (TextView) convertView.findViewById(R.id.author_view);
            // Set the authors on the TextView
            authorTV.setText(authors);
        }
        return convertView;
    }
}
