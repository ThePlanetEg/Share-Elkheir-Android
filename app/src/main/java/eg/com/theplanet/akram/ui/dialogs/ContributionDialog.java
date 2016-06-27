package eg.com.theplanet.akram.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.models.Contribution;
import eg.com.theplanet.akram.utils.Utils;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by georgenaiem on 5/31/16.
 */
public class ContributionDialog extends AlertDialog {

    public Contribution contribution;

    public ContributionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_contribution);
        ImageView userImageView = (ImageView) findViewById(R.id.user_imageView);
        ImageView typeImageView = (ImageView) findViewById(R.id.type_imageView);
        TextView usernameTextView = (TextView) findViewById(R.id.username_textView);
        TextView timeTextView = (TextView) findViewById(R.id.time_textView);
        TextView locationTextView = (TextView) findViewById(R.id.location_textView);
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_textView);
        ProgressBar accessibilityProgressBar = (ProgressBar) findViewById(R.id.accessibility_progressBar);
        TextView statusTextView = (TextView) findViewById(R.id.status_textView);


        Glide.with(getContext())
                .load(contribution.user.imageURL)
                .placeholder(R.drawable.ic_person_white_48dp)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(userImageView);

        usernameTextView.setText(contribution.user.username);
        timeTextView.setText(Utils.formatDateString(getContext(), contribution.timestamp));
        locationTextView.setText(contribution.area);
        if (contribution.isCovered == 1) {
            statusTextView.setText(R.string.this_area_is_covered);
            statusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            statusTextView.setText(R.string.this_area_needs_help);
            statusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));

        }

        if (contribution.accessibility.contentEquals("easy")) {
            accessibilityProgressBar.setProgress(25);
            accessibilityProgressBar.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (contribution.accessibility.contentEquals("normal")) {
            accessibilityProgressBar.setProgress(50);
            accessibilityProgressBar.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            accessibilityProgressBar.setProgress(100);
            accessibilityProgressBar.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.dark_red), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (contribution.type.contentEquals("hot_meals")) {
//            quantityProgressBar.
//                    getProgressDrawable().setColorFilter(
//                    ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            quantityTextView.setText(contribution.quantity);
            quantityTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.blue));

            typeImageView.setImageResource(R.drawable.ic_circle_hot_meals);
        } else if (contribution.type.contentEquals("clothes")) {
//            quantityProgressBar.
//                    getProgressDrawable().setColorFilter(
//                    ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            quantityTextView.setText(contribution.quantity);
            quantityTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.purple));

            typeImageView.setImageResource(R.drawable.ic_circle_clothes);
        } else {
//            quantityProgressBar.getProgressDrawable().setColorFilter(
//                    ContextCompat.getColor(getContext(), R.color.brown), android.graphics.PorterDuff.Mode.SRC_IN);
            quantityTextView.setText(contribution.quantity);
            quantityTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.brown));

            typeImageView.setImageResource(R.drawable.ic_circle_food_boxes);

        }
//        int quantity;
//
//        try {
//            quantity = Integer.parseInt(contribution.quantity);
//        } catch (Exception e) {
//            quantity = 1000;
//        }
//        quantityProgressBar.setProgress((int) (quantity * .1));


    }
}
