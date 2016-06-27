package eg.com.theplanet.akram.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eg.com.theplanet.akram.R;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class SearchHolder extends RecyclerView.ViewHolder{

    public TextView titleTextView;
    public TextView addressTextView;

    public SearchHolder(View itemView) {
        super(itemView);
        titleTextView = (TextView) itemView.findViewById(R.id.title_textView);
        addressTextView = (TextView) itemView.findViewById(R.id.address_textView);


    }
}
