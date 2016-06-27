package eg.com.theplanet.akram.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import eg.com.theplanet.akram.R;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class ContributionHolder extends RecyclerView.ViewHolder{

    public ImageView typeImageView;
    public TextView areaTextView;
    public TextView timeTextView;
    public Button shareButton;

    public ContributionHolder(View itemView) {
        super(itemView);
        typeImageView = (ImageView) itemView.findViewById(R.id.imageView);
        areaTextView = (TextView) itemView.findViewById(R.id.title_textView);
        timeTextView = (TextView) itemView.findViewById(R.id.time_textView);
        shareButton = (Button) itemView.findViewById(R.id.share_button);


    }
}
