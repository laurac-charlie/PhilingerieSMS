package com.isd360.philingerie_sms.adapter;

import java.util.List;

import com.isd360.philingerie_sms.activity.R;
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
		return this.getItem(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem = null;
		//TODO: Adapter à l'item
		if (convertView == null) 
			layoutItem = (LinearLayout) mInflater.inflate(R.layout.list_conf_template, parent, false);
		else
			layoutItem = (LinearLayout) convertView;

		TextView name = (TextView) layoutItem.findViewById(R.id.conf_name);
		TextView description = (TextView) layoutItem.findViewById(R.id.conf_description);
		
		name.setTextColor(Color.WHITE);
		description.setTextColor(Color.WHITE);

		name.setText(this.configurations.get(position).getName());
		description.setText(this.configurations.get(position).getDescription());

		if (position % 2 == 0)
			layoutItem.setBackgroundColor(Color.DKGRAY);
		else
			layoutItem.setBackgroundColor(Color.BLACK);
		
		return layoutItem;
	}
	

}
