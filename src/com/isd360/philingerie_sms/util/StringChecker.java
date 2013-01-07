package com.isd360.philingerie_sms.util;

import java.util.regex.Pattern;

/**
 * Classe de vérification et formatage de châine de caractères
 * @author Charlie
 *
 */
public class StringChecker {
	
	/**
	 * 
	 * @param phone Numéro du destinataire
	 * @param mag Caractère représentant le magasin du destinataire
	 * @return le numéro de téléphone mis au format international ou "" si la reconstitution a été impossible
	 */
	public static String formatPhoneNumber(String phone, char mag){
		//On retire les points et les espaces
		phone = phone.replaceAll("\\.", "");
		//phone = phone.replaceAll("\\s", "");
		phone = phone.replaceAll("\\p{Zs}", "");
		
		//On vérifie qu'il n'y a plus que des chiffres, sinon on arrête le traitement en renvoyant rien
		try{
			Integer.parseInt(phone);
		}catch(NumberFormatException ne){
			return "";
		}
		
		//Selon le nombre de caractères que le numéro de téléphone donné en paramètre contient, on tente de le reconstituer
		switch(phone.length()){
			case 6 : 
				phone = (mag == 'G') ? "+590690" + phone : "+596696" + phone;
				break;
			case 9 : 
				if(phone.substring(0, 3).equals("690")) phone = "+590" + phone;
				if(phone.substring(0, 3).equals("696")) phone = "+596" + phone;
				break;
			case 10 : 
				if(phone.substring(0, 4).equals("0690")) phone = "+59" + phone;
				if(phone.substring(0, 4).equals("0696")) phone = "+596" + phone.substring(1, phone.length());
				break;
			case 13 : break;
			default : phone = "";
			break;
		}
		
		if(phone.length() != 13) phone = "";
		
		return phone;
	}

	/**
	 * 
	 * @param msg Message de type SMS a formaté (enlevé caractères accentués et apostrophes)
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
	
	
	public static boolean validSMStext(String smstext){
		//TODO: Implement
		return true;
	}
}
