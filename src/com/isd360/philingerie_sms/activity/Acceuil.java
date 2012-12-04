package com.isd360.philingerie_sms.activity;

import java.io.FileNotFoundException;
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
        
        Button btn = (Button) findViewById(R.id.sendMessage);
		btn.setOnClickListener(this.clickSendListener);
    }
    
    /**
     * On remet les champs à zéro lorsque l'on redémarre l'application
     */
    @Override
    public void onStart(){
    	super.onStart();
    	this.listLogs.setText("");
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
			
			//TODO:Fichier de données csv à rendre paramétrable
			String filename = "datasms.csv";
			
			try {
				FTPManager.DownloadCSVfile(filename,Acceuil.this);
				
			} catch (Exception e) {
				Toast.makeText(Acceuil.this,e.getMessage(),500).show();
				Acceuil.this.updateStatusMsg(e.getMessage());
			}
			
			//On lance le Thread d'envoi des messages
			MessageThread mt = new MessageThread(filename);
			mt.start();
			
		}
	};
	
	/**
	 * Ajout d'un message à la ligne dans la testBox des logs
	 * @param message
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
	
	private void updateStatusMsg(String statusMsg){
		final String msg = statusMsg;
		runOnUiThread(new Runnable() {
			public void run() {
				Acceuil.this.statusMessage.setTextColor(Color.RED);
				Acceuil.this.statusMessage.setText(msg);
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
		
		public void run(){
			ParserCSV psr = null;
			try {
				psr = new ParserCSV(this.filename);
			} catch (FileNotFoundException fnf) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(Acceuil.this, "Le fichier spécifié n'a pas été trouvé. Vérifier d'il existe.", 500).show();
						Acceuil.this.updateStatusMsg("Le fichier spécifié n'a pas été trouvé. Vérifier d'il existe.");
					}
				});
			}
			
			runOnUiThread(new Runnable() {
				public void run() {
					Acceuil.this.listLogs.setText("");
				}
			});
			
			//On récupère la liste des destinataires à partir du fichier CSV
			ArrayList<Destinataire> listDest = psr.parseRecipient();
			//On met à jour le nombre de destinataires à traiter
			Acceuil.this.setTotalDestinataire(listDest.size());
			int count = 0;
			
			for (Destinataire d : listDest)
			{
				if(SmsSender.SendMessage(d))
				{
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [OK]";
					count++;
				}
				else
				{
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [KO]";
				}
				
				//On met à jour la liste de log et le statut
				Acceuil.this.addMessage(MessageThread.this.logMsg);
				Acceuil.this.updateStatusCount(count);
				
				//TODO:Vérifier la latence
				try 
					{Thread.sleep(300);} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
			
			Acceuil.this.addMessage("Fin de la campagne");
		}
	}
}