package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.d(TAG, "Network is connected");
                Toast.makeText(context, "Соединение восстановлено", Toast.LENGTH_SHORT).show();
                // Выполните действия, когда сеть подключена
            } else {
                Log.d(TAG, "Network is disconnected");
                Toast.makeText(context, "Соединение потеряно", Toast.LENGTH_SHORT).show();
                // Выполните действия, когда сеть отключена
            }
        }
    }
}