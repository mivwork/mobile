package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Handler handler;
    private Runnable checkConnectionRunnable;
    private String url = "http://192.168.0.103:8080/";
    private boolean isConnected = false;
    private boolean hasConnectedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        checkConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                new CheckUrlTask().execute(url);
                handler.postDelayed(this, 5000); // Проверять каждые 5 секунд
            }
        };

        handler.post(checkConnectionRunnable);
    }

    private class CheckUrlTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            String urlString = urls[0];
            return isUrlReachable(urlString);
        }

        @Override
        protected void onPostExecute(Boolean isReachable) {
            if (isReachable) {
                if (!isConnected) {
                    Log.d(TAG, "URL is reachable");
                    setContentView(R.layout.activity_main);
                    WebView myWebView = findViewById(R.id.webview);
                    WebSettings webSettings = myWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    myWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                            handler.proceed(); // Игнорировать SSL ошибки
                        }
                    });
                    myWebView.loadUrl(url);
                    isConnected = true;
                    hasConnectedOnce = true;
                }
            } else {
                if (isConnected) {
                    Log.d(TAG, "URL is not reachable");
                    Toast.makeText(MainActivity.this, "Соединение потеряно", Toast.LENGTH_SHORT).show();
                    isConnected = false;
                } else {
                    if (!hasConnectedOnce) {
                        setContentView(R.layout.error);
                        Button retryButton = findViewById(R.id.button);
                        retryButton.setOnClickListener(v -> new CheckUrlTask().execute(url));
                    }
                }
            }
        }
    }

    private boolean isUrlReachable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000); // 5 секунд на установление соединения
            urlConnection.setReadTimeout(5000); // 5 секунд на чтение данных
            urlConnection.setRequestMethod("HEAD"); // Используем метод HEAD для проверки соединения
            int responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkConnectionRunnable);
    }
}
