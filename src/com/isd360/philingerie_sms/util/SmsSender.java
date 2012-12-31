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
	 * Envoi Un sms au destinataire donné en paramètre
	 * @param dest
	 * @return
	 */
	public static boolean SendMessage(Destinataire dest) {
		
		String message = "";
		//Par défaut le numéro de Philingerie est celui de la martinique
		String numPhilingerie = "0596755850";
		
		//Selon le numéro de magasin commence G => numéro de guadeloupe, R => numéro de Robert
		if(dest.getMagasin().substring(0, 1).equals("G"))
			numPhilingerie = "0590859156";
		if(dest.getMagasin().substring(0, 1).equals("R"))
			numPhilingerie = "0596538492";
		
		// TODO:A terme on doit récupérer le message depuis la config prédéfinies (et le parser pour les accolades)
		message = MessageFormat.format("Bonjour {0} ,\nPHILINGERIE fete ses 16 ans, venez beneficier d une PROMO exceptionnelle  de -20% a -80% avant le 31/01!\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		//message = MessageFormat.format("Cher(e) {0} , c est bientot votre anniversaire, nous vous offrons 1 bon de -25% sur 1 article au choix, valable jusqu au 31/01 \n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = formatMsg(message);
		
		// TODO: Mettre une meilleur vérification (regex)
		//if (dest.getNumero().length() == 10 && message.length() > 0) {
		if (!StringChecker.formatPhoneNumber(dest.getNumero(),dest.getMagasin().charAt(0)).equals("") && message.length() > 0) {
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
		msg = msg.replace('\'', ' ');
		
		return msg;
	}
}
