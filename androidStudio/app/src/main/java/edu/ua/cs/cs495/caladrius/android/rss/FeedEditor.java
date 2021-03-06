package edu.ua.cs.cs495.caladrius.android.rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import edu.ua.cs.cs495.caladrius.android.Caladrius;
import edu.ua.cs.cs495.caladrius.android.GenericEditor;
import edu.ua.cs.cs495.caladrius.android.R;
import edu.ua.cs.cs495.caladrius.android.rss.conditions.ConditionAdapter;
import edu.ua.cs.cs495.caladrius.android.rss.conditions.ConditionEditor;
import edu.ua.cs.cs495.caladrius.rss.Feed;
import edu.ua.cs.cs495.caladrius.rss.condition.Condition;
import edu.ua.cs.cs495.caladrius.rss.condition.ExtremeValue;
import edu.ua.cs.cs495.caladrius.server.Clientside;
import edu.ua.cs.cs495.caladrius.server.ServerAccount;

import java.io.IOException;

/**
 * This class is the fragment that manages the components necessary for editing RSS feeds. Note that the FeedEditor
 * class contains a subclass called FeedEditorActivity that will display a FeedEditor along with save/cancel/etc
 * buttons.
 */
public class FeedEditor extends Fragment
{
	protected static final String ARG_FEED = "FeedEditor_feed";
	protected static final String EXTRA_RESULT = "iouwlkxnvljweefoiu";
	private static final String LOGTAG = "FEED_EDITOR";
	protected Feed f;
	protected FloatingActionButton add;
	ConditionAdapter adapter;
	EditText name;

	public static FeedEditor newInstance(@NonNull Feed f)
	{
		FeedEditor fe = new FeedEditor();

		Bundle b = new Bundle();
		b.putSerializable(ARG_FEED, f);

		fe.setArguments(b);

		return fe;
	}

	public static Feed getFeed(Intent data)
	{
		return (Feed) data.getSerializableExtra(EXTRA_RESULT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		} else if (resultCode != Activity.RESULT_OK) {
			throw new RuntimeException("Condition editor yielded an unexpected result");
		}

		Condition cond = ConditionEditor.getCondition(data);
		if (requestCode == 0) {
			if (cond != null) {
				adapter.addItem(cond);
			}
		} else {
			if (cond == null) {
				adapter.removeItem(requestCode - 1);
			} else {
				adapter.setItem(requestCode - 1, cond);
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		Bundle args = getArguments();
		f = (Feed) args.getSerializable(ARG_FEED);
		if (f == null) {
			throw new RuntimeException("FeedEditor must be provided with a feed to edit");
		}

		View rootView = inflater.inflate(R.layout.rss_feed_edit, container, false);
		name = rootView.findViewById(R.id.feedName);
		name.setText(f.name);

		TextView urlView = rootView.findViewById(R.id.url);
		String url = f.getURL();
		urlView.setText(url);

		ListView ll = rootView.findViewById(R.id.ConditionList);

		adapter = new ConditionAdapter(f.conditions, getContext(), (int i, Condition cond) ->
		{
			Intent in = ConditionEditor.createIntent(getContext(), cond);
			startActivityForResult(in, i + 1);
		});

		ll.setAdapter(adapter);

		add = rootView.findViewById(R.id.add_condition);
		add.setOnClickListener((View v) ->
		{
			Intent in = ConditionEditor.createIntent(getContext(),
				new ExtremeValue("", 0.0, ExtremeValue.extremeType.equal));
			startActivityForResult(in, 0);
		});

		return rootView;
	}

	public Feed updateFeed()
	{
		f.name = name.getText()
		             .toString();
		return f;
	}

	protected static class AsyncSaveFeed extends AsyncTask<AsyncSaveFeed.ASSFArgs, Void, Boolean>
	{
		Clientside cs = new Clientside();
		ServerAccount sa = Caladrius.getUser().sAcc;
		Activity activity;
		public AsyncSaveFeed(Activity activity)
		{
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(ASSFArgs... args)
		{
			// TODO: progress bar of some sort? Probably not a literal bar though; feeds /should/ only be one long.
			boolean success = true;
			for (int x = 0; x < args.length; x++) {
				try {
					boolean delete = args[x].delete;
					Feed f = args[x].f;
					if (delete) {
						cs.deleteFeed(sa, f.uuid);
						continue;
					}
					cs.setFeed(sa, f);
				} catch (IOException e) {
					Log.w("AsyncSaveFeed", e);
					success = false;
				}
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean success)
		{
			if (success) {
				activity.finish();
			}
		}

		public static class ASSFArgs
		{
			Feed f;
			boolean delete = false;
		}
	}

	public static class FeedEditorActivity extends GenericEditor
	{
		protected static final String EXTRA_FEED = "feed";
		FeedEditor fe;

		protected FeedEditorActivity()
		{
			super("Feed Editor", false);
		}

		public static Intent newIntent(Context cntxt, Feed feed)
		{
			Intent in = new Intent(cntxt, FeedEditorActivity.class);
			in.putExtra(EXTRA_FEED, feed);
			return in;
		}

		@Override
		protected Fragment makeFragment()
		{
			Bundle bun = getIntent().getExtras();
			fe = newInstance((Feed) bun.getSerializable(EXTRA_FEED));
			return fe;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId()) {
			case R.id.Delete_All_Conditions:
				while (fe.adapter.getCount() > 0) {
					fe.adapter.removeItem(0);
				}
				return true;
			case R.id.Delete_Feed:
				Intent in = new Intent();

				in.putExtra(EXTRA_RESULT, (Feed) null);
				setResult(Activity.RESULT_OK, in);

				AsyncSaveFeed assf = new AsyncSaveFeed(this);
				AsyncSaveFeed.ASSFArgs args = new AsyncSaveFeed.ASSFArgs();
				args.f = fe.f;
				args.delete = true;
				assf.execute(args);

				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		protected void save()
		{
			Intent in = new Intent();

			in.putExtra(EXTRA_RESULT, fe.updateFeed());
			setResult(Activity.RESULT_OK, in);

			AsyncSaveFeed.ASSFArgs args = new AsyncSaveFeed.ASSFArgs();
			args.f = fe.f;
			AsyncSaveFeed assf = new AsyncSaveFeed(this);
			assf.execute(args);
		}
	}
}
