package com.isd360.philingerie_sms.util;

import java.util.regex.Pattern;

/**
 * Classe de vérification et formatage de chaîne de caractères
 * @author Charlie
 *
 */
public class StringFormater {
	
	/**
	 * Reconstitue le numéro au format internatinal si c'est possible
	 * @param phone Numéro du destinataire
	 * @param mag Caractère représentant le magasin du destinataire
	 * @return le numéro de téléphone mis au format international ou "" si la reconstitution a été impossible
	 */
	public static String formatPhoneNumber(String phone, char mag){
		//On retire les points et les espaces
		phone = phone.replaceAll("\\.", "");
		phone = phone.replaceAll("\\p{Zs}", "");
		//phone = phone.replaceAll("\\-", "");
		
		//On vérifie qu'il n'y a plus que des chiffres, sinon on arrête le traitement en renvoyant rien
		try{
			Integer.parseInt(phone);
		}catch(NumberFormatException ne){
			return "";
		}
		
		//Selon le nombre de caractères que le numéro de téléphone donné en paramètre contient, on tente de le reconstituer
		switch(phone.length()){
			case 6 : 
				//phone = (mag == 'G') ? "+590690" + phone : "+596696" + phone;
				if(mag == 'G') phone = "+590690" + phone;
				if(mag == 'M' && mag == 'R') phone = "+596696" + phone;
				break;
			case 9 : 
				if(phone.substring(0, 3).equals("690")) phone = "+590" + phone;
				if(phone.substring(0, 3).equals("696")) phone = "+596" + phone;
				if(phone.substring(0, 3).equals("694")) phone = "+594" + phone;
				//Si le numéro est composé de 9 chiffres et qu'il commence par 6 ou 7 on ajoute l'indicatif de france
				//un numéro de guadeloupe ou martinique aura déjà été traité donc ne sera pas affecté ici
				if(phone.substring(0,1).equals("6") || phone.substring(0,1).equals("7")) phone = "+330" + phone;
				break;
			case 10 : 
				if(phone.substring(0, 4).equals("0690")) phone = "+59" + phone;
				if(phone.substring(0, 4).equals("0696")) phone = "+596" + phone.substring(1, phone.length());
				if(phone.substring(0, 4).equals("0694")) phone = "+594" + phone.substring(1, phone.length());
				//Si le numéro est composé de 10 chiffres et qu'il commence par 06 ou 07 on ajoute l'indicatif de france
				//un numéro de guadeloupe ou martinique aura déjà été traité donc ne sera pas affecté ici
				if(phone.substring(0,2).equals("06") || phone.substring(0,2).equals("07")) phone = "+33" + phone;
				break;
			case 13 : break;
			default : phone = "";
			break;
		}
		
		//13 caractères pour un numéro antillais et 12 pour un numéro de france à cause des indicatifs
		if(phone.length() != 13 && phone.length() != 12) phone = "";
		
		return phone;
	}

	/**
	 * Formate le message enlevant les caractères accentués et apostrophes
	 * @param msg Message de type SMS a formaté 
	 * @return Le message formaté
	 */
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
		msg = msg.replaceAll("\'", " ");
		
		return msg;
	}
	
	/**
	 * Vérifie si une chaîne est une adresse IP valide
	 * @param ipAdress Adresse ip à vérifier
	 * @return Vrai si l'ip est valide, faux dans le cas contraire
	 */
	public static boolean validIP(String ipAdress)
	{
		if (ipAdress.equals("")) return false;
		
		//Regex pour adresse IP
		String pattern = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		if(Pattern.compile(pattern).matcher(ipAdress).matches())
			return true;
		else 
			return false;
	}
	
	/**
	 * Vérifie la taille et la validité du message SMS
	 * @param smstext Texte du SMS
	 * @return Vrai si le message est valide, faux sinon
	 */
	public static boolean validSMStext(String smstext){
		if(smstext == null || smstext.equals("")) return false;
		if(smstext.length() > 135) return false;
		
		return true;
	}
}
