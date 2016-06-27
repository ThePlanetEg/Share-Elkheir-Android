package eg.com.theplanet.akram.utils;

import android.app.Application;
import android.location.Address;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by georgenaiem on 5/26/16.
 */
public class App extends Application {

    private static App mInstance;
    public Address searchResultAddress; // hardcoded coz there's no time
    public static App getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
    }
}
