package com.isd360.philingerie_sms.view;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 
 * @author Charlie
 * @version 1.0
 * 
 */
public class PhoneState {

	/**
	 * Donne l'état de la carte SD
	 * 
	 * @return Vrai si la carte SD est disponible, faux sinon
	 */
	public static boolean getSDcardState() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Permet de savoir si un réseau est disponible
	 * @param activity Activité nécessaire pour consulter les services du téléphone
	 * @return Renvoi le réseau disponible et une chaîne vide si aucun réseau n'est disponible
	 */
	public static String getConnectivityState(Activity activity) {
		String type = "";
		ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isAvailable())
			type ="Wifi";
		else if (mobile3g.isAvailable())
			type = "Mobile 3G ";
		
		return type;
	}
}
