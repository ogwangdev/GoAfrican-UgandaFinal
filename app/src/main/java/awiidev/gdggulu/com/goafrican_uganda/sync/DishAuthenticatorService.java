package awiidev.gdggulu.com.goafrican_uganda.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Eng. Fidy on 1/8/2015.
 */
public class DishAuthenticatorService extends Service {
    private DishAuthenticator rAuthenticator;

    @Override
    public void onCreate() {
        rAuthenticator = new DishAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return rAuthenticator.getIBinder();

    }
}
