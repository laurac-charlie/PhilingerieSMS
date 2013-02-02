package com.isd360.philingerie_sms.util;

import java.util.regex.Pattern;

/**
 * Classe de v�rification et formatage de cha�ne de caract�res
 * @author Charlie
 *
 */
public class StringFormater {
	
	/**
	 * Reconstitue le num�ro au format internatinal si c'est possible
	 * @param phone Num�ro du destinataire
	 * @param mag Caract�re repr�sentant le magasin du destinataire
	 * @return le num�ro de t�l�phone mis au format international ou "" si la reconstitution a �t� impossible
	 */
	public static String formatPhoneNumber(String phone, char mag){
		//On retire les points et les espaces
		phone = phone.replaceAll("\\.", "");
		phone = phone.replaceAll("\\p{Zs}", "");
		//phone = phone.replaceAll("\\-", "");
		
		//On v�rifie qu'il n'y a plus que des chiffres, sinon on arr�te le traitement en renvoyant rien
		try{
			Integer.parseInt(phone);
		}catch(NumberFormatException ne){
			return "";
		}
		
		//Selon le nombre de caract�res que le num�ro de t�l�phone donn� en param�tre contient, on tente de le reconstituer
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
				//Si le num�ro est compos� de 9 chiffres et qu'il commence par 6 ou 7 on ajoute l'indicatif de france
				//un num�ro de guadeloupe ou martinique aura d�j� �t� trait� donc ne sera pas affect� ici
				if(phone.substring(0,1).equals("6") || phone.substring(0,1).equals("7")) phone = "+330" + phone;
				break;
			case 10 : 
				if(phone.substring(0, 4).equals("0690")) phone = "+59" + phone;
				if(phone.substring(0, 4).equals("0696")) phone = "+596" + phone.substring(1, phone.length());
				if(phone.substring(0, 4).equals("0694")) phone = "+594" + phone.substring(1, phone.length());
				//Si le num�ro est compos� de 10 chiffres et qu'il commence par 06 ou 07 on ajoute l'indicatif de france
				//un num�ro de guadeloupe ou martinique aura d�j� �t� trait� donc ne sera pas affect� ici
				if(phone.substring(0,2).equals("06") || phone.substring(0,2).equals("07")) phone = "+33" + phone;
				break;
			case 13 : break;
			default : phone = "";
			break;
		}
		
		//13 caract�res pour un num�ro antillais et 12 pour un num�ro de france � cause des indicatifs
		if(phone.length() != 13 && phone.length() != 12) phone = "";
		
		return phone;
	}

	/**
	 * Formate le message enlevant les caract�res accentu�s et apostrophes
	 * @param msg Message de type SMS a format� 
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
	
	/**
	 * V�rifie la taille et la validit� du message SMS
	 * @param smstext Texte du SMS
	 * @return Vrai si le message est valide, faux sinon
	 */
	public static boolean validSMStext(String smstext){
		if(smstext == null || smstext.equals("")) return false;
		if(smstext.length() > 135) return false;
		
		return true;
	}
}
