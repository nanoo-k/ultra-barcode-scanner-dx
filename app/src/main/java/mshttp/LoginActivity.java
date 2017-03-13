package mshttp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.varvet.barcodereadersample.MainActivity;
import com.varvet.barcodereadersample.R;

import java.io.IOException;
import java.net.URL;

import mshttp.utilities.NetworkUtils;
import mshttp.utilities.PreferenceData;

//import static datafrominternet.utilities.PreferenceData.getJwt;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

//    @Override
//    public void onCreatePreferences(Bundle bundle, String s) {
//
//        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
//        addPreferencesFromResource(R.xml.pref_visualizer);
//
//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        mUsernameEditText = (EditText) findViewById(R.id.username);
        mPasswordEditText = (EditText) findViewById(R.id.password);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        /* Set default test username and pass */
        mUsernameEditText.setText("MANAGERKM");
        mPasswordEditText.setText("PASS8520");

//        mUsernameEditText.setText("KM@QA3");
//        mPasswordEditText.setText("PASS8520");

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
        mSearchResultsTextView.setVisibility(View.VISIBLE);
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
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class LoginTask extends AsyncTask<URL, Void, String> {
        private Context context;
        public LoginTask (Context c){
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

            URL searchUrl = params[0];
            String loginResults = null;
            try {
                loginResults = NetworkUtils.getJWT(searchUrl, context);

            } catch (IOException e) {
                Log.i("doInBackground", "exception.");

                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return loginResults;
        }

        @Override
        protected void onPostExecute(String loginResults) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (loginResults != null && loginResults != "closed") {
                Log.i("onPostExecute", loginResults);

                goToMainActivity();

            } else {
                Log.i("onPostExecute", "Null.");
//                showErrorMessage();
            }
        }
    }


    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link LoginTask}
     */
    private void makeLoginRequest() {
        String u = mUsernameEditText.getText().toString();
        String p = mPasswordEditText.getText().toString();

        URL loginUrl = NetworkUtils.buildAuthUrl(u, p);

        mUrlDisplayTextView.setText(loginUrl.toString());
        new LoginTask(this.getApplicationContext()).execute(loginUrl);

    }



    protected void goToMainActivity () {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void login (View view) {
//        Log.i("THIS", "THAT");
        makeLoginRequest();
    }

    public void showJwt (View view) {
        String text = PreferenceData.getJwt(this.getApplicationContext());

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemThatWasClickedId = item.getItemId();
//        if (itemThatWasClickedId == R.id.action_search) {
//            makeGithubSearchQuery();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
