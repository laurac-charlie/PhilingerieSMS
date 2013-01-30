package com.isd360.philingerie_sms.controller;

import java.util.ArrayList;

import android.graphics.Color;

import com.isd360.philingerie_sms.model.Destinataire;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * Thread d'envoi des SMS � la liste de destinaires
 * @author Charlie
 *
 */
public class CampagneThread extends Thread{
	
	private MainActivity main = null;
	private ArrayList<Destinataire> listDest = null;
	private String smsText = "";
	
	/**
	 * Pour que l'information soit disponible partout dans l'application, on va utiliser la varible running qui sera mis � jour
	 * manuellement pendant la vie du Thread (et en cas de plantage) pl�tot que la fonction getState qui n�cessiterait
	 * d'avoir acc�s � l'objet.
	 */
	public static boolean RUNNING = false;
	
	/**
	 * Initialise le thread d'envoi des SMS 
	 * @param main Activit� de la page priincipal � mettre � jour
	 * @param listDest Liste des destinataires � qui envoy� les sms
	 * @param smsText Texte du sms
	 */
	public CampagneThread(MainActivity main,ArrayList<Destinataire> listDest,String smsText){
		super();
		this.main = main;
		this.listDest = listDest;
		this.smsText = smsText;
	}
	
	@Override
	public void run(){
		//On met la variale running � vrai pour dire que le thread est actif
		CampagneThread.RUNNING = true;
		
		//On vide la liste de logs
		this.main.emptyLogs();
		
		//On met � jour le nombre de destinataires � traiter
		this.main.setTotalDestinataire(this.listDest.size());
		String logMsg = "";
		int countAll = 0;
		int countOk = 0;
		int countKo = 0;
		
		this.main.updateStatusMsg("Traitement envoi SMS...",Color.BLUE,false);
					
		for (Destinataire d : this.listDest)
		{
			if(SmsSender.SendMessage(d,this.smsText))
			{
				logMsg = "Envoi [OK] : " + d.getLastName() + " " + d.getFirstName();
				
				countOk++;
			}
			else
			{
				logMsg = "Envoi [KO] : " + d.getLastName() + " " + d.getFirstName();
				countKo++;
			}
			countAll++;
			
			//On met � jour la liste de log et le statut
			this.main.addMessage(logMsg);
			this.main.updateStatusCount(countAll,countOk,countKo);
			
			//On met � jour le nombre de destinataires � traiter
			this.main.setTotalDestinataire(listDest.size());
			
			try 
				{Thread.sleep(5000);} 
			catch (InterruptedException e) 
				{this.main.updateStatusMsg(e.getMessage(),Color.RED,true);}
		}
		//On remet la varibale � faut � la fin du traitement
		CampagneThread.RUNNING = false;
		//On r�active le button d'envoi
		this.main.setButtonEnable(true);
		this.main.updateStatusMsg("Traitement termin�",Color.GREEN,false);
		this.main.addMessage("Fin de la campagne");
		
	}
}
