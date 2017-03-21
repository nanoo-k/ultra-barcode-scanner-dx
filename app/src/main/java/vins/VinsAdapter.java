package vins;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.varvet.barcodereadersample.R;

import java.util.List;

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

    @Override
    public VinsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.vin_management_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        VinsViewHolder viewHolder = new VinsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VinsViewHolder holder, int position) {
        holder.bind(mVins.get(position));
    }

    @Override
    public int getItemCount() {
        return mVins.size();
    }

    class VinsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView idTextView;
        TextView dateTextView;
        TextView vinTextView;
        TextView carDetailsYearTextView;
        TextView carDetailsMakeTextView;
        TextView carDetailsModelTextView;



//        TextView idTextView = (TextView) convertView.findViewById(R.id.id_text_view);
//        TextView dateTextView = (TextView) convertView.findViewById(R.id.date_text_view);
//        TextView vinTextView = (TextView) convertView.findViewById(R.id.vin_text_view);
//        TextView carDetailsYearTextView = (TextView) convertView.findViewById(R.id.car_details_year_text_view);
//        TextView carDetailsMakeTextView = (TextView) convertView.findViewById(R.id.car_details_make_text_view);
//        TextView carDetailsModelTextView = (TextView) convertView.findViewById(R.id.car_details_model_text_view);


        public VinsViewHolder(View itemView) {
            super(itemView);

            idTextView = (TextView) itemView.findViewById(R.id.id_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            vinTextView = (TextView) itemView.findViewById(R.id.vin_text_view);
            carDetailsYearTextView = (TextView) itemView.findViewById(R.id.car_details_year_text_view);
            carDetailsMakeTextView = (TextView) itemView.findViewById(R.id.car_details_make_text_view);
            carDetailsModelTextView = (TextView) itemView.findViewById(R.id.car_details_model_text_view);



            Button deleteButton = (Button) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("onDelete: ", "???");
                }
            });

            itemView.setOnClickListener(this);
        }

        void bind(Vin vin) {
            idTextView.setText(vin.getId());
            dateTextView.setText(vin.getDate());
            vinTextView.setText(vin.getVin());
            carDetailsYearTextView.setText(vin.getDetails().getYear());
            carDetailsMakeTextView.setText(vin.getDetails().getMake());
            carDetailsModelTextView.setText(vin.getDetails().getModel());

        }

        @Override
        public void onClick(View v) {
            Log.i("ONCLICK: ", "Happenes");
//            int clickedPosition = getAdapterPosition();
//            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
