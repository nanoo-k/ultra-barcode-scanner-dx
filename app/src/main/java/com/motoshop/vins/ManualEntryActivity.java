package com.motoshop.vins;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.varvet.barcodereadersample.MyApplication;
import com.varvet.barcodereadersample.R;

import java.io.IOException;
import java.net.URL;

import mshttp.LoginActivity;
import mshttp.utilities.NetworkUtils;
import mshttp.utilities.PreferenceData;

/**
 * Created by mvalencia on 3/16/17.
 */

public class ManualEntryActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;
    private TextView mDecodedVinResultTextView;
    private TextView mErrorMessageDisplay;
    private TextView mVinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entry);


        ((MyApplication) getApplication()).startTracking();

        /* Save references to onscreen elements */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);
        mVinEditText = (TextView) findViewById(R.id.vin_edit_text);
        mDecodedVinResultTextView = (TextView) findViewById(R.id.decode_vin_result_text_view);



        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

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

    protected void logout () {
        PreferenceData.clearLoggedInUser(getApplicationContext());
        PreferenceData.clearJwt(getApplicationContext());

        navigateToLogin();
    }

    /* Navigate to login page */
    protected void navigateToLogin () {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void decodeVin (View view) {
        String vin = mVinEditText.getText().toString();

        Log.i("decodeVin: ", vin);

        decodeVinRequest(vin);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link DecodeVinTask}
     */
    private void decodeVinRequest(String vin) {

//        String jwt = PreferenceData.getJwt(this.getApplicationContext());

        URL decodeVinUrl = NetworkUtils.buildDecodeVinUrl(vin);

        mDecodedVinResultTextView.setText(decodeVinUrl.toString());
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
//            Log.i("onPreExecute", "MADE IT.");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
//            Log.i("doInBackground", "MADE IT to doInBackground.");

            URL recentVinsUrl = params[0];
            String vinResults = null;
            try {
                vinResults = NetworkUtils.decodeVin(recentVinsUrl, context);
            } catch (IOException e) {
//                Log.i("doInBackground", "exception.");

                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return vinResults;
        }

        @Override
        protected void onPostExecute(String vinResults) {

//            Log.i("onPostExecute: ", "MADE IT TO POST EXCECUTE");

            /* If VIN got decoded and we got data from server... */
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (vinResults != null && vinResults != "closed") {
                Log.i("onPostExecute", vinResults);

                showJsonDataView();
                mDecodedVinResultTextView.setText(vinResults);

                sendAnalyticsHit("BarcodeSuccess", "BarcodeSuccess", "BarcodeSuccess");


            }
            /* Else error decoding */
            else {
                sendAnalyticsHit("BarcodeFail", "BarcodeFail", "BarcodeFail");
//
                Log.i("onPostExecute", "Null.");
//                showErrorMessage();
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
        mDecodedVinResultTextView.setVisibility(View.VISIBLE);
    }
}
