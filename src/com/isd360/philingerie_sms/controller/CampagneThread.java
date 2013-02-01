package com.isd360.philingerie_sms.controller;

import java.util.ArrayList;

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
	
	public static boolean PAUSED = false;
	public static boolean STOPPED = false;
	
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
		CampagneThread.PAUSED = false;
		CampagneThread.STOPPED = false;
		
		//On initialise les variables qui seront en permanence mise � jour
		String logMsg = "";
		String message_traitement = "Traitement termin�";
		int countAll = 0;
		int countOk = 0;
		int countKo = 0;
		
		//On temporise (pour l'effet)
		try 
			{Thread.sleep(2000);} 
		catch (InterruptedException e) 
			{this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);}
		
		this.main.updateStatusMsg("Traitement envoi SMS...",MainActivity.COLOR_BLUE,false);
		
		//On temporise (pour l'effet)
		try 
			{Thread.sleep(2000);} 
		catch (InterruptedException e) 
			{this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);}
		
		this.main.emptyLogs();
		
		LOOP: for (Destinataire d : this.listDest)
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
			this.main.updateStatusCount(listDest.size(),countAll,countOk,countKo);
			
			//On arr�te le thread si la variable a �t� mise � jour autre part
			if(CampagneThread.STOPPED) {
				message_traitement = "Traitement arr�ter";
				break;
			}
			
			//Si on met l'application en pause on tourne dans la boucle 
			//TODO: La boucle ne devrait pas tourner � l'infini
			while(CampagneThread.PAUSED){
				if(CampagneThread.STOPPED) {
					message_traitement = "Traitement arr�ter";
					//On arr�te la boucle m�re
					break LOOP;
				}
			}
			
			try 
				{Thread.sleep(5000);} 
			catch (InterruptedException e) 
				{this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);}
		}
		//On remet la varibale � faut � la fin du traitement
		CampagneThread.RUNNING = false;
		//On repasse sur les bouttons d'acceuil
		//this.main.flipButton(0);
		this.main.flipButtonAcceuil();
		this.main.updateStatusMsg(message_traitement,MainActivity.COLOR_BLUE,false);
		this.main.setCampagneState("[TERMINE]", MainActivity.COLOR_GREEN);
		
	}
}
