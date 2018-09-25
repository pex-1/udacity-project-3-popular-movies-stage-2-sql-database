package udacity.popularmoviesstage1.database;

import android.provider.BaseColumns;

public class FavoritesContract {

    public static final class FavoriteMovie implements BaseColumns{

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_TRAILER_TYPE = "trailerType";
        public static final String COLUMN_TRAILER_LINK = "trailerLink";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        /*public static final String COLUMN_
        public static final String COLUMN_
        public static final String COLUMN_*/

    }
}
