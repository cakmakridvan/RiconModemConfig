package main.application;

import android.app.Application;

import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 27/06/18.
 */

public class MyApplicaiton extends Application {

    private static MyApplicaiton mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplicaiton getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityChangeReceiver.ConnectivityReceiverListener listener) {
        ConnectivityChangeReceiver.connectivityReceiverListener = listener;
    }
}
