package awiidev.gdggulu.com.goafrican_uganda.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class DishContract {

    public static final String CONTENT_AUTHORITY = "com.awiidev.gdggulu.goafrican_uganda";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DISH_PATH = "dish";

    public static class DishEntry implements BaseColumns {
        public static final String DISH_TABLE = "dishRecords";

        public static final String DISH_COLUMN_DISH_ID = "_id";
        public static final String DISH_COLUMN_DISH_KEY = "dishId";
        public static final String DISH_COLUMN_DISH_NAME = "dishName";
        public static final String DISH_COLUMN_DESCRIPTION= "description";
        public static final String DISH_COLUMN_INGREDIENTS = "ingredients";
        public static final String DISH_COLUMN_STEPS = "steps";
        public static final String DISH_COLUMN_IMAGE = "image";
        public static final String DISH_COLUMN_VIDEO = "video";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(DISH_PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + DISH_PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + DISH_PATH;

        public static Uri buildDishUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static class CommentEntry implements BaseColumns {
        public static final String COMMENT_TABLE = "comments";

        public static final String COMMENT_COLUMN_COMMENT_ID = "_id";
        public static final String COMMENT_COLUMN_DISH_KEY = "dish_id";
        public static final String COMMENT_COLUMN_USER = "username";
        public static final String COMMENT_COLUMN_COMMENT= "comment";
        public static final String COMMENT_COLUMN_RATING = "rating";
    }
}
