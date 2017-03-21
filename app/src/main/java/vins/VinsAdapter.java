package vins;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.varvet.barcodereadersample.MainActivity;
import com.varvet.barcodereadersample.MyApplication;
import com.varvet.barcodereadersample.R;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import mshttp.utilities.NetworkUtils;
import mshttp.utilities.PreferenceData;
import mshttp.utilities.Vin;

/**
 * Created by mvalencia on 3/20/17.
 */

public class VinsAdapter extends RecyclerView.Adapter<VinsAdapter.VinsViewHolder> {

    private List<Vin> mVins;
//    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        /* Signature can be anything */
        void onListItemClick(int clickedItemIndex);
    }

    public VinsAdapter(List<Vin> vins) {
        mVins = vins;
//        mOnClickListener = listener;
    }

    Context context;

    @Override
    public VinsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        int layoutIdForListItem = R.layout.vin_management_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        VinsViewHolder viewHolder = new VinsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VinsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mVins.size();
    }

    class VinsViewHolder extends RecyclerView.ViewHolder {

        int mId;
        String mVin;
        int mPosition;

        TextView dateTextView;
        TextView vinTextView;
        TextView carDetailsYearTextView;
        TextView carDetailsMakeTextView;
        TextView carDetailsModelTextView;


        public VinsViewHolder(View itemView) {
            super(itemView);


            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            vinTextView = (TextView) itemView.findViewById(R.id.vin_text_view);
            carDetailsYearTextView = (TextView) itemView.findViewById(R.id.car_details_year_text_view);
            carDetailsMakeTextView = (TextView) itemView.findViewById(R.id.car_details_make_text_view);
            carDetailsModelTextView = (TextView) itemView.findViewById(R.id.car_details_model_text_view);


            /* Handle request to delete VIN */
            Button deleteButton = (Button) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Delete VIN from list */
                    mVins.remove(getAdapterPosition());
                    /* TODO: Wut this do? Update mVins data? */
                    notifyItemRemoved(getAdapterPosition());
                    /* Make the HTTP request to delete the VIN */
                    deleteVinRequest(mVin, getAdapterPosition(), context);
                }
            });

        }

        private void deleteVinRequest(String vin, int position, Context context) {
            String jwt = PreferenceData.getJwt(context);

            URL deleteVinUrl = NetworkUtils.buildDeleteVinUrl(vin);

            Log.i("deleteVinUrl: ", "" + deleteVinUrl);
            Log.i("jwt: ", jwt);

            new VinsAdapter.VinsViewHolder.DeleteVinTask(context).execute(deleteVinUrl);

        }

        public class DeleteVinTask extends AsyncTask<URL, Void, String> {

            /* We need the app context available for these callback functions, so
            ensure that we set it when calling this task */
            private Context context;
            public DeleteVinTask (Context c){
                context = c;
            }

            @Override
            protected void onPreExecute() {
                Log.i("onPreExecute", "MADE IT.");
                super.onPreExecute();
//                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(URL... params) {
                Log.i("doInBackground", "MADE IT.");

                URL deleteVinUrl = params[0];
                String isSuccess = null;
                try {
                    isSuccess = NetworkUtils.deleteVin(deleteVinUrl, context);
                } catch (IOException e) {
                    Log.i("doInBackground", "IOException.");

                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i("doInBackground", "exception.");
                    e.printStackTrace();
                }
                return isSuccess;
            }

            @Override
            protected void onPostExecute(String isSuccess) {

                if (isSuccess == "true") {
                    sendAnalyticsHit("DeleteVinSuccess", "DeleteVinSuccess", "DeleteVinSuccess");

                    Log.i("SUCCESS: ", "SUCCESS");
                }

                Log.i("FAILURE: ", "FAILURE");

            /* If VIN got decoded and we got data from server... */
//                mLoadingIndicator.setVisibility(View.INVISIBLE);
//                if (vinResults != null && vinResults != "closed") {
//                    Log.i("onPostExecute", vinResults);
//
//                    showJsonDataView();
//                    mDecodedVinTextView.setText(vinResults);

//                    sendAnalyticsHit("BarcodeSuccess", "BarcodeSuccess", "BarcodeSuccess");


//                }
            /* Else error decoding */
//                else {
//                    sendAnalyticsHit("BarcodeFail", "BarcodeFail", "BarcodeFail");

//                    Log.i("onPostExecute", "Null.");
//                    showErrorMessage();
//                }
            }
        }

        /* Implement these where we want and then set autotracking to false (res/xml/tracker_settings) */
        private void sendAnalyticsHit (String category, String action, String label) {

//            TODO Implement tracker

            // Get the tracker
//            Tracker tracker = ((MyApplication) getApplication()).getTracker();
//
//            // Send the hit
//            tracker.send(new HitBuilders.EventBuilder()
//                    .setCategory(category)
//                    .setAction(action)
//                    .setLabel(label)
//                    .build());
        }

        void bind(int position) {
            Vin vin = mVins.get(position);

            mId = vin.getId();
            mVin = vin.getVin();
            mPosition = position;

            dateTextView.setText(vin.getDate());
            vinTextView.setText(vin.getVin());
            carDetailsYearTextView.setText(vin.getDetails().getYear());
            carDetailsMakeTextView.setText(vin.getDetails().getMake());
            carDetailsModelTextView.setText(vin.getDetails().getModel());

        }

//        @Override
//        public void onClick(View v) {
//            Log.i("ONCLICK: ", "Happenes");
////            int clickedPosition = getAdapterPosition();
////            mOnClickListener.onListItemClick(clickedPosition);
//        }
    }
}
