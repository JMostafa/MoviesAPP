package com.moviapp.mostafa.moviapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by mostafa on 8/29/2016.
 */
public class MoviAdapterBitmap extends BaseAdapter {

    Context context;
    Bitmap posters[];

    public MoviAdapterBitmap(Context context, Bitmap[] movies) {
        this.context = context;
        this.posters = movies;
    }

    @Override
    public int getCount() {
        return posters.length;
    }

    @Override
    public Object getItem(int i) {
        return posters[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = LayoutInflater.from(context).inflate(R.layout.moviposter, null);
        ImageView icon = (ImageView) view.findViewById(R.id.poster_Image);
        //Toast.makeText(context,"http://image.tmdb.org/t/p/w185/"+posters[i],Toast.LENGTH_LONG).show();
        icon.setImageBitmap(posters[i]);
        //Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+posters[i]).into(icon);
        return view;
    }
}
