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
	 * Envoi Un sms au destinataire donn� en param�tre
	 * @param dest
	 * @return
	 */
	public static boolean SendMessage(Destinataire dest) {
		
		String message = "";
		//Par d�faut le num�ro de Philingerie est celui de la martinique
		String numPhilingerie = "0596755850";
		
		//Selon le num�ro de magasin commence G => num�ro de guadeloupe, R => num�ro de Robert
		if(dest.getMagasin().substring(0, 1).equals("G"))
			numPhilingerie = "0590859156";
		if(dest.getMagasin().substring(0, 1).equals("R"))
			numPhilingerie = "0596538492";
		
		// TODO:A terme on doit r�cup�rer le message depuis la config pr�d�finies (et le parser pour les accolades)
		message = MessageFormat.format("Bonjour {0} ,\nPHILINGERIE fete ses 16 ans, venez beneficier d une PROMO exceptionnelle  de -20% a -80% avant le 31/01!\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		//message = MessageFormat.format("Cher(e) {0} , c est bientot votre anniversaire, nous vous offrons 1 bon de -25% sur 1 article au choix, valable jusqu au 31/01 \n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = formatMsg(message);
		
		// TODO: Mettre une meilleur v�rification (regex)
		//if (dest.getNumero().length() == 10 && message.length() > 0) {
		if (!StringChecker.formatPhoneNumber(dest.getNumero(),dest.getMagasin().charAt(0)).equals("") && message.length() > 0) {
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
		msg = msg.replace('\'', ' ');
		
		return msg;
	}
}
