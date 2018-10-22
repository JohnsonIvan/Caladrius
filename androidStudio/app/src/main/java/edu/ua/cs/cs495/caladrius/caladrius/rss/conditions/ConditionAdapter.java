package edu.ua.cs.cs495.caladrius.caladrius.rss.conditions;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ua.cs.cs495.caladrius.caladrius.R;


public class ConditionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context c;
    private ArrayList<Condition> conditions;
    private final FragmentManager fm;

    private static final String OUR_TAG = "FeedAdapter";

    public ConditionAdapter(Context c, ArrayList<Condition> conditions, FragmentManager fm) {
        this.c = c;
        this.conditions = conditions;
        this.fm = fm;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return conditions.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.rss_feed_item, null);
        }
        TextView text = view.findViewById(R.id.name);
        text.setText(conditions.get(i).toString());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = conditions.get(i).makeEditorIntent(c);
                c.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public Object getItem(int i) {
        return conditions.get(i);
    }
}
