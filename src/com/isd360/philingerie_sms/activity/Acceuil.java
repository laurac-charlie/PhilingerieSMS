package com.isd360.philingerie_sms.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.isd360.philingerie_sms.entity.Destinataire;
import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;
import com.isd360.philingerie_sms.util.SmsSender;

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
    	this.listLogs.setText("");
    	Acceuil.this.statusMessage.setTextColor(Color.GREEN);
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
			
			//On désactive le button d'envoi
			Acceuil.this.setButtonEnable(false);
			
			//TODO:Fichier de données csv à rendre paramétrable
			String filename = "datasmsg.csv";
			
			try {
				Acceuil.this.updateStatusMsg("Téléchargement du fichier : " + filename,Color.BLUE,false);
				FTPManager.DownloadCSVfile(filename);
				
			} catch (Exception e) {
				Acceuil.this.updateStatusMsg(e.getMessage(),Color.RED,true);
			}
			
			//On lance le Thread d'envoi des messages
			MessageThread mt = new MessageThread(filename);
			mt.start();
			

			//On réactive le button d'envoi
			Acceuil.this.setButtonEnable(true);
		}
	};
	
	/**
	 * Ajout d'un message à la ligne dans la testBox des logs
	 * @param message le message à notifier
	 */
	private void addMessage(String message){
		final String msg = message;
		runOnUiThread(new Runnable() {
			public void run() {
				//On ajoute la ligne à la TextView
		        Acceuil.this.listLogs.append(msg+"\n");
		        //On va ensuite scroller si nécessaire
		        final int scrollAmount = Acceuil.this.listLogs.getLayout().getLineTop(Acceuil.this.listLogs.getLineCount()) - Acceuil.this.listLogs.getHeight();
		        if(scrollAmount>0)
		            Acceuil.this.listLogs.scrollTo(0, scrollAmount);
		        else
		            Acceuil.this.listLogs.scrollTo(0,0);
			}
		});
    }
	
	/**
	 * Met à jout le nombre de destinataires à qui le msg a été envoyé
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
	 * @param statusMsg Message du status à afficher
	 * @param colorMsg Couleur du message à afficher
	 * @param toast Vrai si une notfication Toast avec le message doit être affichée
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
		
		public MessageThread(String filename){
			this.filename = filename;
		}
		
		private String filename = "";
		private String logMsg = "";
		
		@Override
		public void run(){
			
			ParserCSV psr = null;
			ArrayList<Destinataire> listDest = null;
			
			try 
			{
				Acceuil.this.updateStatusMsg("Chargement du fichier CSV",Color.BLUE,false);
				
				//On récupère la liste des destinataires à partir du fichier CSV
				psr = new ParserCSV(this.filename);
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
			
			
			//On met à jour le nombre de destinataires à traiter
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
				
				//On met à jour la liste de log et le statut
				Acceuil.this.addMessage(MessageThread.this.logMsg);
				Acceuil.this.updateStatusCount(count);
				
				//On met à jour le nombre de destinataires à traiter
				Acceuil.this.setTotalDestinataire(listDest.size());
				
				//TODO:Vérifier la latence 5s
				try 
					{Thread.sleep(5000);} 
				catch (InterruptedException e) 
					{Acceuil.this.updateStatusMsg(e.getMessage(),Color.RED,true);}
			}
			
			Acceuil.this.updateStatusMsg("Traitement terminé",Color.GREEN,false);
			Acceuil.this.addMessage("Fin de la campagne");
			
		}
	}
}