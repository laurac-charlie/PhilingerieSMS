package com.isd360.philingerie_sms.util;

import java.util.regex.Pattern;

/**
 * Classe de v�rification et formatage de ch�ine de caract�res
 * @author Charlie
 *
 */
public class StringChecker {
	
	/**
	 * 
	 * @param phone Num�ro du destinataire
	 * @param mag Caract�re repr�sentant le magasin du destinataire
	 * @return le num�ro de t�l�phone mis au format international ou "" si la reconstitution a �t� impossible
	 */
	public static String formatPhoneNumber(String phone, char mag){
		//On retire les points et les espaces
		phone = phone.replaceAll("\\.", "");
		//phone = phone.replaceAll("\\s", "");
		phone = phone.replaceAll("\\p{Zs}", "");
		
		//On v�rifie qu'il n'y a plus que des chiffres, sinon on arr�te le traitement en renvoyant rien
		try{
			Integer.parseInt(phone);
		}catch(NumberFormatException ne){
			return "";
		}
		
		//Selon le nombre de caract�res que le num�ro de t�l�phone donn� en param�tre contient, on tente de le reconstituer
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
	 * @param msg Message de type SMS a format� (enlev� caract�res accentu�s et apostrophes)
	 * @return Le message format�
	 */
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
		msg = msg.replaceAll("\'", " ");
		
		return msg;
	}
	
	/**
	 * V�rifie si une cha�ne est une adresse IP valide
	 * @param ipAdress Adresse ip � v�rifier
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
