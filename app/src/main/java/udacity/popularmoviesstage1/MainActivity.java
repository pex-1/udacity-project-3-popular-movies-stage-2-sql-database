package udacity.popularmoviesstage1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import udacity.popularmoviesstage1.database.FavoritesDbHelper;
import udacity.popularmoviesstage1.model.Movie;
import udacity.popularmoviesstage1.utils.JsonUtils;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private ProgressBar progressBar;

    private ContentResolver contentResolver;

    private static String apiKey = BuildConfig.ApiKey;
    private Context context;

    private FavoritesDbHelper favoritesDbHelper;

    private Boolean refresh;

    private int currentState = R.id.popularity_sort;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.popularity_sort:
                refresh = false;
                runAsyncTask(getString(R.string.popularity_sort));
                currentState = R.id.popularity_sort;
                break;
            case R.id.rating_sort:
                refresh = false;
                runAsyncTask(getString(R.string.rating_sort));
                currentState = R.id.rating_sort;
                break;
            case R.id.favorite:
                refresh = true;
                currentState = R.id.favorite;
                displayFavorites();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("state", currentState);
    }

    private void displayFavorites() {
        favoritesDbHelper = new FavoritesDbHelper(this);
        List<Movie> movies = favoritesDbHelper.getFavorites();
        Movie[] favorites = new Movie[movies.size()];
        //Log.d("SIZE", movies.size() + "");
        for(int i = 0; i<movies.size(); i++){
            favorites[i] = movies.get(i);/*
            Log.d("Poster: ", movies.get(i).getPosterPath());
            Log.d("Title: ", movies.get(i).getTitle());
            Log.d("Release Date: ", movies.get(i).getReleaseDate());
            Log.d("Overview: ", movies.get(i).getOverview());*/
        }
        gridView.setAdapter(new GridAdapter(context, favorites));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //coming back from DetailActivity
    @Override
    protected void onRestart() {
        super.onRestart();
        if(refresh) displayFavorites();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = getContentResolver();
        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.movies_toolbar);
        setSupportActionBar(toolbar);

        gridView = findViewById(R.id.grid);
        progressBar = findViewById(R.id.progressBar);

        //refresh movies coming back from detail view
        refresh = false;


        if(savedInstanceState != null){
            currentState = savedInstanceState.getInt("state");
        }
            if(currentState == R.id.rating_sort){
                runAsyncTask(getString(R.string.rating_sort));
            }else if(currentState == R.id.favorite){
                displayFavorites();
            }else{
                runAsyncTask(getString(R.string.popularity_sort));
            }




        gridView.setOnItemClickListener(detailViewListener);
    }

    private void runAsyncTask(String sort){
        if(isNetworkConnected()){
            LoadMoviesAsyncTask task = new LoadMoviesAsyncTask(this);
            task.execute(sort);
        }
        else{
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.mainLayout), "No network connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
        }
    }



    private final GridView.OnItemClickListener detailViewListener = new GridView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Movie movie = (Movie) adapterView.getItemAtPosition(i);

            //Toast.makeText(MainActivity.this, movie.getOverview(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            //TODO: parcelable
            intent.putExtra(getString(R.string.movieObject), movie);

            startActivity(intent);
        }
    };



    public static class LoadMoviesAsyncTask extends AsyncTask<String, Void, Movie[]>{
        private WeakReference<MainActivity> activityWeakReference;

        LoadMoviesAsyncTask(MainActivity activity){
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Movie[] doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String moviesJsonString = null;

            try{
                URL url = getApiUrl(strings);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();

                if(inputStream == null){
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while( (line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }

                moviesJsonString = stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(httpURLConnection != null) httpURLConnection.disconnect();
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try{
                MainActivity activity = activityWeakReference.get();
                return JsonUtils.parseMovieJson(moviesJsonString, activity.context);
            }
            catch (JSONException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setVisibility(View.GONE);
            activity.gridView.setAdapter(new GridAdapter(activity.getApplicationContext(), movies));
        }
    }



    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private static URL getApiUrl(String[] parameters) throws MalformedURLException {
        final String BASE = "http://api.themoviedb.org/3" + parameters[0] + "?api_key=";

        Uri builtUri = Uri.parse(BASE + apiKey).buildUpon().build();


        //Log.e("URI", builtUri.toString());
        return new URL(builtUri.toString());
    }
}
