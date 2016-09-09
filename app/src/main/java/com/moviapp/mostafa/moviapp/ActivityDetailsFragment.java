package com.moviapp.mostafa.moviapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActivityDetailsFragment extends Fragment {

    MoviDBHelper dataBase;
    MoviData moviData;
    View rootView;
    CheckBox detail_Favorite;
    TextView detail_Dtae;
    TextView detail_Title;
    TextView detail_OverView;
    RatingBar detail_Rate;
    ImageView detail_Poster;
    TextView detail_Vidoes;
    TextView detail_Review;
    ReviewAdapter reviewAdapter;
    VideosAdapter videosAdapter;
    ListView listView_review;
    ListView listView_videos;
    video[] videos;

    public ActivityDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        dataBase=new MoviDBHelper(getContext());
        rootView = inflater.inflate(R.layout.fragment_activity_details, container, false);
        listView_review =(ListView) rootView.findViewById(R.id.listView_reviews);
        listView_videos =(ListView) rootView.findViewById(R.id.listView_videos);
        moviData = new MoviData();
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getBundleExtra("Data");
        if(bundle ==null)
            bundle =getArguments();
        detail_Favorite = (CheckBox) rootView.findViewById(R.id.detail_Favorite);
        detail_Poster = (ImageView) rootView.findViewById(R.id.detail_Poster);
        detail_Rate =(RatingBar) rootView.findViewById(R.id.detail_Rate);
        detail_Dtae = (TextView) rootView.findViewById(R.id.detail_Dtae);
        detail_Vidoes = (TextView) rootView.findViewById(R.id.textView_Viduos);
        detail_Review = (TextView) rootView.findViewById(R.id.textView_Reviews);
        detail_OverView = (TextView) rootView.findViewById(R.id.detail_OverView);
        detail_Title = (TextView) rootView.findViewById(R.id.detail_Title);
        boolean isFavorite = false ;
        if(bundle !=null )
        {
            // getActivity().getActionBar().setTitle(intent.getStringExtra("Title"));

            moviData.setDate(bundle.getString("Date"));
            moviData.setTitle(bundle.getString("Title"));
            moviData.setId(bundle.getInt("Id",-1));
            moviData.setLanguage(bundle.getString("Language"));
            moviData.setRate(bundle.getFloat("Rate",0.0f));
            moviData.setOverView(bundle.getString("OverView"));
            moviData.setBackdropPath(bundle.getString("Backdrop"));
            moviData.setPosterPath(bundle.getString("Poster"));
            isFavorite = dataBase.ifFound(moviData.getId());
            if(isFavorite)
                detail_Favorite.setChecked(true);
            detail_Dtae.setText(moviData.getDate());
            detail_Title.setText(moviData.getTitle());
            detail_Rate.setRating((moviData.getRate()/2.0f));
            detail_Rate.setEnabled(false);
            detail_OverView.setText(moviData.getOverView());
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+ moviData.getPosterPath()).into(detail_Poster);
            updateReviews(moviData.getId());
            updateVideos(moviData.getId());
        }
        detail_Favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    //
                    if(!dataBase.ifFound(moviData.getId()))
                    {
                        ContentValues values =new ContentValues();
                        values.put("id",moviData.getId());
                        values.put("title",moviData.getTitle());
                        values.put("date",moviData.getDate());
                        values.put("overView",moviData.getOverView());
                        values.put("language",moviData.getLanguage());
                        values.put("posterPath",moviData.getPosterPath());
                        values.put("rate",moviData.getRate());
                        Bitmap image =((BitmapDrawable)detail_Poster.getDrawable()).getBitmap();
                        values.put("poster",getBytes(image));

                        long a= dataBase.insertRow("moviData",null,values);
                        if(a!=-1)
                            Toast.makeText(getActivity(),"Movi Added To Favorite" ,Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(dataBase.ifFound(moviData.getId())) {
                        if(dataBase.delete("moviData"," id = " +moviData.getId(),null))
                             Toast.makeText(getActivity(),"Movi Removed From Favorite" ,Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        listView_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key = videos[i].getKey();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + key));
                    startActivity(intent);
                }
            }
        });

        return rootView ;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void updateReviews(int id)
    {
        FetchReviewsTask fetchReviewsTask =new FetchReviewsTask();
        String url = "http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=adbf0eec884f61c468a3b71d1e721503";
        fetchReviewsTask.execute(url);
    }

    public void updateVideos(int id)
    {
        FetchVideosTask fetchVideosTask =new FetchVideosTask();
        String url = "http://api.themoviedb.org/3/movie/"+id+"/videos?api_key=adbf0eec884f61c468a3b71d1e721503";
        fetchVideosTask.execute(url);
    }

    public class FetchReviewsTask extends AsyncTask<String,Void,Review[]> {

        private Review[] getMoviFromJson(String moviJsonStr) throws JSONException {
            JSONObject ReviewsJson = new JSONObject(moviJsonStr);
            JSONArray Result = ReviewsJson.getJSONArray("results");
            Review[] reviews =new Review[Result.length()];
            JSONObject movijsonobject;
            for(int i = 0; i < Result.length(); i++) {
                Review review =new Review();
                movijsonobject = Result.getJSONObject(i);
                review.setId(movijsonobject.getString("id"));
                review.setAuthor(movijsonobject.getString("author"));
                review.setContent(movijsonobject.getString("content"));
                review.setUrl(movijsonobject.getString("url"));
                reviews[i] = review;
            }
            return reviews;
        }

        @Override
        protected Review[] doInBackground(String... params) {

            if(params.length == 0 )
                return null ;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviJsonStr = null;

            try {

                // Create the request to OpenWeatherMap, and open the connection
                URL url = new URL(params[0].toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    Toast.makeText(getActivity(),"No Internet Connection !!!" ,Toast.LENGTH_SHORT).show();
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                return getMoviFromJson(moviJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //;
            return null;
        }

        @Override
        protected  void onPostExecute(Review[] result)
        {
            if(result != null && result.length != 0)
            {
                detail_Review.setText("Reviews");
                reviewAdapter = new ReviewAdapter(getActivity(),result);
                listView_review.setAdapter(reviewAdapter);
            }

        }

    }

    public class FetchVideosTask extends AsyncTask<String,Void,video[]> {

        private video[] getMoviFromJson(String moviJsonStr) throws JSONException {
            JSONObject ReviewsJson = new JSONObject(moviJsonStr);
            JSONArray Result = ReviewsJson.getJSONArray("results");
            videos = new video[Result.length()];
            //Log.d("asd",""+Result.length());
            //Toast.makeText(getActivity(),""+Result.length(),Toast.LENGTH_SHORT).show();
            JSONObject movijsonobject;
            for(int i = 0; i < Result.length(); i++) {
                video videoD =new video();
                movijsonobject = Result.getJSONObject(i);
                videoD.setId(movijsonobject.getString("id"));
                videoD.setType(movijsonobject.getString("type"));
                videoD.setKey(movijsonobject.getString("key"));
                videoD.setName(movijsonobject.getString("name"));
                videoD.setSite(movijsonobject.getString("site"));
                videos[i] = videoD;
            }
            return videos;
        }

        @Override
        protected video[] doInBackground(String... params) {

            if(params.length == 0 )
                return null ;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviJsonStr = null;

            try {

                // Create the request to OpenWeatherMap, and open the connection
                URL url = new URL(params[0].toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    Toast.makeText(getActivity(),"No Internet Connection !!!" ,Toast.LENGTH_SHORT).show();
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                return getMoviFromJson(moviJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //;
            return null;
        }

        @Override
        protected  void onPostExecute(video[] result)
        {
            if(result != null && result.length != 0)
            {
                detail_Vidoes.setText("Trailers");
                videosAdapter = new VideosAdapter(getActivity(),result);
                listView_videos.setAdapter(videosAdapter);
                //Log.d("asd",""+listView_videos.getCount());
            }
        }

    }

}
