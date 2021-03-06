package mshttp.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.motoshop.vins.Vin;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static String API_SERVER_BASE_URI = "https://api-dev.motoshop.io/shop-qa";
//    final static String API_SERVER_BASE_URI = "https://api.motoshop.io/shop";
//            - also `shop-qa` and just `shop`

    final static String AUTHENTICATE = API_SERVER_BASE_URI + "/authenticate";
    final static String VIN = API_SERVER_BASE_URI + "/vin/scans";
    final static String USER_INFO = API_SERVER_BASE_URI + "/users/info";


//    final static String GITHUB_BASE_URL =
//            "https://api.github.com/search/repositories";

    final static String PARAM_QUERY = "q";

    /* Requires utf-16 else errors */
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-16");

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
//    final static String PARAM_SORT = "sort";
//    final static String sortBy = "stars";

    /**
     * Builds the URL used to query GitHub.
     *
     * @param username The keyword that will be queried for.
     * @param password The keyword that will be queried for.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildAuthUrl(String username, String password) {
        Uri builtUri = Uri.parse(AUTHENTICATE).buildUpon()
                .build();
//                .appendQueryParameter(PARAM_QUERY, username)

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildRecentVinsUrl() {
        Uri builtUri = Uri.parse(VIN).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildDecodeVinUrl (String vin) {
        String mash = VIN + "/" + vin;
        Uri builtUri = Uri.parse(mash).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildDeleteVinUrl (String vin) {
//        String mash = VIN + "/" + vin;
        String mash = VIN + "/" + "3FA6P0K93FR226623";
        Uri builtUri = Uri.parse(mash).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getJWT (URL url, Context context) throws Exception {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();


        PreferenceData.setLoggedInUser(context, "MANAGERKM");
        PreferenceData.setUserLoggedInStatus(context, true);

        String credential = Credentials.basic("MANAGERKM", "PASS8520");

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", credential)
                .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

//        JSONObject jsonObject = new JSONObject(response.body().string());

        String responseString = response.body().string();

        /* You can only call response.body().string() once. Why. I dunno, but it's fact. */
        JWT jwt = gson.fromJson(responseString, JWT.class);

        Log.i("JWT.toString", jwt.toString());

        PreferenceData.setJwt(context, jwt.jwt);

        return jwt.jwt;
    }



    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getRecentVins (URL url, Context context) throws Exception {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();


        String jwt = PreferenceData.getJwt(context);

        String bearer = "Bearer " + jwt;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", bearer)
                .build();


        Log.i("HEADERS: ", request.headers().toString());

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        String vinsAsString = response.body().string();

//        JSONObject jsonObject = new JSONObject(response.body().string());

        /* This is a pattern for gson parsing an array of some kind of object */
        /* First you determine the listType of the object */
        Type listType = new TypeToken<List<Vin>>(){}.getType();
        /* Then you say you're going to parse to create a list of that thing, assigning to
         * gson the listType */
        List<Vin> vins = gson.fromJson(vinsAsString, listType);

        Log.i("Response.toString", vins.toString());

//        PreferenceData.setJwt(context, vins.toString());

        return vinsAsString;

    }



    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String decodeVin (URL url, Context context) throws Exception {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();


        String jwt = PreferenceData.getJwt(context);

        String bearer = "Bearer " + jwt;

        String json = gson.toJson("empty");

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", bearer)
                .build();

//        Log.i("REQUEST: ", request.toString());
//        Log.i("HEADERS: ", request.headers().toString());

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.i("BAD RESPONSE: ", response.toString());
        }
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        /* gson the listType */
        Vin vin = gson.fromJson(response.body().string(), Vin.class);

        Log.i("decodeVin.Response", vin.toString());

//        PreferenceData.setJwt(context, vin.toString());

        return vin.toString();

    }

    public static String deleteVin(URL url, Context context) throws Exception {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        String jwt = PreferenceData.getJwt(context);

        String bearer = "Bearer " + jwt;

        String json = gson.toJson("empty");

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .addHeader("Authorization", bearer)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.i("BAD RESPONSE: ", response.toString());
        }
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        /* gson the listType */
//        Vin vin = gson.fromJson(response.body().string(), Vin.class);

//        Log.i("decodeVin.Response", vin.toString());

        return "true";
    }
}