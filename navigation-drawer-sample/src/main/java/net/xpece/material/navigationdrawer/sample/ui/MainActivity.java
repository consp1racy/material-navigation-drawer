package net.xpece.material.navigationdrawer.sample.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.xpece.material.navigationdrawer.NavigationDrawerUtils;
import net.xpece.material.navigationdrawer.descriptors.BaseNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.descriptors.SimpleNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.list.NavigationListFragmentCallbacks;
import net.xpece.material.navigationdrawer.list.SupportNavigationListFragment;
import net.xpece.material.navigationdrawer.sample.widget.ToggleNavigationItemDescriptor;
import net.xpece.materialnavigationdrawersample.BuildConfig;
import net.xpece.materialnavigationdrawersample.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public class MainActivity extends ActionBarActivity implements NavigationListFragmentCallbacks {

//  @InjectView(R.id.app_bar)
//  Toolbar mToolbar;

  private static final List<NavigationSectionDescriptor> SECTIONS = new ArrayList<>();
  private static final NavigationSectionDescriptor PINNED_SECTION;

  static {
    NavigationSectionDescriptor section = new NavigationSectionDescriptor()
        .addItem(new SimpleNavigationItemDescriptor(1).text("Goodbye").badge("Hello").sticky()
            .iconResource(R.drawable.ic_star_black_24dp)
            .activeColorResource(R.color.material_pink_500)
            .badgeColorResource(R.color.material_red_500))
        .addItem(new SimpleNavigationItemDescriptor(2).text("Yes").badge("No").sticky()
            .iconResource(R.drawable.ic_star_black_24dp)
            .activeColorResource(R.color.material_pink_500)
            .badgeColorResource(R.color.material_amber_500))
        .addItem(new SimpleNavigationItemDescriptor(3).text("Stop").badge("Go, go, go").sticky()
//            .iconResource(R.drawable.ic_star_black_24dp)
            .activeColorResource(R.color.material_pink_500)
            .badgeColorResource(R.color.material_light_green_500))
        .addItem(new SimpleNavigationItemDescriptor(4).text("Why").badge("I don't know").sticky()
//            .iconResource(R.drawable.ic_star_black_24dp)
//            .activeColorResource(R.color.material_pink_500)
//            .badgeColorResource(R.color.material_light_blue_500));
            .iconResource(R.drawable.ic_star_black_24dp)
            .passiveColorResource(R.color.material_amber_500).iconColorAlwaysPassiveOn()
            .badgeColor(0));
    SECTIONS.add(section);
    NavigationSectionDescriptor section2 = new NavigationSectionDescriptor().heading("Want more?")
//        .addItem(new SimpleNavigationItemDescriptor(5).text("Oh no").sticky()
//            .badge("99+").badgeColor(0)
//            .iconResource(R.drawable.ic_star_black_24dp)
//            .passiveColorResource(R.color.material_amber_500).iconColorAlwaysPassiveOn())
        .addItem(new ToggleNavigationItemDescriptor(8));
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

  ActionBarDrawerToggle mDrawerToggle;
  SupportNavigationListFragment mNavFragment;

  // retain this
  Long mSelectedItem;

  // so I can show new toast immediately
  Toast mToast = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    if (mDrawerLayout != null) {
      // setup drawer toggle, because i use native Action Bar and nav drawer below it
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.untitled, android.R.string.untitled);
      mDrawerLayout.setDrawerListener(mDrawerToggle);
      NavigationDrawerUtils.fixMinDrawerMargin(mDrawerLayout); // apply navigation margin fix
    }

    // since the fragment is defined in layout, i can call this safely in onCreate
    mNavFragment = (SupportNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//    if (mDrawerLayout != null) {
    NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavFragment.getView());
//    }

    // set up the nav fragment
    mNavFragment.setHeaderView(View.inflate(this, R.layout.mnd_custom_header, null));
    mNavFragment.setSections(SECTIONS);
    mNavFragment.setPinnedSection(PINNED_SECTION);

    if (savedInstanceState == null) {
      mSelectedItem = 1l;
    } else {
      mSelectedItem = savedInstanceState.getLong("mSelectedItem");
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    if (mDrawerToggle != null) mDrawerToggle.syncState();

    if (savedInstanceState == null) {
      mNavFragment.setSelectedItem(mSelectedItem);
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
    outState.putLong("mSelectedItem", mSelectedItem);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    } else if (id == android.R.id.home) {
      if (mDrawerToggle != null) return mDrawerToggle.onOptionsItemSelected(item);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNavigationItemSelected(View view, int position, long id, NavigationItemDescriptor item) {
    if (mToast != null) mToast.cancel();
    mToast = Toast.makeText(this, "Item #" + id + " on position " + position + " selected!", Toast.LENGTH_SHORT);
    mToast.show();
    mSelectedItem = id;

    if (id == 8) return; // custom toggle does not close the drawer

//    mDrawerLayout.closeDrawers(); // uncomment to close drawer on item selected
  }

}
