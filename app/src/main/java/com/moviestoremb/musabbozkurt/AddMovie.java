package com.moviestoremb.musabbozkurt;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.moviestoremb.musabbozkurt.a150111022_hw3.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AddMovie extends Activity
{
    private long rowID;
    private EditText titleEditText;
    private EditText directorEditText;
    private EditText releaseYearEditText;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_movie);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        directorEditText = (EditText) findViewById(R.id.directorEditText);
        releaseYearEditText = (EditText) findViewById(R.id.releaseYearEditText);


        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            titleEditText.setText(extras.getString("title"));
            directorEditText.setText(extras.getString("director"));
            releaseYearEditText.setText(extras.getString("releaseYear"));

        }
        Button saveMovieButton = (Button) findViewById(R.id.saveMovieButton);
        saveMovieButton.setOnClickListener(saveMovieButtonClicked);        MobileAds.initialize(this, "ca-app-pub-7816554778639642~2263961642");

        mAdView = (AdView) findViewById(R.id.adView);
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
    View.OnClickListener saveMovieButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (titleEditText.getText().length() != 0)
            {
                AsyncTask<Object, Object, Object> saveMovieTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params)
                            {
                                saveMovie();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                finish();
                            }
                        };
                saveMovieTask.execute((Object[]) null);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMovie.this);
                builder.setTitle(R.string.errorMovieName);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show();
            }
        } // end method onClick
    };
    private void saveMovie()
    {
        DBConnector DBConnector = new DBConnector(this);

        if (getIntent().getExtras() == null)
        {
            DBConnector.insertMovie(
                    titleEditText.getText().toString(),
                    directorEditText.getText().toString(),
                    releaseYearEditText.getText().toString());
        }
        else
        {
            DBConnector.updateMovie(rowID,
                    titleEditText.getText().toString(),
                    directorEditText.getText().toString(),
                    releaseYearEditText.getText().toString());
        }
    }
}