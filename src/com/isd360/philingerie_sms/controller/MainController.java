package com.isd360.philingerie_sms.controller;

import java.io.File;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.StringChecker;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * Classe de traitement pour le lancement de la campagne
 * @author Charlie
 *
 */
public class MainController {

	private MainActivity main = null;
	
	public MainController(MainActivity main){
		this.main = main;
	}
	
	public void launchCampaign(){
		//On initialise les paramètres à partir des préférences préconfigurées
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.main);
		boolean connectionOk = true;
		String ftp_host = (StringChecker.validIP(prefs.getString("param_serveur_ip", ""))) ? prefs.getString("param_serveur_ip", "") : "";
		String ftp_login = prefs.getString("param_serveur_login", "");
		String ftp_pass = prefs.getString("param_serveur_pass", "");
		
		boolean csvOk = true;
		String csvfile = prefs.getString("param_fichier_csv", "");
		boolean smsOK = true;
		String smsfile = prefs.getString("param_fichier_texte_sms", "");
		
		FTPManager ftpManager = new FTPManager(ftp_host, ftp_login, ftp_pass);
		
		//On teste la validité des paramètres
		if(!ftpManager.tryFtpConnection())
		{
			connectionOk = false;
			this.main.updateStatusMsg("La connexion au serveur ftp a échoué, veuillez les vérifier paramètres de connection et que la 3g est activé.", Color.RED, true);
		}
		
		if(csvfile.equals(""))
		{
			csvOk = false;
			this.main.updateStatusMsg("Le nom du fichier de contact csv n'a pas été configuré.", Color.RED, true);
		}
		
		if(smsfile.equals(""))
		{
			smsOK = false;
			this.main.updateStatusMsg("Le nom du fichier du texte sms n'a pas été configuré.", Color.RED, true);
		}
		
		//Si les noms des fichiers csv et du texte sms sont renseignés et que les fichiers sont présent ou que la connection au ftp est disponible, on continue le traitement
		//if((csvOk && (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + csvfile)).exists()) || (csvOk && connectionOk))
		if(csvOk && smsOK && (((new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + csvfile)).exists() && (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + smsfile)).exists()) || connectionOk))
		{
			//Si la connection fonctionne, on va tenté de télécharger le fichier csv
			if(connectionOk)
			{
				try {
					this.main.updateStatusMsg("Téléchargement du fichier : " + csvfile,Color.BLUE,false);
					//Télchagement du fichier de contact
					ftpManager.downloadCSVfile(csvfile);
					//Télchagement du fichier du texte sms
					ftpManager.downloadSMSfile(smsfile);
				} catch (Exception e) {
					this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
				}
			}
			
			//TODO: Vérifier la validité du texte du message récupéré depuis le fichier
			
			
			//On lance le Thread d'envoi des messages
			CampagneThread mt = new CampagneThread(this.main,csvfile);
			mt.start();
		}
	}
}
