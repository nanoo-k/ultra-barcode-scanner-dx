package com.varvet.barcodereadersample;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by mvalencia on 3/14/17.
 */

public class MyApplication extends Application {
    public Tracker mTracker;

    // Get the tracker associated with this app
    public void startTracking() {

        // Initialize an Analytics tracker using a Google Analytics property ID.

        // Does the Tracker already exist?
        // If not, create it

        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            // Get the config data for the tracker
            mTracker = ga.newTracker(R.xml.tracker_settings);

            // Enable tracking of activities
            ga.enableAutoActivityReports(this);


            Log.i("startTracking: ", mTracker.toString());

            // Set the log level to verbose.
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker() {
        // Make sure the tracker exists
        startTracking();

        Log.i("getTracker: ", mTracker.toString());

        // Then return the tracker
        return mTracker;
    }

}

