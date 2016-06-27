package eg.com.theplanet.akram.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.models.Contribution;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class CompleteFormFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_complete, container, false);
        rootView.findViewById(R.id.facebook_imageView).setOnClickListener(this);
        rootView.findViewById(R.id.twitter_imageView).setOnClickListener(this);
        rootView.findViewById(R.id.mail_imageView).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.facebook_imageView:
            case R.id.twitter_imageView:
            case R.id.mail_imageView:
                share();
                break;
        }
    }

    private void share() {
        Contribution contribution = new Contribution();
        contribution.type = getArguments().getString(Constants.FORM_TYPE, "");
        Utils.share(getContext(),contribution);
    }
}
