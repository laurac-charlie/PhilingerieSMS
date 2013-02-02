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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Activité de la page principal de l'application
 * @author Charlie
 *
 */
public class MainActivity extends Activity {
	
	public static final int COLOR_YELLOW = Color.parseColor("#FFB400");
	public static final int COLOR_BLUE = Color.parseColor("#4C43FE");
	public static final int COLOR_GREEN = Color.parseColor("#1FA055");
	public static final int COLOR_RED = Color.parseColor("#E30000");
	public static final int COLOR_BLACK = Color.parseColor("#000000");
	
	private MainController mainController = null;
	
	private TextView txt_journal = null;
	private TextView statusCount = null;
	private TextView statusCountSent = null;
	private TextView statusCountFail = null;
	
	private ImageButton sendButton = null;
	private ImageButton pauseButton = null;
	private ImageButton stopButton = null;
	private ImageButton acceuilButton = null;
	private ImageButton resumeButton = null;
	private ImageButton quitButton = null;
	
	private ViewFlipper statusFlipper = null;
	private ViewFlipper startFlipper = null;
	private ViewFlipper stopFlipper = null;
	private ViewFlipper resumeFlipper = null;
	private ViewFlipper acceuilFlipper = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On charge les données de l'interface
        this.loadInterface();
    }

    /**
     * Charge les différents champs pour les manipuler ensuite
     */
	private void loadInterface() {
		
		/** CHAMPS STATUTS **/
		//Les champs de count sont déclarés une seule fois en global puisqu'ils seront souvent mis à jour
		this.statusCount = (TextView)this.findViewById(R.id.stat_count_sms_global);
		this.statusCountSent = (TextView)this.findViewById(R.id.stat_count_sms_sent);
		this.statusCountFail = (TextView)this.findViewById(R.id.stat_count_sms_fail);
		
		//On déclare la textview journal et on met un mouvement de scroll
        this.txt_journal = (TextView)this.findViewById(R.id.txt_journal);
        this.txt_journal.setMovementMethod(new ScrollingMovementMethod());
        
        /** BUTTONS **/
        //On déclare les bouttons et on leur associe leurs évènements
        this.sendButton = (ImageButton) findViewById(R.id.btn_startApp);
		this.sendButton.setOnClickListener(this.clickSendListener);
		
		this.pauseButton = (ImageButton) findViewById(R.id.btn_traitement_pause);
		this.pauseButton.setOnClickListener(this.clickPauseListener);
		
		this.stopButton = (ImageButton) findViewById(R.id.btn_traitement_stop);
		this.stopButton.setOnClickListener(this.clickStopListener);
		
		this.resumeButton = (ImageButton) findViewById(R.id.btn_traitement_resume);
		this.resumeButton.setOnClickListener(this.clickResumeListener);
		
		this.acceuilButton = (ImageButton) findViewById(R.id.btn_acceuil);
		this.acceuilButton.setOnClickListener(this.clickAcceuilListener);
		
		this.quitButton = (ImageButton)this.findViewById(R.id.btn_quitApp);
		this.quitButton.setOnClickListener(this.clickQuitListener);
		
		/** VIEW FLIPPER **/
		//On initialise les flipper qui permettront de changer le layout de statut et les bouttons avec leurs animations
		this.statusFlipper = (ViewFlipper)this.findViewById(R.id.layout_status_flipper);
		this.statusFlipper.setInAnimation(this,R.anim.anim_slideout);
		this.statusFlipper.setOutAnimation(this,R.anim.anim_slidein);
		
		this.stopFlipper = (ViewFlipper)this.findViewById(R.id.btn_stop_flipper);
		this.stopFlipper.setInAnimation(this,R.anim.anim_slideout);
		this.stopFlipper.setOutAnimation(this,R.anim.anim_slidein);
		
		this.startFlipper = (ViewFlipper)this.findViewById(R.id.btn_start_flipper);
		this.startFlipper.setInAnimation(this,R.anim.anim_slideout);
		this.startFlipper.setOutAnimation(this,R.anim.anim_slidein);
		
		this.resumeFlipper = (ViewFlipper)this.findViewById(R.id.btn_resume_flipper);
		this.resumeFlipper.setInAnimation(this,R.anim.anim_slideout);
		this.resumeFlipper.setOutAnimation(this,R.anim.anim_slidein);
		
		this.acceuilFlipper = (ViewFlipper)this.findViewById(R.id.btn_acceuil_flipper);
		this.acceuilFlipper.setInAnimation(this,R.anim.anim_slideout);
		this.acceuilFlipper.setOutAnimation(this,R.anim.anim_slidein);
		
		/** Controlleur **/
		//On initialise le controlleur pour lancer les traitements relatif à la campagne
		this.mainController = new MainController(this);
		this.mainController.loadPrerequisites();
	}
    
    @Override
    public void onStart(){
    	super.onStart();
    	
    	//On empêche la mise en veille de l'application
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
    	//On réinitialise les champs au démarrage de l'application seulement si Campagne Thread ne tourne plus.
    	if(!CampagneThread.RUNNING)
    	{
	    	//On replace la bonne fênetre de statut si besoin
    		this.flipLayout(0);
    		//On recharge les prérequis
    		this.mainController.loadPrerequisites();
    		this.setStepMessage(R.string.txt_step_init);
    		this.emptyLogs();
	    	this.updateStatusCount(0, 0, 0, 0);
    	}
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	//On réactive la mise en veille
    	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
			
			//On va tester la preparation de la campagne pour savoir si on continue
			if(MainActivity.this.mainController.prepareCampaign())
			{
				//On crée l'alertDialog qui est en faire
				Builder helpBuilder = new Builder(MainActivity.this);
				helpBuilder.setTitle("Prévisualation message").setMessage(MainActivity.this.mainController.getSmsText());
				
				helpBuilder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//On change de cadre statut et les bouttons
						MainActivity.this.flipLayout(1);
						MainActivity.this.setStepMessage(R.string.txt_step_traitement);
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
		}
	};
	
	/**
	 * Evenement du boutton pour mettre l'application en pause
	 */
	private OnClickListener clickPauseListener = new OnClickListener() {
		public void onClick(View view) {
			//On lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			
			//On arrête l'application 
			CampagneThread.PAUSED = true;
			MainActivity.this.setCampagneState("[EN PAUSE]", MainActivity.COLOR_YELLOW);
			MainActivity.this.flipButtonResume(true);
		}
	};
	
	/**
	 * Evenement du boutton pour relancer l'application
	 */
	private OnClickListener clickResumeListener = new OnClickListener() {
		public void onClick(View view) {
			//On lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			
			//On redémarre l'application
			CampagneThread.PAUSED = false;
			MainActivity.this.setCampagneState(MainActivity.this.getString(R.string.statut_en_cours),MainActivity.COLOR_BLUE);
			MainActivity.this.flipButtonResume(false);
		}
	};
	
	/**
	 * Evenement du boutton pour mettre l'application en pause
	 */
	private OnClickListener clickStopListener = new OnClickListener() {
		public void onClick(View view) {
			//On lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			//On arrête l'application
			CampagneThread.STOPPED = true;
			MainActivity.this.flipButtonAcceuil();
		}
	};
	
	/**
	 * Evenement du boutton pour retourner à la page d'acceuil
	 */
	private OnClickListener clickAcceuilListener = new OnClickListener() {
		public void onClick(View view) {
			//On lance l'animation alpha du boutton
			Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
			view.startAnimation(animAlpha);
			MainActivity.this.setStepMessage(R.string.txt_step_init);
			MainActivity.this.emptyLogs();
			//MainActivity.this.acceuilFlipper.setDisplayedChild(0);
			MainActivity.this.flipLayout(0);

			MainActivity.this.setCampagneState(MainActivity.this.getString(R.string.statut_en_cours), MainActivity.COLOR_BLUE);
			MainActivity.this.updateStatusCount(0, 0, 0, 0);
			
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
	 * Change la fênetre pour correspondre à l'écran principal ou la fenêtre de traitement
	 * @param pos 0 : Layout Acceuil / 1 : Layout Traitement
	 */
	public void flipLayout(int pos){
		if(pos != 0 && pos != 1) pos = 0;
		
		if(this.statusFlipper.getDisplayedChild() != pos) this.statusFlipper.setDisplayedChild(pos);
		if(this.startFlipper.getDisplayedChild() != pos) this.startFlipper.setDisplayedChild(pos);
		if(this.stopFlipper.getDisplayedChild() != pos) this.stopFlipper.setDisplayedChild(pos);
		//Pour le flip acceuil on doit juste s'assurer de le retirer mais pas l'ajouter ici
		if(this.acceuilFlipper.getDisplayedChild() != 0) this.acceuilFlipper.setDisplayedChild(0);
		//POur le flip resume on doit juste s'assurer de le retirer
		if(this.resumeFlipper.getDisplayedChild() != 0) this.resumeFlipper.setDisplayedChild(0);
	}
	
	/**
	 * Passe des bouttons de traitement au boutton d'acceuil
	 */
	public void flipButtonAcceuil(){
		this.runOnUiThread(new Runnable() {
			public void run() {
				if(MainActivity.this.acceuilFlipper.getDisplayedChild() == 0) MainActivity.this.acceuilFlipper.setDisplayedChild(1);
			}
		});
	}
	
	/**
	 * Passe le boutton pause à relancer ou l'inverse
	 */
	public void flipButtonResume(boolean pause){
		final boolean p = pause;
		this.runOnUiThread(new Runnable() {
			public void run() {
				if(p && MainActivity.this.resumeFlipper.getDisplayedChild() == 0) MainActivity.this.resumeFlipper.setDisplayedChild(1);
				if(!p && MainActivity.this.resumeFlipper.getDisplayedChild() == 1) MainActivity.this.resumeFlipper.setDisplayedChild(0);
			}
		});
	}
	
	/**
	 * Met à jour le message de l'étape en cours
	 * @param stepMessage Texte du message d'étape (référence R.strings)
	 */
	public void setStepMessage(int stepMessage){
		TextView etape = (TextView) this.findViewById(R.id.txt_step);
		etape.setText(stepMessage);
	}
	
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
	 * Met à jour les nombres de messages envoyés, en réussite ou en échec
	 * @param totalDest Nombre total de destinataire
	 * @param currentTotal Nombre de tentative d'envoi
	 * @param countSucess Nombre de message envoyé
	 * @param countFail Nombre de message non envoyé
	 */
	public void updateStatusCount(int totalDest ,int currentTotal, int countSucess, int countFail){
		final String total = String.valueOf(totalDest);
		final String current = String.valueOf(currentTotal);
		final String fail = String.valueOf(countFail);
		final String sucess = String.valueOf(countSucess);
		//La méthode est appelé depuis une autre thread
		this.runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.statusCount.setText(current+ "/" + total);
				MainActivity.this.statusCountFail.setText(fail);
				MainActivity.this.statusCountSent.setText(sucess);
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
		//final boolean makeToast = toast;
		this.runOnUiThread(new Runnable() {
			public void run() {
				//if(makeToast) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				MainActivity.this.txt_journal.setTextColor(color);
				MainActivity.this.txt_journal.setText(msg);
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
		//Les champs n'étant pas mis à jour en permanence, on les déclare à chaque utilisation
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
		//Le champ n'étant pas mis à jour en permanence, on le déclare à chaque utilisation
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
		//Les champs n'étant pas mis à jour en permanence, on les déclare à chaque utilisation
		TextView ftpOk = (TextView)this.findViewById(R.id.stat_ftpserv);
		TextView ftpError = (TextView)this.findViewById(R.id.stat_ftpserv_type);
		
		//On met à jour le texte et les couleurs selon l'accès au serveur ftp
		ftpOk.setText(isOK ? R.string.statut_OK : R.string.statut_KO);
		ftpOk.setTextColor(isOK ? MainActivity.COLOR_BLUE : MainActivity.COLOR_RED);
		
		ftpError.setText(error);
		
	}
	
	/**
	 * Met à jour le statut dernier envoi
	 * @param date Date/heure du dernier envoi
	 */
	public void setDateEnvoi(String date){
		TextView statusLastSent = (TextView)this.findViewById(R.id.stat_lastsent);
		TextView lastSent = (TextView)this.findViewById(R.id.stat_lastsent_text);
		
		statusLastSent.setText(date.equals("") ? R.string.statut_KO : R.string.statut_OK);
		statusLastSent.setTextColor(date.equals("") ? MainActivity.COLOR_RED : MainActivity.COLOR_BLUE);
		
		lastSent.setText(date.equals("") ? "N/A": date);
	}
	
	/**
	 * Met à jour l'état du traitement
	 * @param state Chaîne de caractères représentant l'état
	 * @param color Couleur du texte
	 */
	public void setCampagneState(String state, int color){
		final String stat = state;
		final int colo = color;
		//La méthode est appelé depuis une autre thread
		this.runOnUiThread(new Runnable() {
			public void run() {
				//Le champ n'étant pas mis à jour en permanence, on le déclare à chaque utilisation
				TextView traitement_sms = (TextView)MainActivity.this.findViewById(R.id.stat_traitement_sms);
				
				traitement_sms.setTextColor(colo);
				traitement_sms.setText(stat);
			}
		});
	}
}
