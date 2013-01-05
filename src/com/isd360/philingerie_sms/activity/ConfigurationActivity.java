package com.isd360.philingerie_sms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ConfigurationActivity extends PreferenceActivity{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.configuration);
        this.addPreferencesFromResource(R.xml.parameters);
        
        Preference button_ftp = (Preference)this.findPreference("button_serv_ftp");
        button_ftp.setOnPreferenceClickListener(ftpClickListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_retour, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_retour:
               finish();
               return true;
           default:
              return false;
        }
    }
    
    private OnPreferenceClickListener ftpClickListener = new OnPreferenceClickListener() {
		
		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(ConfigurationActivity.this,ConfigFtpActivity.class);
     	   	startActivity(intent);
			return true;
		}
	};
    
    
/*
    private void loadConfiguration(){
    	if(this.list_intervention != null)
		{
	        this.adapter = new ConfigurationListAdapter(this, this.list_intervention);
			ListView list = (ListView) findViewById(R.id.listViewConfiguration);
			list.setAdapter(this.adapter);
			list.setOnItemClickListener(clickListener);
		}
    }
    
    private OnItemClickListener clickListener = new OnItemClickListener() 
    {
		public void onItemClick(AdapterView<?> prent, View v, int position,long id) 
		{
			
		}
    };
    */
}
