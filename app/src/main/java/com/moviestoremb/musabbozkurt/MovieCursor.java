package com.moviestoremb.musabbozkurt;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.moviestoremb.musabbozkurt.a150111022_hw3.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MovieCursor extends ListActivity
{
    public static final String ROW_ID = "row_id";
    private ListView movieListView;
    private CursorAdapter movieAdapter;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        movieListView = getListView();
        movieListView.setOnItemClickListener(viewMovieListener);

        MobileAds.initialize(this, "ca-app-pub-7816554778639642~2263961642");

        String[] from = new String[] { "title" };
        Collections.sort(Arrays.asList(from), new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        int[] to = new int[] { R.id.movieTextView };
        movieAdapter = new SimpleCursorAdapter(MovieCursor.this, R.layout.movie_list_item, null, from, to);
        setListAdapter(movieAdapter);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7816554778639642/2721759669");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                super.onAdClosed();
                finish();
            }
        });

    }

    public void showInterstitial(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void onBackPressed() {
        showInterstitial();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        new GetMoviesTask().execute((Object[]) null);
    }

    @Override
    protected void onStop()
    {
        Cursor cursor = movieAdapter.getCursor();

        if (cursor != null)
            cursor.deactivate();

        movieAdapter.changeCursor(null);
        super.onStop();
    }

    private class GetMoviesTask extends AsyncTask<Object, Object, Cursor>
    {
        DBConnector DBConnector = new DBConnector(MovieCursor.this);

        @Override
        protected Cursor doInBackground(Object... params)
        {
            DBConnector.open();

            return DBConnector.getAllMovies();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            movieAdapter.changeCursor(result);
            DBConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent addNewMovie = new Intent(MovieCursor.this, AddMovie.class);
        startActivity(addNewMovie);
        return super.onOptionsItemSelected(item);
    }

    OnItemClickListener viewMovieListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3)
        {
            Intent viewMovie = new Intent(MovieCursor.this, MovieDetails.class);
            viewMovie.putExtra(ROW_ID, arg3);
            startActivity(viewMovie);
        } // end method onItemClick
    };
}