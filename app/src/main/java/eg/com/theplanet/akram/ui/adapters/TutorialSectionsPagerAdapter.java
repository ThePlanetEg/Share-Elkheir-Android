package eg.com.theplanet.akram.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import eg.com.theplanet.akram.ui.fragments.TutorialPlaceholderFragment;


public class TutorialSectionsPagerAdapter extends FragmentPagerAdapter {

    public TutorialSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return TutorialPlaceholderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

//		@Override
//		public CharSequence getPageTitle(int position) {
//			switch (position) {
//			case 0:
//				return getString(R.string.title_section1);
//			case 1:
//				return getString(R.string.title_section2);
//			case 2:
//				return getString(R.string.title_section3);
//			}
//			return null;
//		}

}