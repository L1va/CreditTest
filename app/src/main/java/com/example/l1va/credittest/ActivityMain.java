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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

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
                    mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.author_email)});
                    mailer.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                    mailer.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
                    startActivity(Intent.createChooser(mailer, getString(R.string.email_button)));
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

        private final String[] titles = new String[]{getString(R.string.tab_gallery), getString(R.string.tab_photos), getString(R.string.tab_cache)};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentGrid();
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];

        }
    }
}
