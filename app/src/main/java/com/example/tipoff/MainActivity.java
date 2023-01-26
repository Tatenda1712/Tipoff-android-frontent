package com.example.tipoff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tipoff.fragments.home;

import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    public static OkHttpClient client;
    CookieManager handler = new CookieManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient().newBuilder()
                .cookieJar(new JavaNetCookieJar(handler))
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES).build();
        getSupportFragmentManager().beginTransaction().replace(R.id.replacee, new home()).commit();

    }
}