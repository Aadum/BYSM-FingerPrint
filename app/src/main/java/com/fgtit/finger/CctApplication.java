package com.fgtit.finger;

import android.app.Application;
import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CctApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tasky.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}