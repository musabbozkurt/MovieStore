package com.moviestoremb.musabbozkurt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moviestoremb.musabbozkurt.a150111022_hw3.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MovieDetails extends Activity
{
    private long rowID;
    private TextView titleTextView;
    private TextView directorTextView;
    private TextView releaseYearTextView;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_movie_details);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        directorTextView = (TextView) findViewById(R.id.directorTextView);
        releaseYearTextView = (TextView) findViewById(R.id.releaseYearTextView);

        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(MovieCursor.ROW_ID);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(searchTrailerButtonClicked);
        MobileAds.initialize(this, "ca-app-pub-7816554778639642~2263961642");

        mAdView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

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
    public View.OnClickListener searchTrailerButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String searchedMovie= (String) titleTextView.getText();
            String urlString = getString(R.string.searchURL) + searchedMovie;
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            startActivity(webIntent);
        } // end method onClick
    };

    @Override
    protected void onResume()
    {
        super.onResume();

        new LoadMovieTask().execute(rowID);
    }

    private class LoadMovieTask extends AsyncTask<Long, Object, Cursor>
    {
        DBConnector DBConnector = new DBConnector(MovieDetails.this);

        @Override
        protected Cursor doInBackground(Long... params)
        {
            DBConnector.open();

            return DBConnector.getOneMovie(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst();

            int titleIndex = result.getColumnIndex("title");
            int directorIndex = result.getColumnIndex("director");
            int releaseYearIndex = result.getColumnIndex("releaseYear");

            titleTextView.setText(result.getString(titleIndex));
            directorTextView.setText(result.getString(directorIndex));
            releaseYearTextView.setText(result.getString(releaseYearIndex));

            result.close();
            DBConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.updateItem:
                Intent addEditMovie = new Intent(this, AddMovie.class);
                addEditMovie.putExtra(MovieCursor.ROW_ID, rowID);
                addEditMovie.putExtra("title", titleTextView.getText());
                addEditMovie.putExtra("director", directorTextView.getText());
                addEditMovie.putExtra("releaseYear", releaseYearTextView.getText());
                startActivity(addEditMovie);
                return true;
            case R.id.deleteItem:
                deleteMovie();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMovie()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetails.this);

        builder.setTitle(R.string.confirmMovieTitle);
        builder.setMessage(R.string.confirmMessage);

        builder.setPositiveButton(R.string.button_yes,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DBConnector DBConnector = new DBConnector(MovieDetails.this);

                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Long... params)
                                    {
                                        DBConnector.deleteMovie(params[0]);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        finish();
                                    }
                                };
                        deleteTask.execute(new Long[] { rowID });
                    } // end method onClick
                }
        );

        builder.setNegativeButton(R.string.button_no, null);
        builder.show();
    }
}