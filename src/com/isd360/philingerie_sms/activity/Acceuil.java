package com.isd360.philingerie_sms.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.isd360.philingerie_sms.entity.Destinataire;
import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.util.StringChecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Acceuil extends Activity {
	
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
        
        //On d�clare les textbox et on met un mouvement de scroll
        this.statusCount = (TextView)this.findViewById(R.id.txt_status_count);
        this.statusMessage = (TextView)this.findViewById(R.id.txt_status_msg);
        this.listLogs = (TextView)this.findViewById(R.id.txt_listEnvoi);
        this.listLogs.setMovementMethod(new ScrollingMovementMethod());
        
        this.sendButton = (Button) findViewById(R.id.sendMessage);
		this.sendButton.setOnClickListener(this.clickSendListener);
		
    }
    
    /**
     * On remet les champs � z�ro lorsque l'on red�marre l'application
     */
    @Override
    public void onStart(){
    	super.onStart();
    	//On r�initialise les champs au d�marrage de l'application
    	this.listLogs.setText("");
    	Acceuil.this.statusMessage.setTextColor(Color.GREEN);
    	this.statusMessage.setText("Pr�t");
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
    	//Si on clique sur le bouton de menu, on va vers l'activit� de configuration
        switch (item.getItemId()) {
           case R.id.menu_config:
        	   Intent intent = new Intent(Acceuil.this,ConfigurationActivity.class);
        	   startActivity(intent);
              return true;
           default:
              return false;
        }
    }
	
	private OnClickListener clickSendListener = new OnClickListener() {
		public void onClick(View view) {			
			
			//On d�sactive le button d'envoi
			Acceuil.this.setButtonEnable(false);
			
			//On initialise les param�tres � partir des pr�f�rences pr�configur�es
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Acceuil.this);
			boolean connectionOk = true;
			String ftp_host = (StringChecker.validIP(prefs.getString("param_serveur_ip", ""))) ? prefs.getString("param_serveur_ip", "") : "";
			String ftp_login = prefs.getString("param_serveur_login", "");
			String ftp_pass = prefs.getString("param_serveur_pass", "");
			
			boolean fileOk = true;
			String csvfile = prefs.getString("param_fichier_csv", "");
			//csvfile = "datasmsg.csv";
			
			FTPManager ftpManager = new FTPManager(ftp_host, ftp_login, ftp_pass);
			
			//On teste la validit� des param�tres
			if(!ftpManager.tryFtpConnection(Acceuil.this))
			{
				connectionOk = false;
				Acceuil.this.updateStatusMsg("Les param�tres de connexion au serveur ftp sont incorrectes, veuillez les v�rifier.", Color.RED, true);
			}
			
			if(csvfile.equals(""))
			{
				fileOk = false;
				Acceuil.this.updateStatusMsg("Le nom du fichier de contact csv n'a pas �t� configur�.", Color.RED, true);
			}
			
			//Si le fichier existe en local et que son nom a bien �t� renseign� on peut lancer le traitement (quand bien m�me la connection aurait �chou�)
			//Ou si la connection est disponible et que le nom du fichier existe bien, on va tenter le t�l�chargement
			if((fileOk && (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + csvfile)).exists()) || (fileOk && connectionOk))
			{
				//Si la connection fonctionne, on va tent� de t�l�charger le fichier csv
				if(connectionOk)
				{
					try {
						Acceuil.this.updateStatusMsg("T�l�chargement du fichier : " + csvfile,Color.BLUE,false);
						//T�lchagement du fichier de contact
						ftpManager.DownloadCSVfile(csvfile);
						
					} catch (Exception e) {
						Acceuil.this.updateStatusMsg(e.getMessage(),Color.RED,true);
					}
				}
				
				//On lance le Thread d'envoi des messages
				MessageThread mt = new MessageThread(csvfile);
				mt.start();
			}

			//On r�active le button d'envoi
			Acceuil.this.setButtonEnable(true);
		}
	};
	
	/**
	 * Ajout d'un message � la ligne dans la testBox des logs
	 * @param message le message � notifier
	 */
	private void addMessage(String message){
		final String msg = message;
		runOnUiThread(new Runnable() {
			public void run() {
				//On ajoute la ligne � la TextView
		        Acceuil.this.listLogs.append(msg+"\n");
		        //On va ensuite scroller si n�cessaire
		        final int scrollAmount = Acceuil.this.listLogs.getLayout().getLineTop(Acceuil.this.listLogs.getLineCount()) - Acceuil.this.listLogs.getHeight();
		        if(scrollAmount>0)
		            Acceuil.this.listLogs.scrollTo(0, scrollAmount);
		        else
		            Acceuil.this.listLogs.scrollTo(0,0);
			}
		});
    }
	
	/**
	 * Met � jout le nombre de destinataires � qui le msg a �t� envoy�
	 * @param countDest nombre de destinataires actuels
	 */
	private void updateStatusCount(int countDest){
		final int count = countDest;
		runOnUiThread(new Runnable() {
			public void run() {
				Acceuil.this.statusCount.setText(count + "/" + Acceuil.this.getTotalDestinataire());
			}
		});
	}
	
	/**
	 * Notifie le status de l'application 
	 * @param statusMsg Message du status � afficher
	 * @param colorMsg Couleur du message � afficher
	 * @param toast Vrai si une notfication Toast avec le message doit �tre affich�e
	 */
	private void updateStatusMsg(String statusMsg,int colorMsg,boolean toast){
		final String msg = statusMsg;
		final int color = colorMsg;
		final boolean makeToast = toast;
		runOnUiThread(new Runnable() {
			public void run() {
				if(makeToast) Toast.makeText(Acceuil.this, msg, 500).show();
				Acceuil.this.statusMessage.setTextColor(color);
				Acceuil.this.statusMessage.setText(msg);
			}
		});
	}
	
	private void setButtonEnable(boolean enable){
		final boolean ena = enable;
		runOnUiThread(new Runnable() {
			public void run() {
				Acceuil.this.sendButton.setEnabled(ena);
			}
		});
	}
	
	public int getTotalDestinataire() {
		return totalDestinataire;
	}

	public void setTotalDestinataire(int totalDestinataire) {
		this.totalDestinataire = totalDestinataire;
	}

	/**
	 * 
	 * @author Charlie
	 *
	 */
	private class MessageThread extends Thread{
		
		private String csvfile = "";
		private String logMsg = "";
		
		public MessageThread(String csvfile){
			this.csvfile = csvfile;
		}
		
		@Override
		public void run(){
			
			ParserCSV psr = null;
			ArrayList<Destinataire> listDest = new ArrayList<Destinataire>();
			
			try 
			{
				Acceuil.this.updateStatusMsg("Chargement du fichier CSV",Color.BLUE,false);
				
				//On r�cup�re la liste des destinataires � partir du fichier CSV
				psr = new ParserCSV(this.csvfile);
				listDest = psr.parseRecipient(';');
			} catch (FileNotFoundException ex) {
				Acceuil.this.updateStatusMsg(ex.getMessage(),Color.RED,true);
			} catch (IOException ex) {
				Acceuil.this.updateStatusMsg(ex.getMessage(),Color.RED,true);
			}
			
			//On lance la notification sur le Thread de l'UI
			runOnUiThread(new Runnable() {
				public void run() {
					Acceuil.this.listLogs.setText("");
				}
			});
			
			
			//On met � jour le nombre de destinataires � traiter
			Acceuil.this.setTotalDestinataire(listDest.size());
			int count = 0;
			
			Acceuil.this.updateStatusMsg("Traitement envoi SMS...",Color.BLUE,false);
						
			for (Destinataire d : listDest)
			{
				if(SmsSender.SendMessage(d))
				{
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [OK]";
					count++;
				}
				else
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [KO]";
				
				//On met � jour la liste de log et le statut
				Acceuil.this.addMessage(MessageThread.this.logMsg);
				Acceuil.this.updateStatusCount(count);
				
				//On met � jour le nombre de destinataires � traiter
				Acceuil.this.setTotalDestinataire(listDest.size());
				
				//TODO:V�rifier la latence 5s
				try 
					{Thread.sleep(5000);} 
				catch (InterruptedException e) 
					{Acceuil.this.updateStatusMsg(e.getMessage(),Color.RED,true);}
			}
			
			Acceuil.this.updateStatusMsg("Traitement termin�",Color.GREEN,false);
			Acceuil.this.addMessage("Fin de la campagne");
			
		}
	}
}