package awiidev.gdggulu.com.goafrican_uganda.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.MyApi;
import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.model.DishBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import awiidev.gdggulu.com.goafrican_uganda.MainActivity;
import awiidev.gdggulu.com.goafrican_uganda.R;
import awiidev.gdggulu.com.goafrican_uganda.data.DishContract;
import awiidev.gdggulu.com.goafrican_uganda.data.DishRecord;

/**
 * Created by Eng FIDY on 1/8/2015.
 */
public class DishSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = DishSyncAdapter.class.getSimpleName();
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private static final int DISH_NOTIFICATION_ID = 3004;


    private static final String[] NOTIFY_DISH_PROJECTION = new String[] {
            DishContract.DishEntry.DISH_COLUMN_DISH_ID,
            DishContract.DishEntry.DISH_COLUMN_DISH_KEY,
            DishContract.DishEntry.DISH_COLUMN_DISH_NAME,
            DishContract.DishEntry.DISH_COLUMN_DESCRIPTION
    };

    public DishSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        MyApi myApiService = null;
        if (myApiService == null) {

            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://focused-century-87702.appspot.com/_ah/api/");
            myApiService = builder.build();
        }

        try {
              List<DishBean> remoteDishes = myApiService.getDishes().execute().getItems();

            if (remoteDishes != null) {
                List<DishRecord> dishList = new ArrayList<DishRecord>();
                for (DishBean dishBean : remoteDishes) {
                    dishList.add(new DishRecord(dishBean.getId(), dishBean.getTitle(), dishBean.getDescription(),
                            dishBean.getIngredients(), dishBean.getSteps()));
                }

                ContentValues dishValues = new ContentValues();
                for (int i = 0; i < dishList.size(); i++) {
                    DishRecord dishRecord = dishList.get(i);

                    Cursor cursor = getContext().getContentResolver().query(
                            DishContract.DishEntry.CONTENT_URI,
                            new String[]{DishContract.DishEntry.DISH_COLUMN_DISH_KEY},
                            DishContract.DishEntry.DISH_COLUMN_DISH_KEY + " = ?",
                            new String[]{dishRecord.getDishID().toString()},
                            null);
                    if (cursor.moveToFirst()) {
                        int dishIdIndex = cursor.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_DISH_KEY);
//                    return cursor.getLong(dishIdIndex);
                    } else {

                        dishValues.put(DishContract.DishEntry.DISH_COLUMN_DISH_KEY, dishRecord.getDishID());
                        dishValues.put(DishContract.DishEntry.DISH_COLUMN_DISH_NAME, dishRecord.getTitle());
                        dishValues.put(DishContract.DishEntry.DISH_COLUMN_DESCRIPTION, dishRecord.getDescription());
                        dishValues.put(DishContract.DishEntry.DISH_COLUMN_INGREDIENTS, dishRecord.getIngredients());
                        dishValues.put(DishContract.DishEntry.DISH_COLUMN_STEPS, dishRecord.getSteps());

//            Uri dishInsertUri =
                        getContext().getContentResolver().insert(DishContract.DishEntry.CONTENT_URI, dishValues);
                    }
notifyNewDish();
                }
                Log.d(LOG_TAG, "DishFetchTask Complete. " + dishList.size() + " Inserted");
            }

        } catch (IOException e) {
            Log.e(DishSyncAdapter.class.getSimpleName(), "Loading of new dish failed.Please ensure that you are connected to the internet.", e);
        }

    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        DishSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void notifyNewDish() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {

                Uri dishUri = DishContract.DishEntry.CONTENT_URI;
                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(dishUri, NOTIFY_DISH_PROJECTION, null, null, null);

                if (cursor.moveToFirst()) {

                    int dishId = cursor.getInt(0);
                    Long dishKey = cursor.getLong(1);
                    String dishName = cursor.getString(2);
                    String desc = cursor.getString(3);

//                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            dishName,
                            desc );

                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // DISH_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(DISH_NOTIFICATION_ID, mBuilder.build());


                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
            }
        }

    }



