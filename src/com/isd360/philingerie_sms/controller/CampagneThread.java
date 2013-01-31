package com.isd360.philingerie_sms.controller;

import java.util.ArrayList;

import com.isd360.philingerie_sms.model.Destinataire;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * Thread d'envoi des SMS à la liste de destinaires
 * @author Charlie
 *
 */
public class CampagneThread extends Thread{
	
	private MainActivity main = null;
	private ArrayList<Destinataire> listDest = null;
	private String smsText = "";
	
	/**
	 * Pour que l'information soit disponible partout dans l'application, on va utiliser la varible running qui sera mis à jour
	 * manuellement pendant la vie du Thread (et en cas de plantage) plûtot que la fonction getState qui nécessiterait
	 * d'avoir accès à l'objet.
	 */
	public static boolean RUNNING = false;
	
	/**
	 * Initialise le thread d'envoi des SMS 
	 * @param main Activité de la page priincipal à mettre à jour
	 * @param listDest Liste des destinataires à qui envoyé les sms
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
		//On met la variale running à vrai pour dire que le thread est actif
		CampagneThread.RUNNING = true;
		
		//On initialise les variables qui seront en permanence mise à jour
		String logMsg = "";
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
			
			//On met à jour la liste de log et le statut
			this.main.addMessage(logMsg);
			this.main.updateStatusCount(listDest.size(),countAll,countOk,countKo);
			
			try 
				{Thread.sleep(5000);} 
			catch (InterruptedException e) 
				{this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);}
		}
		//On remet la varibale à faut à la fin du traitement
		CampagneThread.RUNNING = false;
		//On repasse sur les bouttons d'acceuil
		this.main.flipButton(0);
		this.main.updateStatusMsg("Traitement terminé",MainActivity.COLOR_BLUE,false);
		this.main.setCampagneState("[TERMINE]", MainActivity.COLOR_GREEN);
		
	}
}
