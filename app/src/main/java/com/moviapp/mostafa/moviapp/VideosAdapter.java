package com.moviapp.mostafa.moviapp;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by mostafa on 8/29/2016.
 */
public class VideosAdapter extends BaseAdapter {

    Context context;
    video videos[];

    public VideosAdapter(Context context, video[] videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.length;
    }

    @Override
    public Object getItem(int i) {
        return videos[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_videos, null);
        TextView video_Name = (TextView)view.findViewById(R.id.video_Name);
        TextView video_Type = (TextView)view.findViewById(R.id.video_Type);
        TextView video_site = (TextView)view.findViewById(R.id.video_site);
        video_Name.setText(videos[i].getName());
        video_Type.setText(videos[i].getType());
        video_site.setText(videos[i].getSite());
        return view;
    }
}
