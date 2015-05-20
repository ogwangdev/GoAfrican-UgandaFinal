package awiidev.gdggulu.com.goafrican_uganda.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class DishDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "dishes.db";

    // If you change the database schema, you must increment the database version.
    public DishDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table if not exists " + DishContract.DishEntry.DISH_TABLE + " ( " +
                        DishContract.DishEntry.DISH_COLUMN_DISH_ID + " integer primary key autoincrement, " +
                        DishContract.DishEntry.DISH_COLUMN_DISH_KEY + " Long," +
                        DishContract.DishEntry.DISH_COLUMN_DISH_NAME + " text," +
                        DishContract.DishEntry.DISH_COLUMN_DESCRIPTION + " text, " +
                        DishContract.DishEntry.DISH_COLUMN_INGREDIENTS + " text, " +
                        DishContract.DishEntry.DISH_COLUMN_STEPS + " text, " +
                        DishContract.DishEntry.DISH_COLUMN_IMAGE + " text, " +
                        DishContract.DishEntry.DISH_COLUMN_VIDEO + " text)"
        );

        sqLiteDatabase.execSQL("create table if not exists " + DishContract.CommentEntry.COMMENT_TABLE + " ( " +
                        DishContract.CommentEntry.COMMENT_COLUMN_COMMENT_ID + " integer primary key autoincrement, " +
                        DishContract.CommentEntry.COMMENT_COLUMN_DISH_KEY + " Long," +
                        DishContract.CommentEntry.COMMENT_COLUMN_USER + " text, " +
                        DishContract.CommentEntry.COMMENT_COLUMN_COMMENT + " text, " +
                        DishContract.CommentEntry.COMMENT_COLUMN_RATING + " Long, " +
                        " FOREIGN KEY (" + DishContract.CommentEntry.COMMENT_COLUMN_DISH_KEY + ") REFERENCES " +
                        DishContract.DishEntry.DISH_TABLE + " (" + DishContract.DishEntry.DISH_COLUMN_DISH_KEY + "))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DishContract.DishEntry.DISH_TABLE + "");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DishContract.CommentEntry.COMMENT_TABLE + "");
        onCreate(sqLiteDatabase);
    }

    public void saveReview(Long dishID, String username, String comment, Long rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues commentValues = new ContentValues();

        commentValues.put(DishContract.CommentEntry.COMMENT_COLUMN_DISH_KEY, dishID);
        commentValues.put(DishContract.CommentEntry.COMMENT_COLUMN_USER, username);
        commentValues.put(DishContract.CommentEntry.COMMENT_COLUMN_COMMENT, comment);
        commentValues.put(DishContract.CommentEntry.COMMENT_COLUMN_RATING, rating);

        db.insert(DishContract.CommentEntry.COMMENT_TABLE, null, commentValues);
    }


    public List<DishRecord> findDish(String dishName) {
        List<DishRecord> dishRecordList = new ArrayList<>();
        String query = "Select * FROM " + DishContract.DishEntry.DISH_TABLE +
                " WHERE " + DishContract.DishEntry.DISH_COLUMN_DISH_NAME + " =  \"" + dishName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                DishRecord dish = new DishRecord(null, null, null, null, null);
                dish.setDishID(cursor.getLong(0));
                dish.setTitle(cursor.getString(1));
                dish.setDescription(cursor.getString(2));
                dish.setIngredients(cursor.getString(3));
                dish.setSteps(cursor.getString(4));
                dishRecordList.add(dish);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dishRecordList;
    }

    public Cursor searchDish(String dishName) {
        String query = "Select * FROM " + DishContract.DishEntry.DISH_TABLE +
                " WHERE " + DishContract.DishEntry.DISH_COLUMN_DISH_NAME + " LIKE  '%" + dishName + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getReviews() {
        String query = "SELECT * FROM " + DishContract.CommentEntry.COMMENT_TABLE +
                " ORDER BY " + DishContract.CommentEntry.COMMENT_COLUMN_COMMENT_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}

