package com.isd360.philingerie_sms.util;

import java.text.MessageFormat;
import java.util.ArrayList;

import android.telephony.SmsManager;

import com.isd360.philingerie_sms.model.Destinataire;

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
		
		// TODO:A terme on doit récupérer le message depuis la config prédéfinies (et le parser pour les accolades) (ne doit pas dépasser 159 caractères)
		//message = MessageFormat.format("Bonjour {0},\nPHILINGERIE fete ses 16 ans, venez beneficier d une PROMO exceptionnelle  de -20% a -80% avant le 31/01!\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = MessageFormat.format("Cher(e) {0}, c est bientot votre anniversaire, nous vous offrons 1 bon de -25% sur 1 article au choix, valable < 31/01\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = StringChecker.formatMsg(message);
		
		//Test Multipart
		//ArrayList<String> parts = new ArrayList<String>();
		//if(message.length() > 150)
		//{
		//	parts.add(message.substring(0, 150));
		//	parts.add(message.substring(151,message.length()));
		//}
		
		String phone = StringChecker.formatPhoneNumber(dest.getNumero(),dest.getMagasin().charAt(0));
		
		//if (dest.getNumero().length() == 10 && message.length() > 0) {
		//if (!phone.equals("") && message.length() > 0) {
		if (!phone.equals("") && !message.equals("")){
			try {
				// Envoie du SMS grâce à SMSmanager
				SmsManager.getDefault().sendTextMessage(phone,null, message, null, null);
				//SmsManager.getDefault().sendMultipartTextMessage(phone, null, parts, null, null);
			} catch (Exception iae) {
				return false;
			}
			return true;
		} else{
			return false;
		}
	}
}
