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
	 * Donne l'�tat de la carte SD
	 * 
	 * @return Vrai si la carte SD est disponible, faux sinon
	 */
	public static boolean getSDcardState() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Permet de savoir si un r�seau est disponible
	 * @param activity Activit� n�cessaire pour consulter les services du t�l�phone
	 * @return Renvoi le r�seau disponible et une cha�ne vide si aucun r�seau n'est disponible
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
