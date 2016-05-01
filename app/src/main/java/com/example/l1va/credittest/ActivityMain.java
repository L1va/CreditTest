package com.example.l1va.credittest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    private DrawerLayout drawer;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ActivitySettings.getThemeNoActionBar(getBaseContext()));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    navigationView.setCheckedItem(R.id.nav_gallery);
                } else if (position == 1) {
                    navigationView.setCheckedItem(R.id.nav_photo);
                } else if (position == 2) {
                    navigationView.setCheckedItem(R.id.nav_cache);
                }
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentGrid(), getString(R.string.tab_gallery));
        adapter.addFragment(new FragmentGrid(), getString(R.string.tab_photos));
        adapter.addFragment(new FragmentGrid(), getString(R.string.tab_cache));
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                startSettingsActivity();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivityForResult(intent, SETTINGS_REQUEST);
    }

    private static final int SETTINGS_REQUEST = 10001;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTINGS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    recreate();
                }
                break;
        }
    }

    class OnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_gallery:
                    tabLayout.getTabAt(0).select();
                    break;
                case R.id.nav_photo:
                    tabLayout.getTabAt(1).select();
                    break;
                case R.id.nav_cache:
                    tabLayout.getTabAt(2).select();
                    break;
                case R.id.nav_comment:
                    Intent mailer = new Intent(Intent.ACTION_SEND);
                    mailer.setType("text/plain");
                    mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{"", "L1va4ka@yandex.by"});
                    mailer.putExtra(Intent.EXTRA_SUBJECT, "Credit Test App Comment");
                    mailer.putExtra(Intent.EXTRA_TEXT, "Hi, Mike!");
                    startActivity(Intent.createChooser(mailer, "Send email..."));
                    break;
                case R.id.nav_settings:
                    startSettingsActivity();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
