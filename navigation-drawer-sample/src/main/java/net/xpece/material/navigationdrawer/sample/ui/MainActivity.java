package net.xpece.material.navigationdrawer.sample.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.xpece.material.navigationdrawer.list.CrossFadeSlidingPaneLayout;

import net.xpece.material.navigationdrawer.NavigationDrawerUtils;
import net.xpece.material.navigationdrawer.descriptors.BaseNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.descriptors.SimpleNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.list.NavigationListFragmentCallbacks;
import net.xpece.material.navigationdrawer.list.SupportCompactNavigationListFragment;
import net.xpece.material.navigationdrawer.list.SupportNavigationListFragment;
import net.xpece.material.navigationdrawer.sample.BuildConfig;
import net.xpece.material.navigationdrawer.sample.R;
import net.xpece.material.navigationdrawer.sample.widget.ToggleNavigationItemDescriptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationListFragmentCallbacks {

//  @InjectView(R.id.app_bar)
//  Toolbar mToolbar;

    private static final List<NavigationSectionDescriptor> SECTIONS = new ArrayList<>();
    private static final NavigationSectionDescriptor PRIMARY_SECTION;
    private static final NavigationSectionDescriptor PINNED_SECTION;

    static {
        PRIMARY_SECTION = new NavigationSectionDescriptor()
            .addItem(new SimpleNavigationItemDescriptor(1).text("Goodbye").badge("Hello").sticky()
                .iconResource(R.drawable.ic_star_black_24dp)
                .activeColorResource(R.color.material_red_500)
                .badgeColorResource(R.color.material_red_500)
            .activatedBackgroundResource(R.color.material_red_100))
            .addItem(new SimpleNavigationItemDescriptor(2).text("Yes").badge("No").sticky()
                .iconResource(R.drawable.ic_star_black_24dp)
                .passiveColorResource(R.color.material_amber_500).iconColorAlwaysPassiveOn()
//                .activatedBackgroundResource(R.color.material_amber_100)
                .badgeColorResource(R.color.material_amber_500))
            .addItem(new SimpleNavigationItemDescriptor(3).text("Stop").badge("Go, go, go").sticky()
//            .iconResource(R.drawable.ic_star_black_24dp)
                .iconResource(android.R.color.transparent)
                .activeColorResource(R.color.material_light_green_500)
                .badgeColorResource(R.color.material_light_green_500)
                .activatedBackgroundResource(R.color.material_light_green_100))
            .addItem(new SimpleNavigationItemDescriptor(4).text("Why").badge("I don't know").sticky()
                .iconResource(0)
                .activeColorResource(R.color.material_light_blue_500).iconColorAlwaysPassiveOn()
                .badgeColor(0)
                .activatedBackgroundResource(R.color.material_light_blue_100));
        SECTIONS.add(PRIMARY_SECTION);
        NavigationSectionDescriptor section2 = new NavigationSectionDescriptor().heading("Want more?")
            .addItem(new ToggleNavigationItemDescriptor(8).checked(true).text("Hell no.").textChecked("Bring it!"));
//            .addItem(new ToggleNavigationItemDescriptor(9).checked(true).text("A0").textChecked("A1"))
//            .addItem(new ToggleNavigationItemDescriptor(10).checked(true).text("B0").textChecked("B1"))
//            .addItem(new ToggleNavigationItemDescriptor(11).checked(true).text("C0").textChecked("C1"))
//            .addItem(new ToggleNavigationItemDescriptor(12).checked(true).text("D0").textChecked("D1"))
//            .addItem(new ToggleNavigationItemDescriptor(13).checked(true).text("E0").textChecked("E1"))
//            .addItem(new ToggleNavigationItemDescriptor(14).checked(true).text("F0").textChecked("F1"))
//            .addItem(new ToggleNavigationItemDescriptor(15).checked(true).text("G0").textChecked("G1"))
//            .addItem(new ToggleNavigationItemDescriptor(16).checked(true).text("H0").textChecked("H1"))
//            .addItem(new ToggleNavigationItemDescriptor(17).checked(true).text("I0").textChecked("I1"));
        SECTIONS.add(section2);
        NavigationSectionDescriptor section3 = new NavigationSectionDescriptor()
            .addItem(new BaseNavigationItemDescriptor(6).text("Settings")
                .iconResource(R.drawable.ic_settings_black_24dp))
            .addItem(new BaseNavigationItemDescriptor(7).text("Help & feedback")
                .iconResource(R.drawable.ic_help_black_24dp));
        PINNED_SECTION = section3;

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }

    @Optional
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Optional
    @InjectView(R.id.sliding_layout)
    CrossFadeSlidingPaneLayout mSlidingLayout;

    @Optional
    @InjectView(R.id.navigation_container)
    ViewGroup mNavigationContainer;

    @InjectView(R.id.content)
    View mContent;

    ActionBarDrawerToggle mDrawerToggle;

    SupportNavigationListFragment mNavFragment;
    SupportCompactNavigationListFragment mNavFragmentCompact;

    // retain this
    int mSelectedItem;

    // so I can show new toast immediately
    Toast mToast = null;

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // only setup drawer layout if there is a drawer in current layout (on phones)
        if (mDrawerLayout != null) {
            // setup drawer toggle, because i use native Action Bar and nav drawer below it
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.untitled, android.R.string.untitled);
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            NavigationDrawerUtils.fixMinDrawerMargin(mDrawerLayout); // apply navigation margin fix

            // the following are correct the RIGHT drawer drops shadow to LEFT and vice versa
            mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_left, Gravity.RIGHT);
            mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_right, Gravity.LEFT);
        }

        // only setup sliding layout if there is one in current layout (on tablets)
        if (mSlidingLayout != null) {
            mSlidingLayout.setSliderFadeColor(0);
            mSlidingLayout.setShadowResourceLeft(R.drawable.mnd_shadow_left);
            mSlidingLayout.setShadowResourceRight(R.drawable.mnd_shadow_right);
            NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavigationContainer);

            Drawable d = getResources().getDrawable(R.drawable.ic_menu_white_24dp);
            int c = Utils.getColor(getSupportActionBar().getThemedContext(), R.attr.colorControlNormal, 0);
            d = Utils.tintDrawable(d, c);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(d);
        }

        // on phones there is no compact version, so null check is in place
        mNavFragmentCompact = (SupportCompactNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_compact);
        if (mNavFragmentCompact != null) {
            List<NavigationSectionDescriptor> sections = new ArrayList<>();
            sections.add(PRIMARY_SECTION);
            sections.add(PINNED_SECTION);
            mNavFragmentCompact.setHeaderView(mNavFragmentCompact.getLayoutInflater2().inflate(R.layout.mnd_custom_header_compact, null), false);
            mNavFragmentCompact.setSections(sections);

//            mNavFragmentCompact.setBackgroundResource(R.drawable.a7x_aligned_light);
            mNavFragmentCompact.setBackgroundResource(R.drawable.a7x_aligned_dark);
        }

        // since the fragment is defined in layout, i can call this safely in onCreate
        mNavFragment = (SupportNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (mNavFragment != null) {
            NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavFragment.getView());
            // set up the nav fragment
            mNavFragment.setHeaderView(mNavFragment.getLayoutInflater2().inflate(R.layout.mnd_custom_header, null), true);
            mNavFragment.setSections(SECTIONS);
            mNavFragment.setPinnedSection(PINNED_SECTION);

//            mNavFragment.setBackgroundResource(R.drawable.a7x_aligned_light);
            mNavFragment.setBackgroundResource(R.drawable.a7x_aligned_dark);
        }

        if (savedInstanceState == null) {
            mSelectedItem = 1;
        } else {
            mSelectedItem = savedInstanceState.getInt("mSelectedItem");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            if (mNavFragment != null) mNavFragment.setSelectedItem(mSelectedItem);
            if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(mSelectedItem);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mSelectedItem", mSelectedItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // AUTOMATICALLY ONLY ON 4.1+ !!!

        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mDrawerToggle != null) return mDrawerToggle.onOptionsItemSelected(item);
            if (mSlidingLayout != null) {
                if (mSlidingLayout.isOpen()) mSlidingLayout.closePane();
                else mSlidingLayout.openPane();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemSelected(View view, int position, int id, NavigationItemDescriptor item) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, "Item #" + id + " on position " + position + " selected!", Toast.LENGTH_SHORT);
        mToast.show();

        if (item != null && item.isSticky()) {
            // if it is sticky, save selected position and mark in all fragments
            mSelectedItem = id;
            if (mNavFragment != null) mNavFragment.setSelectedItem(id);
            if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(id);
        } else {
//      if (mNavFragment != null) mNavFragment.setSelectedItem(id);
//      if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(id);
        }

        if (id == 8) return; // custom toggle does not close the drawer

//    if (mDrawerLayout != null) mDrawerLayout.closeDrawers(); // uncomment to close drawer on item selected
    }

}
