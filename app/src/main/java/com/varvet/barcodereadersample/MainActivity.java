package com.varvet.barcodereadersample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.motoshop.ocrreader.OcrCaptureActivity;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import mshttp.LoginActivity;
import mshttp.utilities.NetworkUtils;
import mshttp.utilities.PreferenceData;
import com.motoshop.vins.Vin;
import com.motoshop.vins.ManualEntryActivity;
import com.motoshop.vins.VinsAdapter;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private static final int NUM_LIST_ITEMS = 50;
    private VinsAdapter mAdapter;
    private RecyclerView mVinsList;

    private TextView mResultTextView;
    private TextView mErrorMessageDisplay;
    private TextView mDecodedVinTextView;
    private ListView mRecentVinsListView;
    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((MyApplication) getApplication()).startTracking();

        /* Save references to onscreen elements */
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mResultTextView = (TextView) findViewById(R.id.result_textview);
        mDecodedVinTextView = (TextView) findViewById(R.id.decode_vin_result_textview);
//        mRecentVinsListView = (ListView) findViewById(R.id.recent_vins_list_view);


        /* Set up the appbar as a toolbar (docs recommend this for best compatibility */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Button scanOcrButton = (Button) findViewById(R.id.scan_ocr_button);
        scanOcrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Button manualEntryButton = (Button) findViewById(R.id.manual_entry_button);
        manualEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManualEntryActivity.class);
                startActivity(intent);
            }
        });

        vibrate(500);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User wants to logout, log them out.
                logout();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /* onStart, onResume */
    @Override
    protected void onPostResume() {

        /* Check if user is logged in */
        getRecentVins();
//        decodeVinRequest("3FA6P0K93FR226629");
//        boolean isLoggedIn = PreferenceData.getUserLoggedInStatus(this.getApplicationContext());
//        if (!isLoggedIn) {
//
//            navigateToLogin();
//        }
//        else {
//            super.onPostResume();
//        }
        super.onPostResume();
    }

    /* Navigate to login page */
    protected void navigateToLogin () {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    protected void logout () {
        PreferenceData.clearLoggedInUser(getApplicationContext());
        PreferenceData.clearJwt(getApplicationContext());

        navigateToLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    mResultTextView.setText(barcode.displayValue);

                    /* Vibrate phone and decode this Vin */
                    vibrate(500);
                    decodeVinRequest(barcode.displayValue);

                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void vibrate (int miliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        /* Vibrate only if vibrator is available */
        if (vibrator.hasVibrator()) vibrator.vibrate(miliseconds);
    }

//    protected void decodeVin(String vin) {
//        URL decodeVinUrl = NetworkUtils.buildDecodeVinUrl(vin);
//
////        mUrlDisplayTextView.setText(decodeVinUrl.toString());
//        new DecodeVinTask(this.getApplicationContext()).execute(decodeVinUrl);
//    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GetRecentVinsTask}
     */
    private void decodeVinRequest(String vin) {

        String jwt = PreferenceData.getJwt(this.getApplicationContext());

        URL decodeVinUrl = NetworkUtils.buildDecodeVinUrl(vin);

        mDecodedVinTextView.setText(decodeVinUrl.toString());
        new DecodeVinTask(this.getApplicationContext()).execute(decodeVinUrl);

    }

    public class DecodeVinTask extends AsyncTask<URL, Void, String> {

        /* We need the app context available for these callback functions, so
        ensure that we set it when calling this task */
        private Context context;
        public DecodeVinTask (Context c){
            context = c;
        }

        @Override
        protected void onPreExecute() {
            Log.i("onPreExecute", "MADE IT.");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i("doInBackground", "MADE IT.");

            URL recentVinsUrl = params[0];
            String vinResults = null;
            try {
                vinResults = NetworkUtils.decodeVin(recentVinsUrl, context);
            } catch (IOException e) {
                Log.i("doInBackground", "exception.");

                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return vinResults;
//            return "true";
        }

        @Override
        protected void onPostExecute(String vinResults) {

            /* If VIN got decoded and we got data from server... */
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (vinResults != null && vinResults != "closed") {
                Log.i("onPostExecute", vinResults);

                showJsonDataView();
                mDecodedVinTextView.setText(vinResults);

                sendAnalyticsHit("BarcodeSuccess", "BarcodeSuccess", "BarcodeSuccess");


            }
            /* Else error decoding */
            else {
                sendAnalyticsHit("BarcodeFail", "BarcodeFail", "BarcodeFail");

                Log.i("onPostExecute", "Null.");
                showErrorMessage();
            }
        }
    }

    /* Implement these where we want and then set autotracking to false (res/xml/tracker_settings) */
    private void sendAnalyticsHit (String category, String action, String label) {
        // Get the tracker
        Tracker tracker = ((MyApplication) getApplication()).getTracker();

        // Send the hit
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }


    public void getRecentVins () {
        boolean isLoggedIn = PreferenceData.getUserLoggedInStatus(this.getApplicationContext());

        /* If user doesn't have a JWT, we can't make the request */
        if (!isLoggedIn) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            String text = "You are not logged in. Please log in.";

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            navigateToLogin();

        } else {
            makeRecentVinsRequest();
        }

    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GetRecentVinsTask}
     */
    private void makeRecentVinsRequest() {

        String jwt = PreferenceData.getJwt(this.getApplicationContext());

        URL recentVinsUrl = NetworkUtils.buildRecentVinsUrl();

        new GetRecentVinsTask(this.getApplicationContext()).execute(recentVinsUrl);

    }

    public class GetRecentVinsTask extends AsyncTask<URL, Void, String> {

        /* We need the app context available for these callback functions, so
        ensure that we set it when calling this task */
        private Context context;
        public GetRecentVinsTask (Context c){
            context = c;
        }

        @Override
        protected void onPreExecute() {
            Log.i("onPreExecute", "MADE IT.");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i("doInBackground", "MADE IT.");

            URL recentVinsUrl = params[0];
            String vinResults = null;
            try {
                vinResults = NetworkUtils.getRecentVins(recentVinsUrl, context);
            } catch (IOException e) {
                Log.i("doInBackground", "exception.");

                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return vinResults;
//            return "true";
        }

        @Override
        protected void onPostExecute(String vinResults) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (vinResults != null) {
                Log.i("onPostExecute", vinResults);

//                showJsonDataView();
//                mRecentVinsTextView.setText(vinResults);


                Gson gson = new Gson();

                /* This is a pattern for gson parsing an array of some kind of object */
                /* First you determine the listType of the object */
                Type listType = new TypeToken<List<Vin>>(){}.getType();
                /* Then you say you're going to parse to create a list of that thing, assigning to
                 * gson the listType */
                List<Vin> vinList = gson.fromJson(vinResults, listType);

//                Vin[] vinList = gson.fromJson(vinResults, Vin[].class);


//                VinListAdapter adapter = new VinListAdapter(context, R.layout.vin_management_list, vinList);

//                mRecentVinsListView.setAdapter(adapter);

                mVinsList = (RecyclerView) findViewById(R.id.recyclerview_vins);

                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                mVinsList.setLayoutManager(layoutManager);

                mVinsList.setHasFixedSize(true);

                mAdapter = new VinsAdapter(vinList);

                mVinsList.setAdapter(mAdapter);

            } else {
                Log.i("onPostExecute", "Null.");
                showErrorMessage();
            }
        }
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
//        mRecentVinsListView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
//        mRecentVinsListView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


//    @Override
//    public void onListItemClick(int clickedItemIndex) {
//        if (mToast != null) {
//            mToast.cancel();
//        }
//
//        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
//        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
//
//        mToast.show();
//    }

}
