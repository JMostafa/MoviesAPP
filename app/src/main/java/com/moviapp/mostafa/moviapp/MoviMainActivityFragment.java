package com.moviapp.mostafa.moviapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviMainActivityFragment extends Fragment {

    public MoviData [] moviArray;
    private MoviAdapter moviAdapter;
    public GridView grid;
    public View rootView;

    public MoviMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movi_main, container, false);
        grid = (GridView) rootView.findViewById(R.id.main_grid);
        //updateGrid();
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MoviData movi = moviArray[position];
                Bundle bundle =new Bundle();
                bundle.putString("Poster", movi.getPosterPath());
                bundle.putInt("Id", movi.getId());
                bundle.putString("Date", movi.getDate());
                bundle.putFloat("Rate", movi.getRate());
                bundle.putString("Title", movi.getTitle());
                bundle.putString("OverView", movi.getOverView());
                bundle.putString("Language", movi.getLanguage());
                bundle.putString("Backdrop", movi.getBackdropPath());
                if(MoviMainActivity.CheckTablet != true)
                {
                    Intent intent = new Intent(getActivity(), ActivityDetails.class);
                    Toast.makeText(getActivity(), movi.getTitle() ,Toast.LENGTH_SHORT).show();
                    intent.putExtra("Data", bundle);
                    startActivity(intent);
                }
                else
                {

                    ActivityDetailsFragment activityDetailsFragment= new ActivityDetailsFragment();
                    activityDetailsFragment.setArguments(bundle);
                    Toast.makeText(getActivity(), movi.getTitle() ,Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.detail_container,
                            activityDetailsFragment).commit();

                }
            }
        });

        return rootView;
    }

    public void onStart() {
        updateGrid();
        super.onStart();
    }

    public void updateGrid()
    {
        grid.setAdapter(null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Type =prefs.getString(getString(R.string.pref_Type_key),getString(R.string.pref_Type_Default));



        if(!Type.equals(getString(R.string.pref_Favorite_Favorite)))
        {
            //if(isNetworkAvailable())
            {
                FetchMoviesTask Task = new FetchMoviesTask();
                if(Type.equals(getString(R.string.pref_Type_Popular)))
                    Task.execute(getString(R.string.popular_URL));
                else
                    Task.execute(getString(R.string.topRated_URL));
            }
            //else
            //    Toast.makeText(getActivity(),"Network Not Available " ,Toast.LENGTH_SHORT).show();
        }
        else
        {
            getDataFromDataBase();
        }
    }

    public void getDataFromDataBase()
    {
        MoviDBHelper dataBase=new MoviDBHelper(getContext());
        MoviAdapterBitmap moviAdapterImage ;
        Cursor cursor = dataBase.selectAllRaw("moviData");
        int count = cursor.getCount();
        moviArray=new MoviData[count];
        Bitmap[] PosterPath =new Bitmap[count];
        for (int i=0; i<count ;i++)
        {
            cursor.moveToNext();
            MoviData moviData = new MoviData();
            moviData.setId(cursor.getInt(0));
            moviData.setTitle(cursor.getString(1));
            moviData.setDate(cursor.getString(2));
            moviData.setOverView(cursor.getString(3));
            moviData.setLanguage(cursor.getString(4));
            moviData.setPosterPath(cursor.getString(5));
            moviData.setRate((float) cursor.getDouble(6));
            moviArray[i]=moviData;
            PosterPath[i]=getPhoto(cursor.getBlob(7));
        }
        moviAdapterImage = new MoviAdapterBitmap(getActivity(),PosterPath);
        grid.setAdapter(moviAdapterImage);
        Toast.makeText(getActivity(),"Favorite movies " +count,Toast.LENGTH_SHORT).show();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        //return BitmapFactory.decodeByteArray(image, 0, image.length);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        return BitmapFactory.decodeStream(inputStream);
    }

    @Override
    public void onCreate(Bundle savedInstanceeState)
    {
        super.onCreate(savedInstanceeState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movi_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateGrid();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity() ,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,String[]> {

        private String[] getMoviFromJson(String moviJsonStr) throws JSONException {

            JSONObject MoviJson = new JSONObject(moviJsonStr);
            JSONArray Result = MoviJson.getJSONArray("results");
            String[] PosterPath =new String[20];
            moviArray=new MoviData[20];
            JSONObject movijsonobject;


            for(int i = 0; i < 20; i++) {
                MoviData movi =new MoviData();
                movijsonobject = Result.getJSONObject(i);
                movi.setId(movijsonobject.getInt("id"));
                movi.setPosterPath(movijsonobject.getString("poster_path"));
                movi.setDate(movijsonobject.getString("release_date"));
                movi.setTitle(movijsonobject.getString("title"));
                movi.setOverView(movijsonobject.getString("overview"));
                movi.setRate((float) movijsonobject.getDouble("vote_average"));
                movi.setBackdropPath(movijsonobject.getString("backdrop_path"));
                movi.setLanguage(movijsonobject.getString("original_language"));
                moviArray[i] = movi;
                PosterPath[i]=movijsonobject.getString("poster_path");
            }
            return PosterPath;
        }

        @Override
        protected String[] doInBackground(String... params) {

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
        protected  void onPostExecute(String[] result)
        {

            if(result != null && result.length != 0)
            {
                moviAdapter = new MoviAdapter(getActivity(),result);
                grid.setAdapter(moviAdapter);
                //for (int i=0;i<20;i++)
                    //moviAdapter(result[i]);
//                for (int i=0;i<20;i++)
//                {
//                    ImageView icon = (ImageView) getActivity().findViewById(R.id.poster_Image);
//                    //String PosterPath = moviArray[i].getPosterPath();
//                    Toast.makeText(getActivity(),"http://image.tmdb.org/t/p/w185/"+result[i],Toast.LENGTH_LONG).show();
//                    Picasso.with(moviAdapter.getContext()).load("http://image.tmdb.org/t/p/w185/"+result[i]).into(icon);
//
//                    //moviAdapter.add(icon);
//                }
//                //Toast.makeText(moviAdapter.getContext(),"555",Toast.LENGTH_SHORT);
//                grid.setAdapter(moviAdapter);
            }
            else
                Toast.makeText(getActivity(),"No Internet Connection !!!" ,Toast.LENGTH_SHORT).show();

        }

    }
}
