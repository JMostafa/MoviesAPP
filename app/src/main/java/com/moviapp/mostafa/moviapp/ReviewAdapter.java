package com.moviapp.mostafa.moviapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mostafa on 8/29/2016.
 */
public class ReviewAdapter extends BaseAdapter {

    Context context;
    Review review[];

    public ReviewAdapter(Context context, Review[] movies) {
        this.context = context;
        this.review = movies;
    }

    @Override
    public int getCount() {
        return review.length;
    }

    @Override
    public Object getItem(int i) {
        return review[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_reviews, null);
        TextView reviews_Author = (TextView)view.findViewById(R.id.reviews_Author);
        TextView reviews_Content = (TextView)view.findViewById(R.id.reviews_Content);
        TextView reviews_URL = (TextView)view.findViewById(R.id.reviews_URL);
        reviews_Author.setText(review[i].getAuthor());
        reviews_Content.setText(review[i].getContent());

        return view;
    }
}
