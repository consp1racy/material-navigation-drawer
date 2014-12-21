package net.xpece.materialnavigationdrawersample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import net.xpece.materialnavigationdrawer.NavigationDrawerFragment;
import net.xpece.materialnavigationdrawer.NavigationDrawerUtils;
import net.xpece.materialnavigationdrawer.NavigationItemDescriptor;
import net.xpece.materialnavigationdrawer.NavigationSectionDescriptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.Callbacks {

//  @InjectView(R.id.app_bar)
//  Toolbar mToolbar;

  @InjectView(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;

  ActionBarDrawerToggle mDrawerToggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

//    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.untitled, android.R.string.untitled);
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    NavigationDrawerFragment nav = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
    NavigationSectionDescriptor section = new NavigationSectionDescriptor().addItem(new NavigationItemDescriptor(1).text("Hello"));
    List<NavigationSectionDescriptor> sections = new ArrayList<>();
    sections.add(section);
    nav.setSections(sections);

    NavigationDrawerUtils.fixMinDrawerMargin(mDrawerLayout);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    mDrawerToggle.onConfigurationChanged(newConfig);
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
      return mDrawerToggle.onOptionsItemSelected(item);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNavigationItemSelected(AdapterView<?> parent, View view, int position, long id) {
    Toast.makeText(this, "Something", Toast.LENGTH_SHORT).show();
  }
}
