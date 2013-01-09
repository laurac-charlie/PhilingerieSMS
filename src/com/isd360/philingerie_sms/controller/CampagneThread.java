package com.isd360.philingerie_sms.controller;

import java.util.ArrayList;

import android.graphics.Color;

import com.isd360.philingerie_sms.model.Destinataire;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * 
 * @author Charlie
 *
 */
public class CampagneThread extends Thread{
	
	private MainActivity main = null;
	private ArrayList<Destinataire> listDest = null;
	private String logMsg = "";
	private String smsText = "";
	
	/**
	 * Initialise le thread d'envoi des SMS 
	 * @param main Activité de la page priincipal à mettre à jour
	 * @param listDest Liste des destinataires à qui envoyé les sms
	 * @param smsText Texte du sms
	 */
	public CampagneThread(MainActivity main,ArrayList<Destinataire> listDest,String smsText){
		this.main = main;
		this.listDest = listDest;
		this.smsText = smsText;
	}
	
	@Override
	public void run(){
		/*
		ParserCSV psr = null;
		ArrayList<Destinataire> listDest = new ArrayList<Destinataire>();
		
		try 
		{
			this.main.updateStatusMsg("Chargement du fichier CSV",Color.BLUE,false);
			
			//On récupère la liste des destinataires à partir du fichier CSV
			psr = new ParserCSV(this.csvfile);
			listDest = psr.parseRecipient(';');
		} catch (FileNotFoundException ex) {
			this.main.updateStatusMsg(ex.getMessage(),Color.RED,true);
		} catch (IOException ex) {
			this.main.updateStatusMsg(ex.getMessage(),Color.RED,true);
		}*/
		
		//On vide la liste de logs
		this.main.emptyLogs();
		
		//On met à jour le nombre de destinataires à traiter
		this.main.setTotalDestinataire(this.listDest.size());
		int count = 0;
		
		this.main.updateStatusMsg("Traitement envoi SMS...",Color.BLUE,false);
					
		for (Destinataire d : this.listDest)
		{
			if(SmsSender.SendMessage(d,this.smsText))
			{
				this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [OK]";
				count++;
			}
			else
				this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [KO]";
			
			//On met à jour la liste de log et le statut
			this.main.addMessage(CampagneThread.this.logMsg);
			this.main.updateStatusCount(count);
			
			//On met à jour le nombre de destinataires à traiter
			this.main.setTotalDestinataire(listDest.size());
			
			//TODO:Vérifier la latence 5s
			try 
				{Thread.sleep(5000);} 
			catch (InterruptedException e) 
				{this.main.updateStatusMsg(e.getMessage(),Color.RED,true);}
		}
		
		this.main.updateStatusMsg("Traitement terminé",Color.GREEN,false);
		this.main.addMessage("Fin de la campagne");
		
	}
}
