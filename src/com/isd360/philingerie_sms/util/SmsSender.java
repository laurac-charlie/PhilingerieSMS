package com.isd360.philingerie_sms.util;

import java.util.ArrayList;

import android.telephony.SmsManager;
import com.isd360.philingerie_sms.entity.Destinataire;

public class SmsSender {

	private ArrayList<Destinataire> listDestinataires = new ArrayList<Destinataire>();// new List<Destinataire>();
	
	public ArrayList<Destinataire> getListDestinataires() {
		return listDestinataires;
	}

	public void setListDestinataires(ArrayList<Destinataire> listDestinataires) {
		this.listDestinataires = listDestinataires;
	}
	
	public static boolean SendMessage(String numero, String message){
		
		if(numero.length()>= 4 && message.length() > 0){
			//Gr�ce � l'objet de gestion de SMS (SmsManager) que l'on r�cup�re gr�ce � la m�thode static getDefault()
			//On envoit le SMS � l'aide de la m�thode sendTextMessage
			SmsManager.getDefault().sendTextMessage(numero, null, message, null, null);
			return true;
		}
		else
			return false;
	}
}
