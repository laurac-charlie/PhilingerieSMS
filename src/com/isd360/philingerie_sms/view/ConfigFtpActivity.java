package com.isd360.philingerie_sms.view;

import com.isd360.philingerie_sms.view.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
/**
 * 
 * @author Charlie
 *
 */
public class ConfigFtpActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.configuration);
        //On ajoute pas une vue mais un ensemble de préférence pour créer l'interace de l'activité
        this.addPreferencesFromResource(R.xml.ftp_parameters);
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
}
