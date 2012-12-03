package com.isd360.philingerie_sms.util;

import java.text.MessageFormat;
import java.util.ArrayList;

import android.telephony.SmsManager;
import com.isd360.philingerie_sms.entity.Destinataire;

public class SmsSender {

	private ArrayList<Destinataire> listDestinataires = new ArrayList<Destinataire>();// new List<Destinataire>();
	
	public ArrayList<Destinataire> getListDestinataires() {
		return listDestinataires;
	}

	public void setListDestinataires(ArrayList<Destinataire> listDestinataires) {
		this.listDestinataires = listDestinataires;
	}
	
	/**
	 * 
	 * @param numero
	 * @param message
	 * @return
	 */
	public static boolean SendMessage(Destinataire dest){//SendMessage(String numero, String message){
		
		//A terme on doit récupéré le message depuis la liste des messages prédéfinies (et le parser pour les accolades)
		String message = MessageFormat.format("Bonjour {0} {1} {2},\nNous vous annonçons une réduction exceptionnel sur les strings léopard noirs jusqu'au 31 Décembre.\nPhilingerie",dest.getCivility(),dest.getLastName(),dest.getFirstName());
		
		if(dest.getNumero().length() >= 10 && message.length() > 0){
			//Grâce à l'objet de gestion de SMS (SmsManager) que l'on récupère grâce à la méthode static getDefault()
			//On envoit le SMS à l'aide de la méthode sendTextMessage
			SmsManager.getDefault().sendTextMessage(dest.getNumero(), null, message, null, null);
			return true;
		}
		else
			return false;
	}
}
