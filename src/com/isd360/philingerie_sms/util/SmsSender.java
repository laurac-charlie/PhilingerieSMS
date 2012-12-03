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
		
		//A terme on doit r�cup�r� le message depuis la liste des messages pr�d�finies (et le parser pour les accolades)
		String message = MessageFormat.format("Bonjour {0} {1} {2},\nNous vous annon�ons une r�duction exceptionnel sur les strings l�opard noirs jusqu'au 31 D�cembre.\nPhilingerie",dest.getCivility(),dest.getLastName(),dest.getFirstName());
		
		if(dest.getNumero().length() >= 10 && message.length() > 0){
			//Gr�ce � l'objet de gestion de SMS (SmsManager) que l'on r�cup�re gr�ce � la m�thode static getDefault()
			//On envoit le SMS � l'aide de la m�thode sendTextMessage
			SmsManager.getDefault().sendTextMessage(dest.getNumero(), null, message, null, null);
			return true;
		}
		else
			return false;
	}
}
