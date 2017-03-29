package com.varvet.barcodereadersample;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import com.motoshop.vins.CarDetails;
import com.motoshop.vins.Vin;

/**
 * Created by mvalencia on 3/17/17.
 */

class VinListAdapter extends ArrayAdapter<Vin> {

    private static final String TAG = "VinListAdapter";

    private Context mContext;
    private int mResource;

    /**
     * Default constructor for the VinListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public VinListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Vin> objects) {
        super(context, resource, objects);

        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        int id = getItem(position).getId();
        String date = getItem(position).getDate();
        String vin = getItem(position).getVin();
        CarDetails details = getItem(position).getDetails();

//        Vin newVin = new Vin(id, date, vin, details);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

//        TextView idTextView = (TextView) convertView.findViewById(R.id.id_text_view);
//        TextView dateTextView = (TextView) convertView.findViewById(R.id.date_text_view);
//        TextView vinTextView = (TextView) convertView.findViewById(R.id.vin_text_view);
//        TextView carDetailsYearTextView = (TextView) convertView.findViewById(R.id.car_details_year_text_view);
//        TextView carDetailsMakeTextView = (TextView) convertView.findViewById(R.id.car_details_make_text_view);
//        TextView carDetailsModelTextView = (TextView) convertView.findViewById(R.id.car_details_model_text_view);


//        idTextView.setText("" + id);
//        dateTextView.setText(date);
//        vinTextView.setText(vin);
//        carDetailsYearTextView.setText(details.year);
//        carDetailsMakeTextView.setText(details.make);
//        carDetailsModelTextView.setText(details.model);



//        Button deleteButton = (Button) convertView.findViewById(R.id.delete_button);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("onDelete: ", "???");
//            }
//        });

        return convertView;
    }
}
