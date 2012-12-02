package com.isd360.philingerie_sms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
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
		@SuppressWarnings("deprecation")
		public void onClick(View view) {
			String num = "0690126858";
			String msg = "Message de Test Android";
			//Si le numéro est supérieur à 4 charactère et que le message n'est pas vide on lance la procédure d'envoi
			if(num.length()>= 4 && msg.length() > 0){
				//Grâce à l'objet de gestion de SMS (SmsManager) que l'on récupère grâce à la méthode static getDefault()
				//On envoit le SMS à l'aide de la méthode sendTextMessage
				SmsManager.getDefault().sendTextMessage(num, null, msg, null, null);
			}else{
				//On affiche un petit message d'erreur dans un Toast
				Toast.makeText(Acceuil.this, "Enter le numero et/ou le message", Toast.LENGTH_SHORT).show();
			}
		}
	};
}