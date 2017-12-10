package com.aufthesis.searchidiom4;

/*
 * Created by a2035210 on 2017/10/27.
 */
import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MyApp extends Application {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private static FirebaseAnalytics m_FirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64731121-11"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        m_FirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    public static FirebaseAnalytics getFirebaseAnalytics() {
        return m_FirebaseAnalytics;
    }
}

