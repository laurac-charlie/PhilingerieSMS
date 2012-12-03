package com.isd360.philingerie_sms.activity;

import java.util.ArrayList;

import com.isd360.philingerie_sms.entity.Destinataire;
import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;
import com.isd360.philingerie_sms.util.SmsSender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Acceuil extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn = (Button) findViewById(R.id.sendMessage);
		btn.setOnClickListener(this.clickSendListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_config, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_config:
        	   Acceuil.this.clickMenuListener.onClick(getCurrentFocus());
              return true;
           default:
              return false;
        }
    }
    
    private OnClickListener clickMenuListener = new OnClickListener() {
		public void onClick(View view) {
				Intent intent = new Intent(Acceuil.this,ConfigurationActivity.class);
				startActivity(intent);
		}
	};
	
	private OnClickListener clickSendListener = new OnClickListener() {
		public void onClick(View view) {			
			
			String filename = "test.csv";
			ParserCSV psr = null;
			
			try {
				FTPManager.DownloadCSVfile(filename,Acceuil.this);
				psr = new ParserCSV(filename);
			} catch (Exception e) {
				Toast.makeText(Acceuil.this,e.getMessage(),300).show();
			}
			
			
			for (Destinataire d : psr.parseRecipient())
			{
				try 
					{Thread.sleep(1000);} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
				
				if (SmsSender.SendMessage(d))
					Toast.makeText(Acceuil.this, "Le message a bien été envoyé à" + d.getLastName() + " " + d.getFirstName(), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(Acceuil.this, "Erreur d'envoi du message", Toast.LENGTH_SHORT).show();
			}
		}
	};
}