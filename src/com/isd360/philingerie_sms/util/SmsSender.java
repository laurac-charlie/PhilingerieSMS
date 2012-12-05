package com.isd360.philingerie_sms.util;

import java.text.MessageFormat;
import java.util.ArrayList;

import android.telephony.SmsManager;

import com.isd360.philingerie_sms.entity.Destinataire;

public class SmsSender {

	private ArrayList<Destinataire> listDestinataires = new ArrayList<Destinataire>();

	public ArrayList<Destinataire> getListDestinataires() {
		return listDestinataires;
	}

	public void setListDestinataires(ArrayList<Destinataire> listDestinataires) {
		this.listDestinataires = listDestinataires;
	}

	/**
	 * Envoi d'un sms en précisanr le destinaraire
	 * 
	 * @param numero
	 * @param message
	 * @return
	 */
	public static boolean SendMessage(Destinataire dest) {
		// TODO:A terme on doit récupéré le message depuis la config
		// prédéfinies (et le parser pour les accolades)
		// String message =
		// MessageFormat.format("Bonjour {0} {1} {2},\nNous vous annonçons une réduction exceptionnel sur les strings léopard noirs jusqu'au 31 Décembre.\nPhilingerie",dest.getCivility(),dest.getLastName(),dest.getFirstName());
		String message = MessageFormat.format("Bonjour {0} ,\nPHILINGERIE fete ses 16 ans, tout le magasin est en PROMO de -16% a -80% jusqu au 15/12 venez vite !\n 0596755850 www.philingerie.com",dest.getFirstName());
		message = formatMsg(message);
		
		// TODO: Mettre une meilleur vérification (regex)
		if (dest.getNumero().length() == 10 && message.length() > 0) {
			try {
				// Envoie du SMS grâce à SMSmanager
				SmsManager.getDefault().sendTextMessage(dest.getNumero(),null, message, null, null);
				//SmsManager.getDefault().sendDataMessage( dest.getNumero(), null,new Short("16008"), message.getBytes(), null, null);
				
			} catch (Exception iae) {
				return false;
			}
			return true;
		} else
			return false;
	}
	
	public static String formatMsg(String msg){
		msg = msg.replace('à', 'a');
		msg = msg.replace('â', 'a');
		msg = msg.replace('ä', 'a');
		msg = msg.replace('é', 'e');
		msg = msg.replace('è', 'e');
		msg = msg.replace('ê', 'e');
		msg = msg.replace('ë', 'e');
		msg = msg.replace('ô', 'o');
		msg = msg.replace('ö', 'o');
		msg = msg.replace('û', 'u');
		msg = msg.replace('ü', 'u');
		msg = msg.replace('ù', 'u');
		msg = msg.replace('î', 'i');
		msg = msg.replace('ï', 'i');
		
		return msg;
	}
}
