package edu.ua.cs.cs495.caladrius.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * PagerActivity is the home page for the app, it have toolbar with calender page and
 * edit page, also have two page fragment switch by using sliding tabs, one is summary
 * page, another is rss page.
 *
 * @author Hansheng Li
 */
public class PagerActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager_activity);
		// Find the view pager that will allow the user to swipe between fragments
		ViewPager viewPager = findViewById(R.id.viewpager);

		Toolbar toolbar = findViewById(R.id.home_toolbar);
		setSupportActionBar(toolbar);

		PagerAdapter adapter =
			new PagerAdapter(this, getSupportFragmentManager());

		// Set the adapter onto the view pager
		viewPager.setAdapter(adapter);

		// Give the TabLayout the ViewPager
		TabLayout tabLayout = findViewById(R.id.sliding_tabs);
		tabLayout.setupWithViewPager(viewPager);
	}

	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.calender:
			Intent calenderIntent = new Intent(this,
				QueryEditor.class);
			startActivity(calenderIntent);
			return true;
		case R.id.edit:
//			Intent editIntent = new Intent(this,
//					SummaryPageEditor.class);
			Intent editIntent = new Intent(this,
					ListTest.class);
			startActivity(editIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
