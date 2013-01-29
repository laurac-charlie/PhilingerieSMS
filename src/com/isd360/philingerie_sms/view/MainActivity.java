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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Activité de la page principal de l'application
 * @author Charlie
 *
 */
public class MainActivity extends Activity {
	
	public static final int COLOR_BLUE = Color.parseColor("#4C43FE");
	public static final int COLOR_RED = Color.parseColor("#E30000");
	public static final int COLOR_BLACK = Color.parseColor("#000000");
	
	private MainController mainController = null;
	
	private int totalDestinataire = 0;
	
	private TextView txt_journal = null;
	private TextView statusCount = null;
	//private TextView statusMessage = null;
	private ImageButton sendButton = null;
	private ImageButton quitButton = null;
	private ViewFlipper statusFlipper = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On déclare les textbox et on met un mouvement de scroll
        //TODO: Remplacer par les nouveaux champs correspondants
        //this.statusCount = (TextView)this.findViewById(R.id.txt_status_count);
        //this.statusMessage = (TextView)this.findViewById(R.id.txt_status_msg);
        this.txt_journal = (TextView)this.findViewById(R.id.txt_journal);
        this.txt_journal.setMovementMethod(new ScrollingMovementMethod());
        
        //On déclare les bouttons et on leur associe leurs évènements
        this.sendButton = (ImageButton) findViewById(R.id.btn_startApp);
		this.sendButton.setOnClickListener(this.clickSendListener);
		
		this.quitButton = (ImageButton)this.findViewById(R.id.btn_quitApp);
		this.quitButton.setOnClickListener(this.clickQuitListener);
		
		//On initialise le flipper qui permettra de changer le layout de statut
		this.statusFlipper = (ViewFlipper)this.findViewById(R.id.layout_status_flipper);
		this.statusFlipper.setInAnimation(this,R.anim.anim_fadein);
		this.statusFlipper.setOutAnimation(this,R.anim.anim_fadeout);
		//this.statusFlipper.showNext();
		
		//On initialise le controlleur pour lancer les traitements relatif à la campagne
		this.mainController = new MainController(this);
		this.mainController.loadPrerequisites();
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	//On réinitialise les champs au démarrage de l'application seulement si Campagne Thread ne tourne plus.
    	if(!CampagneThread.RUNNING)
    	{
	    	this.statusFlipper.setDisplayedChild(0);
    		this.mainController.loadPrerequisites();
    		this.txt_journal.setText("");
	    	//TODO: Remettre en fonction
	    	//MainActivity.this.statusMessage.setTextColor(Color.GREEN);
	    	//MainActivity.this.statusMessage.setText("Prêt");
	    	//MainActivity.this.totalDestinataire = 0;
	    	//MainActivity.this.updateStatusCount(0);
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
	
    /**
     * Evenement du boutton pour lancer la campagne
     */
	private OnClickListener clickSendListener = new OnClickListener() {
		public void onClick(View view) {
			//ON lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			
			//On désactive le button d'envoi
			MainActivity.this.setButtonEnable(false);
			
			//On va tester la preparation de la campagne pour savoir si on continue
			if(MainActivity.this.mainController.prepareCampaign())
			{
				//On crée l'alertDialog qui est en faire
				Builder helpBuilder = new Builder(MainActivity.this);
				helpBuilder.setTitle("Prévisualation message").setMessage(MainActivity.this.mainController.getSmsText());
				//helpBuilder.setMessage("texte du sms");
				
				helpBuilder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//On change de cadre statut
						MainActivity.this.statusFlipper.setDisplayedChild(1);
						//Une fois que le texte du message a été confirmer on lance la campagne
						MainActivity.this.mainController.launchCampaign();					
					}
				});
				
				helpBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.updateStatusMsg("Le lancement de la campagne a été annulé.", MainActivity.COLOR_RED, true);
					}
				});
	
				// On crée la popup puis on la montre
				AlertDialog helpDialog = helpBuilder.create();
				helpDialog.show();
			}
			
			//On réactive le button d'envoi
			//MainActivity.this.setButtonEnable(true);
		}
	};
	
	/**
	 * Evenement du boutton pour quitter l'application
	 */
	private OnClickListener clickQuitListener = new OnClickListener() {
		public void onClick(View view) {
			//On lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			//On arrête l'application
			MainActivity.this.finish();
		}
	};
	
	/**
	 * Ajout d'un message à la ligne dans la testBox des logs
	 * @param message le message à notifier
	 */
	public void addMessage(String message){
		final String msg = message;
		this.runOnUiThread(new Runnable() {
			public void run() {
				//On s'assure que la couleur est bien le noir
				MainActivity.this.txt_journal.setTextColor(MainActivity.COLOR_BLACK);
				//On ajoute la ligne à la TextView
		        MainActivity.this.txt_journal.append(msg+"\n");
		        //On va ensuite scroller si nécessaire
		        final int scrollAmount = MainActivity.this.txt_journal.getLayout().getLineTop(MainActivity.this.txt_journal.getLineCount()) - MainActivity.this.txt_journal.getHeight();
		        if(scrollAmount>0)
		            MainActivity.this.txt_journal.scrollTo(0, scrollAmount);
		        else
		            MainActivity.this.txt_journal.scrollTo(0,0);
			}
		});
    }
	
	/**
	 * Met à jout le nombre de destinataires à qui le msg a été envoyé
	 * @param countDest nombre de destinataires actuels
	 */
	public void updateStatusCount(int countDest){
		final int count = countDest;
		this.runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.statusCount.setText(count + "/" + MainActivity.this.totalDestinataire);
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
		this.runOnUiThread(new Runnable() {
			public void run() {
				if(makeToast) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				//MainActivity.this.statusMessage.setTextColor(color);
				//MainActivity.this.statusMessage.setText(msg);
				MainActivity.this.txt_journal.setTextColor(color);
				MainActivity.this.txt_journal.setText(msg);
			}
		});
	}
	
	public void setButtonEnable(boolean enable){
		final boolean ena = enable;
		this.runOnUiThread(new Runnable() {
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
				MainActivity.this.txt_journal.setText("");
			}
		});
	}
	
	/**
	 * Met à jour le statut du réseau dans le cadre
	 * @param isOK Indique si un réseau est accessible
	 * @param network Indique quel réseau est accesible
	 */
	public void setNetworkState(boolean isOK, String network){
		//On va mettre à jour le statut réseau de l'interface
		TextView networkOk = (TextView)this.findViewById(R.id.stat_reseau);
		TextView networkType = (TextView)this.findViewById(R.id.stat_reseau_type);
		
		//Puisque l'on utilise pas de thread pour mettre à jour ces valeurs, on a pas besoin d'utiliser runOnUiThread		
		//On met à jour le texte et les couleurs selon l'état du réseau
		networkOk.setText(isOK ? R.string.statut_OK : R.string.statut_KO);
		networkOk.setTextColor(isOK ? MainActivity.COLOR_BLUE : MainActivity.COLOR_RED);
		
		networkType.setText(network);
	}
	
	/**
	 * Met à jour le statut de la carte SD dans le cadre
	 * @param isOK Indique si la carte SD est utilisable
	 */
	public void setSDcardState(boolean isOK){
		TextView sdCardOk = (TextView)this.findViewById(R.id.stat_sdcard);
		
		//Puisque l'on utilise pas de thread pour mettre à jour ces valeurs, on a pas besoin d'utiliser runOnUiThread
		//On met à jour le texte et les couleurs selon l'état du réseau
		sdCardOk.setText(isOK ? R.string.statut_OK : R.string.statut_KO);
		sdCardOk.setTextColor(isOK ? MainActivity.COLOR_BLUE : MainActivity.COLOR_RED);
	}
	
	/**
	 * Met à jour le statut de la connection ftp
	 * @param isOK Indique si la connection ftp est utilisable
	 * @param error Message d'erreur décrivant l'erreur de connection (vide si aucune erreur)
	 */
	public void setFtpState(boolean isOK, String error){
		TextView ftpOk = (TextView)this.findViewById(R.id.stat_ftpserv);
		TextView ftpError = (TextView)this.findViewById(R.id.stat_ftpserv_type);
		
		//On met à jour le texte et les couleurs selon l'accès au serveur ftp
		ftpOk.setText(isOK ? R.string.statut_OK : R.string.statut_KO);
		ftpOk.setTextColor(isOK ? MainActivity.COLOR_BLUE : MainActivity.COLOR_RED);
		
		ftpError.setText(error);
		
	}
	
	public int getTotalDestinataire() {
		return totalDestinataire;
	}

	public void setTotalDestinataire(int totalDestinataire) {
		this.totalDestinataire = totalDestinataire;
	}
}
