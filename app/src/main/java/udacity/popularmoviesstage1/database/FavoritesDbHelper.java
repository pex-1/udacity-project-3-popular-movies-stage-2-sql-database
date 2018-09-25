package udacity.popularmoviesstage1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import udacity.popularmoviesstage1.model.Movie;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites4.db";
    private static final int DATABASE_VERSION = 1;

    SQLiteOpenHelper helper;
    SQLiteDatabase database;

    public FavoritesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        database = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesContract.FavoriteMovie.TABLE_NAME + " (" +
                FavoritesContract.FavoriteMovie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesContract.FavoriteMovie.COLUMN_ID + " INTEGER, " +
                FavoritesContract.FavoriteMovie.COLUMN_TITLE + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_USER_RATING + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_TRAILER_TYPE + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_TRAILER_LINK + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_AUTHOR + " TEXT NOT NULL," +
                FavoritesContract.FavoriteMovie.COLUMN_CONTENT + " TEXT NOT NULL" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoriteMovie.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void deleteFavorite(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(FavoritesContract.FavoriteMovie.TABLE_NAME, FavoritesContract.FavoriteMovie.COLUMN_ID + "=" + id, null);
    }

    public void addFavorite(Movie movie){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoritesContract.FavoriteMovie.COLUMN_ID, movie.getId());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_TITLE, movie.getTitle());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_OVERVIEW, movie.getOverview());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_USER_RATING, movie.getVoteAverage());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_TRAILER_TYPE, movie.getTrailerType());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_TRAILER_LINK, movie.getTrailerLink());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_AUTHOR, movie.getAuthor());
        values.put(FavoritesContract.FavoriteMovie.COLUMN_CONTENT, movie.getContent());

        database.insert(FavoritesContract.FavoriteMovie.TABLE_NAME, null, values);
        database.close();
    }

    public List<Movie> getFavorites(){
        String[] columns = {
                FavoritesContract.FavoriteMovie._ID,
                FavoritesContract.FavoriteMovie.COLUMN_ID,
                FavoritesContract.FavoriteMovie.COLUMN_TITLE,
                FavoritesContract.FavoriteMovie.COLUMN_POSTER_PATH,
                FavoritesContract.FavoriteMovie.COLUMN_RELEASE_DATE,
                FavoritesContract.FavoriteMovie.COLUMN_OVERVIEW,
                FavoritesContract.FavoriteMovie.COLUMN_USER_RATING,
                FavoritesContract.FavoriteMovie.COLUMN_TRAILER_TYPE,
                FavoritesContract.FavoriteMovie.COLUMN_TRAILER_LINK,
                FavoritesContract.FavoriteMovie.COLUMN_AUTHOR,
                FavoritesContract.FavoriteMovie.COLUMN_CONTENT
        };

        String sort = FavoritesContract.FavoriteMovie._ID + " ASC";
        List<Movie> favorites = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(FavoritesContract.FavoriteMovie.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sort
        );

        if(cursor.moveToFirst()){
            do{
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_ID))));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_TITLE)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_POSTER_PATH)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_RELEASE_DATE)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_OVERVIEW)));
                movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_USER_RATING)));
                movie.setTrailerType(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_TRAILER_TYPE)));
                movie.setTrailerLink(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_TRAILER_LINK)));
                movie.setAuthor(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_AUTHOR)));
                movie.setContent(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_CONTENT)));
                //Log.d("Poster path(helper): ", cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoriteMovie.COLUMN_POSTER_PATH)));

                favorites.add(movie);
            }while (cursor.moveToNext());
            cursor.close();
            database.close();

        }
        return favorites;
    }


}

