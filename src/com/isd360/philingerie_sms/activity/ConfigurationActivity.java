package com.isd360.philingerie_sms.activity;

import java.util.ArrayList;

import com.isd360.philingerie_sms.adapter.ConfigurationListAdapter;
import com.isd360.philingerie_sms.entity.Configuration;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigurationActivity extends Activity {
	
	private ArrayList<Configuration> list_intervention = new ArrayList<Configuration>();
	private ConfigurationListAdapter adapter = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        
        //On crée les 2 menus de configuration 
        this.list_intervention.add(new Configuration(0,"Serveur FTP","Réglage des paramètres du serveur FTP."));
        this.list_intervention.add(new Configuration(1,"Fichier CSV","Fichier CSV du ftp à utiliser pour obtenir la liste des contacts."));
        this.loadConfiguration();
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
    
}
