package com.sublimeIT.hisabkhata.Offline;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineDB extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
