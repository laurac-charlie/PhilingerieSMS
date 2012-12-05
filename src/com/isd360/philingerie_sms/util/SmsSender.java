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
	 * Envoi d'un sms en pr�cisanr le destinaraire
	 * 
	 * @param numero
	 * @param message
	 * @return
	 */
	public static boolean SendMessage(Destinataire dest) {
		// TODO:A terme on doit r�cup�r� le message depuis la config
		// pr�d�finies (et le parser pour les accolades)
		// String message =
		// MessageFormat.format("Bonjour {0} {1} {2},\nNous vous annon�ons une r�duction exceptionnel sur les strings l�opard noirs jusqu'au 31 D�cembre.\nPhilingerie",dest.getCivility(),dest.getLastName(),dest.getFirstName());
		String message = MessageFormat.format("Bonjour {0} ,\nPHILINGERIE fete ses 16 ans, tout le magasin est en PROMO de -16% a -80% jusqu au 15/12 venez vite !\n 0596755850 www.philingerie.com",dest.getFirstName());
		message = formatMsg(message);
		
		// TODO: Mettre une meilleur v�rification (regex)
		if (dest.getNumero().length() == 10 && message.length() > 0) {
			try {
				// Envoie du SMS gr�ce � SMSmanager
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
		msg = msg.replace('�', 'a');
		msg = msg.replace('�', 'a');
		msg = msg.replace('�', 'a');
		msg = msg.replace('�', 'e');
		msg = msg.replace('�', 'e');
		msg = msg.replace('�', 'e');
		msg = msg.replace('�', 'e');
		msg = msg.replace('�', 'o');
		msg = msg.replace('�', 'o');
		msg = msg.replace('�', 'u');
		msg = msg.replace('�', 'u');
		msg = msg.replace('�', 'u');
		msg = msg.replace('�', 'i');
		msg = msg.replace('�', 'i');
		
		return msg;
	}
}
