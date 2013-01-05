package com.isd360.philingerie_sms.util;
/**
 * 
 * @author Charlie
 *
 */
public class StringChecker {
	
	/**
	 * 
	 * @param phone Numéro du destinataire
	 * @param mag Caractère représentant le magasin du destinataire
	 * @return
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
			default : phone = "";
			break;
		}
		
		if(phone.length() != 13) phone = "";
		
		return phone;
	}

}
