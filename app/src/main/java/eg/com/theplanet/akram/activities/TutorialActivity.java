package eg.com.theplanet.akram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.viewpagerindicator.CirclePageIndicator;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.ui.adapters.TutorialSectionsPagerAdapter;

/**
 * Created by georgenaiem on 6/27/16.
 */
public class TutorialActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ViewPager viewPager = (ViewPager) findViewById(R.id.tutorial_viewPager);
        viewPager.setAdapter(new TutorialSectionsPagerAdapter(getSupportFragmentManager()));
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        indicator.setSnap(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4){
                    Intent intent = new Intent(TutorialActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
