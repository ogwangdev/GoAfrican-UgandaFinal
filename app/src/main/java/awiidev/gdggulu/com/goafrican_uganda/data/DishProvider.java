package awiidev.gdggulu.com.goafrican_uganda.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class DishProvider extends ContentProvider {

    DishDbHelper dbHelper;
    private static final UriMatcher rUriMatcher = buildUriMatcher();
    private static final int DISHES = 400;
    private static final int DISH_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DishContract.CONTENT_AUTHORITY;

        //Uri types
        matcher.addURI(authority, DishContract.DISH_PATH, DISHES);
        matcher.addURI(authority, DishContract.DISH_PATH + "/#", DISH_ID);

        return matcher;
    }



    @Override
    public boolean onCreate() {
        dbHelper = new DishDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (rUriMatcher.match(uri)) {
            case DISH_ID: {
                retCursor = dbHelper.getReadableDatabase().query(
                        DishContract.DishEntry.DISH_TABLE,
                        projection,
                        DishContract.DishEntry._ID + " = " +
                                ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case DISHES: {
                retCursor = dbHelper.getReadableDatabase().query(
                        DishContract.DishEntry.DISH_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = rUriMatcher.match(uri);

        switch (match) {
            case DISHES:
                return DishContract.DishEntry.CONTENT_TYPE;
            case DISH_ID:
                return DishContract.DishEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        Uri returnUri;

        if (match == DISHES) {
            Long _id = db.insert(DishContract.DishEntry.DISH_TABLE, null, contentValues);
            if ( _id > 0 )
                returnUri = DishContract.DishEntry.buildDishUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        switch (match) {
//            case DISHES:{
//                long _id = db.insert(DishContract.DishEntry.TABLE_NAME, null, contentValues);
//            if ( _id > 0 )
//                returnUri = DishContract.DishEntry.buildDishUri(_id);
//            else
//                throw new android.database.SQLException("Failed to insert row into " + uri);
//            break;
//            }
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        int rowsDeleted;

        if (match == DISHES) {
            rowsDeleted = db.delete(
                    DishContract.DishEntry.DISH_TABLE, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        switch (match) {
//
//            case DISHES:
//                rowsDeleted = db.delete(
//                        DishContract.DishEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        int rowsUpdated;

        if (match == DISHES) {
            rowsUpdated = db.update(DishContract.DishEntry.DISH_TABLE, values, selection,
                    selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        switch (match) {
//
//            case DISHES:
//                rowsUpdated = db.update(DishContract.DishEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);

        if (match == DISHES) {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(DishContract.DishEntry.DISH_TABLE, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        } else {
            return super.bulkInsert(uri, values);
        }

//        switch (match) {
//            case DISHES:
//                db.beginTransaction();
//                int returnCount = 0;
//                try {
//                    for (ContentValues value : values) {
//                        long _id = db.insert(DishContract.DishEntry.TABLE_NAME, null, value);
//                        if (_id != -1) {
//                            returnCount++;
//                        }
//                    }
//                    db.setTransactionSuccessful();
//                } finally {
//                    db.endTransaction();
//                }
//                getContext().getContentResolver().notifyChange(uri, null);
//                return returnCount;
//            default:
//                return super.bulkInsert(uri, values);
//        }
    }
}
