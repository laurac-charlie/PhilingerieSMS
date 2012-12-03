package com.isd360.philingerie_sms.activity;

import java.io.File;

import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
			//Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory().getPath(), 300).show();
			
			ParserCSV psr = new ParserCSV("/PHILINGERIE-SMS.csv");
			Toast.makeText(getApplicationContext(),psr.parseRecipient().get(2).getNumero(), 500).show();
			//Toast.makeText(getApplicationContext(),psr.parseFile().get(0), 500).show();
			//FTPManager.DownloadCSVfile("smsg05.csv");
			
			/*String num = "0690126858";
			String msg = "Message de Test Android";
			
			if (SmsSender.SendMessage(num, msg))
				Toast.makeText(Acceuil.this, "Le message a bien été envoyé", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(Acceuil.this, "Erreur d'envoi du message", Toast.LENGTH_SHORT).show();*/
		}
	};
}