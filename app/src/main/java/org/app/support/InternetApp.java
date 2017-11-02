package org.app.support;

/**
 * Created by patel on 6/1/2017.
 */

public class InternetApp extends AppFonts {
    private static InternetApp mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized InternetApp getInstance(){
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
