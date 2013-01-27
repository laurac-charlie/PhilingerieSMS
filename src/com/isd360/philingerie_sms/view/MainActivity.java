package com.isd360.philingerie_sms.view;

import com.isd360.philingerie_sms.view.R;
import com.isd360.philingerie_sms.controller.CampagneThread;
import com.isd360.philingerie_sms.controller.MainController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activit� de la page principal de l'application
 * @author Charlie
 *
 */
public class MainActivity extends Activity {
	
	private int totalDestinataire = 0;
	
	private TextView listLogs = null;
	//private String listLogstext = "";
	private TextView statusCount = null;
	//private String statusCountText = "";
	private TextView statusMessage = null;
	//private String statusMessageText = "";
	private ImageButton sendButton = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On d�clare les textbox et on met un mouvement de scroll
        this.statusCount = (TextView)this.findViewById(R.id.txt_status_count);
        this.statusMessage = (TextView)this.findViewById(R.id.txt_status_msg);
        this.listLogs = (TextView)this.findViewById(R.id.txt_listEnvoi);
        this.listLogs.setMovementMethod(new ScrollingMovementMethod());
        
        this.sendButton = (ImageButton) findViewById(R.id.btn_startApp);
		this.sendButton.setOnClickListener(this.clickSendListener);
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	//On r�initialise les champs au d�marrage de l'application seulement si Campagne Thread ne tourne plus.
    	if(!CampagneThread.RUNNING)
    	{
	    	this.listLogs.setText("");
	    	MainActivity.this.statusMessage.setTextColor(Color.GREEN);
	    	this.statusMessage.setText("Pr�t");
	    	this.totalDestinataire = 0;
	    	this.updateStatusCount(0);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_config, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Si on clique sur le bouton de menu, on va vers l'activit� de configuration
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
			
			//On d�sactive le button d'envoi
			MainActivity.this.setButtonEnable(false);
			
			//On utilise le controlleur pour lancer les traitements relatif � la campagne
			final MainController mainController = new MainController(MainActivity.this);
			
			if(mainController.prepareCampaign())
			{
				//On cr�e l'alertDialog qui est en faire
				Builder helpBuilder = new Builder(MainActivity.this);
				helpBuilder.setTitle("Pr�visualation message").setMessage(mainController.getSmsText());
				//helpBuilder.setMessage("texte du sms");
				
				helpBuilder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Une fois que le texte du message a �t� confirmer on lance la campagne
						mainController.launchCampaign();					
					}
				});
				
				helpBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.updateStatusMsg("Le lancement de la campagne a �t� annul�.", Color.RED, true);
					}
				});
	
				// On cr�e la popup puis on la montre
				AlertDialog helpDialog = helpBuilder.create();
				helpDialog.show();
			}
			
			//On r�active le button d'envoi
			//MainActivity.this.setButtonEnable(true);
		}
	};
	
	/**
	 * Ajout d'un message � la ligne dans la testBox des logs
	 * @param message le message � notifier
	 */
	public void addMessage(String message){
		final String msg = message;
		runOnUiThread(new Runnable() {
			public void run() {
				//On ajoute la ligne � la TextView
		        MainActivity.this.listLogs.append(msg+"\n");
		        //On va ensuite scroller si n�cessaire
		        final int scrollAmount = MainActivity.this.listLogs.getLayout().getLineTop(MainActivity.this.listLogs.getLineCount()) - MainActivity.this.listLogs.getHeight();
		        if(scrollAmount>0)
		            MainActivity.this.listLogs.scrollTo(0, scrollAmount);
		        else
		            MainActivity.this.listLogs.scrollTo(0,0);
			}
		});
    }
	
	/**
	 * Met � jout le nombre de destinataires � qui le msg a �t� envoy�
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
	 * @param statusMsg Message du status � afficher
	 * @param colorMsg Couleur du message � afficher
	 * @param toast Vrai si une notfication Toast avec le message doit �tre affich�e
	 */
	public void updateStatusMsg(String statusMsg,int colorMsg,boolean toast){
		final String msg = statusMsg;
		final int color = colorMsg;
		final boolean makeToast = toast;
		runOnUiThread(new Runnable() {
			public void run() {
				if(makeToast) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
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
	
	/**
	 * Vide la liste des log d'envoi de message
	 */
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