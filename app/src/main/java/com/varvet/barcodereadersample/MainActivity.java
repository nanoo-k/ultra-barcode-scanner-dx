package com.varvet.barcodereadersample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;

import java.io.IOException;
import java.net.URL;

import mshttp.LoginActivity;
import mshttp.utilities.NetworkUtils;
import mshttp.utilities.PreferenceData;



public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private TextView mResultTextView;
    private TextView mRecentVinsTextView;
    private TextView mErrorMessageDisplay;
    private TextView mDecodedVinTextView;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((MyApplication) getApplication()).startTracking();

        /* Save references to onscreen elements */
//        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);
//        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
//        mRecentVinsTextView = (TextView) findViewById(R.id.recent_vins_text_view);
        mResultTextView = (TextView) findViewById(R.id.result_textview);
        mDecodedVinTextView = (TextView) findViewById(R.id.decode_vin_result_textview);


        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }

    /* onStart, onResume */
    @Override
    protected void onPostResume() {

        /* Check if user is logged in */
//        getRecentVins();
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

                    Log.i("FIRST", "ONETT");

                    /* Decode this Vin */
                    decodeVinRequest(barcode.displayValue);

                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
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

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (vinResults != null && vinResults != "closed") {
                Log.i("onPostExecute", vinResults);

                showJsonDataView();
                mDecodedVinTextView.setText(vinResults);

            } else {
                Log.i("onPostExecute", "Null.");
                showErrorMessage();
            }
        }
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

        mRecentVinsTextView.setText(recentVinsUrl.toString());
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
            if (vinResults != null && vinResults != "closed") {
                Log.i("onPostExecute", vinResults);

                showJsonDataView();
                mRecentVinsTextView.setText(vinResults);

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
        mRecentVinsTextView.setVisibility(View.VISIBLE);
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
        mRecentVinsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
