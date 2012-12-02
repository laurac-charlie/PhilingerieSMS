package com.isd360.philingerie_sms.adapter;

import java.util.List;

import com.isd360.philingerie_sms.entity.Configuration;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfigurationListAdapter extends BaseAdapter {

	private Context context = null;
	private List<Configuration> configurations = null;
	private LayoutInflater mInflater = null;
	
	public ConfigurationListAdapter(Context context, List<Configuration> confs)
	{
		super();
		this.context = context;
		this.configurations = confs;
		this.mInflater = LayoutInflater.from(this.context);
	}
	
	public int getCount() {
		return this.configurations.size();
	}

	public Configuration getItem(int position) {
		return this.configurations.get(position);
	}

	public long getItemId(int position) {
		return this.getItemId(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem = null;
		//TODO: Adapter à l'item
		/*if (convertView == null) 
			layoutItem = (LinearLayout) mInflater.inflate(R.layout.itemtemplate, parent, false);
		else
			layoutItem = (LinearLayout) convertView;

		TextView subject = (TextView) layoutItem.findViewById(R.id.item_subject);
		TextView state = (TextView) layoutItem.findViewById(R.id.item_state);
		TextView period = (TextView) layoutItem.findViewById(R.id.item_period);
		subject.setTextColor(Color.WHITE);
		state.setTextColor(Color.WHITE);
		period.setTextColor(Color.WHITE);

		subject.setText(this.interventions.get(position).getSubject());
		state.setText("is " + this.interventions.get(position).getStatus().toLowerCase());
		period.setText("From : " + this.interventions.get(position).getBeginDate().toString() + " to : " + this.interventions.get(position).getEndDate().toString());

		if (position % 2 == 0)
			layoutItem.setBackgroundColor(Color.DKGRAY);
		else
			layoutItem.setBackgroundColor(Color.BLACK);
		*/
		return layoutItem;
	}
	

}
