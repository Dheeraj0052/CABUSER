package com.longercave.app.Activities;


import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.longercave.app.Fragments.CouponHistory;
import com.longercave.app.Fragments.WalletHistory;
import com.pk.app.R;

public class WalletAndCouponHistory extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String tabTitles[];
    private ImageView backArrow;
    private TextView lblTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblTitle.setText(getResources().getString(R.string.passbook));
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        String strTag = getIntent().getExtras().getString("tag");
        tabTitles = new String[]{getResources().getString(R.string.walletHistory),
                getResources().getString(R.string.couponHistory)};

        viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getSupportFragmentManager(),
                this));

        if (strTag != null) {
            if (strTag.equalsIgnoreCase("past")) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }

        setupTabIcons();

    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(WalletAndCouponHistory.this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getResources().getString(R.string.walletHistory));
        tabLayout.getTabAt(0).setCustomView(tabOne);


        TextView tabTwo = (TextView) LayoutInflater.from(WalletAndCouponHistory.this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getResources().getString(R.string.couponHistory));
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[];
        private Context context;

        public SampleFragmentPagerAdapter(String tabTitles[], FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WalletHistory();
                case 1:
                    return new CouponHistory();
                default:
                    return new WalletHistory();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
