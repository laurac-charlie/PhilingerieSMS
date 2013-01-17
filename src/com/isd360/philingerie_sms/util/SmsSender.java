package com.isd360.philingerie_sms.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.telephony.SmsManager;

import com.isd360.philingerie_sms.controller.MainController;
import com.isd360.philingerie_sms.model.Destinataire;

/**
 * Classe de gestion de l'envoi des 
 * @author Charlie
 *
 */
public class SmsSender {

	/**
	 * Lit le fichier SMS donné en paramètre
	 * @param smsfile Fichier sms présent en local
	 * @return Retourne le texte contneu dans le fichier sms
	 * @throws FileNotFoundException Si le fichier n'existe pas, une exception est levé
	 * @throws IOException Si une erreur quelquonque se produit pendant la lecture du fichier, une exception est levé
	 */
	public static String readSMSfile(String smsfile) throws FileNotFoundException,IOException {
		String smsText = "";
		//FileReader fr = new FileReader(MainController.PHIL_DIRECTORY + smsfile);
		InputStream is = new FileInputStream(MainController.PHIL_DIRECTORY + smsfile);
		InputStreamReader fr = new InputStreamReader(is,"ISO-8859-1");
		BufferedReader br = new BufferedReader(fr);
		String ligne = "";
		
		//Lecture ligne par ligne en remplaçant les sauts de ligne par des espaces
		while ((ligne=br.readLine())!=null)
			smsText +=ligne +" ";
		
		//on ferme les readers
		br.close(); 
		fr.close();
		
		return smsText;
	}
	
	/**
	 * Envoi Un sms au destinataire donné en paramètre
	 * @param dest Destinataire à qui envoyé le message
	 * @param smsText texte du SMS
	 * @return Renvoi vrai si l'envoi a été correctement effectué
	 */
	public static boolean SendMessage(Destinataire dest,String smsText) {
		
		String message = smsText;
		//Par défaut le numéro de Philingerie est celui de la martinique
		String numPhilingerie = "0596755850";
		
		//Selon le numéro de magasin commence G => numéro de guadeloupe, R => numéro de Robert
		if(dest.getMagasin().substring(0, 1).equals("G"))
			numPhilingerie = "0590859156";
		if(dest.getMagasin().substring(0, 1).equals("R"))
			numPhilingerie = "0596538492";
		
		//message = MessageFormat.format("Bonjour {0},\nPHILINGERIE fete ses 16 ans, venez beneficier d une PROMO exceptionnelle  de -20% a -80% avant le 31/01!\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		//message = MessageFormat.format("Cher(e) {0}, c est bientot votre anniversaire, nous vous offrons 1 bon de -25% sur 1 article au choix, valable < 31/01\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = message.replaceAll("#1", dest.getLastName());
		message = message.replaceAll("#2", dest.getFirstName());
		message = message.replaceAll("#3", dest.getCivility());
		//Pas certain
		message = message.replaceAll("#4", numPhilingerie);
		message = StringFormater.formatMsg(message);
		
		String phone = StringFormater.formatPhoneNumber(dest.getNumero(),dest.getMagasin().charAt(0));
		
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
