package com.isd360.philingerie_sms.view;

import com.isd360.philingerie_sms.view.R;
import com.isd360.philingerie_sms.controller.MainController;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activité de la page principal de l'application
 * @author Charlie
 *
 */
public class MainActivity extends Activity {
	
	private int totalDestinataire = 0;
	
	private TextView listLogs = null;
	private TextView statusCount = null;
	private TextView statusMessage = null;
	private Button sendButton = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On déclare les textbox et on met un mouvement de scroll
        this.statusCount = (TextView)this.findViewById(R.id.txt_status_count);
        this.statusMessage = (TextView)this.findViewById(R.id.txt_status_msg);
        this.listLogs = (TextView)this.findViewById(R.id.txt_listEnvoi);
        this.listLogs.setMovementMethod(new ScrollingMovementMethod());
        
        this.sendButton = (Button) findViewById(R.id.sendMessage);
		this.sendButton.setOnClickListener(this.clickSendListener);
    }
    
    /**
     * On remet les champs à zéro lorsque l'on redémarre l'application
     */
    @Override
    public void onStart(){
    	super.onStart();
    	//On réinitialise les champs au démarrage de l'application
    	this.listLogs.setText("");
    	MainActivity.this.statusMessage.setTextColor(Color.GREEN);
    	this.statusMessage.setText("Prêt");
    	this.totalDestinataire = 0;
    	this.updateStatusCount(0);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_config, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Si on clique sur le bouton de menu, on va vers l'activité de configuration
        switch (item.getItemId()) {
           case R.id.menu_config:
        	   Intent intent = new Intent(MainActivity.this,ConfigurationActivity.class);
        	   startActivity(intent);
              return true;
           default:
              return false;
        }
    }
	
	private OnClickListener clickSendListener = new OnClickListener() {
		public void onClick(View view) {			
			
			//On désactive le button d'envoi
			MainActivity.this.setButtonEnable(false);
			
			//On utilise le controlleur pour lancer les traitements relatif à la campagne
			MainController main = new MainController(MainActivity.this);
			main.launchCampaign();
			
			//On réactive le button d'envoi
			MainActivity.this.setButtonEnable(true);
		}
	};
	
	/**
	 * Ajout d'un message à la ligne dans la testBox des logs
	 * @param message le message à notifier
	 */
	public void addMessage(String message){
		final String msg = message;
		runOnUiThread(new Runnable() {
			public void run() {
				//On ajoute la ligne à la TextView
		        MainActivity.this.listLogs.append(msg+"\n");
		        //On va ensuite scroller si nécessaire
		        final int scrollAmount = MainActivity.this.listLogs.getLayout().getLineTop(MainActivity.this.listLogs.getLineCount()) - MainActivity.this.listLogs.getHeight();
		        if(scrollAmount>0)
		            MainActivity.this.listLogs.scrollTo(0, scrollAmount);
		        else
		            MainActivity.this.listLogs.scrollTo(0,0);
			}
		});
    }
	
	/**
	 * Met à jout le nombre de destinataires à qui le msg a été envoyé
	 * @param countDest nombre de destinataires actuels
	 */
	public void updateStatusCount(int countDest){
		final int count = countDest;
		runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.statusCount.setText(count + "/" + MainActivity.this.getTotalDestinataire());
			}
		});
	}
	
	/**
	 * Notifie le status de l'application 
	 * @param statusMsg Message du status à afficher
	 * @param colorMsg Couleur du message à afficher
	 * @param toast Vrai si une notfication Toast avec le message doit être affichée
	 */
	public void updateStatusMsg(String statusMsg,int colorMsg,boolean toast){
		final String msg = statusMsg;
		final int color = colorMsg;
		final boolean makeToast = toast;
		runOnUiThread(new Runnable() {
			public void run() {
				if(makeToast) Toast.makeText(MainActivity.this, msg, 500).show();
				MainActivity.this.statusMessage.setTextColor(color);
				MainActivity.this.statusMessage.setText(msg);
			}
		});
	}
	
	public void setButtonEnable(boolean enable){
		final boolean ena = enable;
		runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.sendButton.setEnabled(ena);
			}
		});
	}
	
	public void emptyLogs(){
		//On lance la notification sur le Thread de l'UI
		this.runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.listLogs.setText("");
			}
		});
	}
	
	public int getTotalDestinataire() {
		return totalDestinataire;
	}

	public void setTotalDestinataire(int totalDestinataire) {
		this.totalDestinataire = totalDestinataire;
	}
}