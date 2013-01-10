package com.isd360.philingerie_sms.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

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
	 * Lit le fichier SMS donn� en param�tre
	 * @param smsfile Fichier sms pr�sent en local
	 * @return Retourne le texte contneu dans le fichier sms
	 * @throws FileNotFoundException Si le fichier n'existe pas, une exception est lev�
	 * @throws IOException Si une erreur quelquonque se produit pendant la lecture du fichier, une exception est lev�
	 */
	public static String readSMSfile(String smsfile) throws FileNotFoundException,IOException {
		String smsText = "";
		FileReader fr = new FileReader(MainController.PHIL_DIRECTORY + smsfile);
		BufferedReader br = new BufferedReader(fr);
		String ligne;
		
		//Lecture ligne par ligne en rempla�ant les sauts de ligne par des espaces
		while ((ligne=br.readLine())!=null)
			smsText +=ligne +" ";
		
		//on ferme les readers
		br.close(); 
		fr.close();
		
		return smsText;
	}
	
	/**
	 * Envoi Un sms au destinataire donn� en param�tre
	 * @param dest Destinataire � qui envoy� le message
	 * @param smsText texte du SMS
	 * @return Renvoi vrai si l'envoi a �t� correctement effectu�
	 */
	public static boolean SendMessage(Destinataire dest,String smsText) {
		
		String message = "";
		//Par d�faut le num�ro de Philingerie est celui de la martinique
		String numPhilingerie = "0596755850";
		
		//Selon le num�ro de magasin commence G => num�ro de guadeloupe, R => num�ro de Robert
		if(dest.getMagasin().substring(0, 1).equals("G"))
			numPhilingerie = "0590859156";
		if(dest.getMagasin().substring(0, 1).equals("R"))
			numPhilingerie = "0596538492";
		
		// TODO:A terme on doit r�cup�rer le message depuis la config pr�d�finies (et le parser pour les accolades) (ne doit pas d�passer 159 caract�res)
		//message = MessageFormat.format("Bonjour {0},\nPHILINGERIE fete ses 16 ans, venez beneficier d une PROMO exceptionnelle  de -20% a -80% avant le 31/01!\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = MessageFormat.format("Cher(e) {0}, c est bientot votre anniversaire, nous vous offrons 1 bon de -25% sur 1 article au choix, valable < 31/01\n{1} www.philingerie.com",dest.getFirstName(),numPhilingerie);
		message = StringFormater.formatMsg(message);
		
		//Test Multipart
		//ArrayList<String> parts = new ArrayList<String>();
		//if(message.length() > 150)
		//{
		//	parts.add(message.substring(0, 150));
		//	parts.add(message.substring(151,message.length()));
		//}
		
		String phone = StringFormater.formatPhoneNumber(dest.getNumero(),dest.getMagasin().charAt(0));
		
		//if (dest.getNumero().length() == 10 && message.length() > 0) {
		//if (!phone.equals("") && message.length() > 0) {
		if (!phone.equals("") && !message.equals("")){
			try {
				// Envoie du SMS gr�ce � SMSmanager
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
