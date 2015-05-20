package awiidev.gdggulu.com.goafrican_uganda.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ZAC on 1/8/2015.
 */
public class DishSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static DishSyncAdapter rDishSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("DishSyncService", "onCreate - DishSyncService");
        synchronized (sSyncAdapterLock) {
            if (rDishSyncAdapter == null) {
                rDishSyncAdapter = new DishSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return rDishSyncAdapter.getSyncAdapterBinder();

    }
}
