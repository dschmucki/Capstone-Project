package com.example.catcha;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catcha.provider.Location;
import com.example.catcha.sync.CatchaObserver;
import com.example.catcha.sync.CatchaSyncAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Catcha extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    ImageView fab;

    private ViewPagerAdapter viewPagerAdapter;
    private boolean isTabletWide = false;
    private int locationTabIndex = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catcha);
        ButterKnife.bind(this);

        // register observer for changes in the locations table
        CatchaObserver catchaObserver = new CatchaObserver(new Handler(), getApplicationContext());
        getContentResolver().registerContentObserver(Location.CONTENT_URI, true, catchaObserver);

        CatchaSyncAdapter.configurePeriodicSync(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        isTabletWide = displayMetrics.widthPixels / displayMetrics.density > 800;

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        setupFab();
    }

    @Override
    protected void onStop() {
        CatchaSyncAdapter.cancelSync(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(final ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(new DeparturesFragment());
        viewPagerAdapter.addFragment(new GoogleMapFragment());
        locationTabIndex = 2;
        viewPagerAdapter.addFragment(new LocationsFragment());

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == locationTabIndex) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    if (isTabletWide && position == locationTabIndex - 1) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupTabIcons() {
        createTab(R.string.tab_departures_title, R.drawable.ic_train_white_24dp, 0);
        createTab(R.string.tab_map_title, R.drawable.ic_map_white_24dp, 1);
        createTab(R.string.tab_locations_title, R.drawable.ic_my_location_white_24dp, 2);
    }

    private void setupFab() {
        fab.setImageResource(R.drawable.ic_add_white_24dp);
        fab.setContentDescription(getString(R.string.button_locations));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationsFragment locationsFragment = (LocationsFragment) viewPagerAdapter.getItem(locationTabIndex);
                locationsFragment.onFabClick(v);
            }
        });
    }

    private void createTab(final int title, final int icon, final int tabNr) {
        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        textView.setContentDescription(getString(title));
        textView.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
        tabLayout.getTabAt(tabNr).setCustomView(textView);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(final Fragment fragment) {
            fragmentList.add(fragment);
        }

        @Override
        public float getPageWidth(int position) {
            if (isTabletWide) {
                return 0.5f;
            } else {
                return super.getPageWidth(position);
            }
        }
    }
}
