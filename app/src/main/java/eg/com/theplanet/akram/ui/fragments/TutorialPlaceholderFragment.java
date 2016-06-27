package eg.com.theplanet.akram.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import eg.com.theplanet.akram.R;


/**
 * Created by georgenaiem on 7/8/15.
 */
public class TutorialPlaceholderFragment extends Fragment {

    private int[] introImageResources = {R.drawable.tutorial_1,
            R.drawable.tutorial_2, R.drawable.tutorial_3,R.drawable.tutorial_4,0};
    private ImageView introImageView;
    private int sectionNumber;

    public static TutorialPlaceholderFragment newInstance(int sectionNumber) {
        TutorialPlaceholderFragment fragment = new TutorialPlaceholderFragment();
        fragment.sectionNumber = sectionNumber;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_placeholder, container, false);
        introImageView = (ImageView) rootView.findViewById(R.id.intro_imageView);
        introImageView.setImageResource(introImageResources[sectionNumber]);

        return rootView;
    }

}
