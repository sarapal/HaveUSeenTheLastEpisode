package it.asg.hustle.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by gbyolo on 9/17/15.
 */
public class CheckConnection {

    public static synchronized boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return connected;
    }
}
