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
		//On initialise les param�tres � partir des pr�f�rences pr�configur�es
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
		
		//On teste la validit� des param�tres
		if(!ftpManager.tryFtpConnection())
		{
			connectionOk = false;
			this.main.updateStatusMsg("La connexion au serveur ftp a �chou�, veuillez les v�rifier param�tres de connection et que la 3g est activ�.", Color.RED, true);
		}
		
		if(csvfile.equals(""))
		{
			csvOk = false;
			this.main.updateStatusMsg("Le nom du fichier de contact csv n'a pas �t� configur�.", Color.RED, true);
		}
		
		if(smsfile.equals(""))
		{
			smsOK = false;
			this.main.updateStatusMsg("Le nom du fichier du texte sms n'a pas �t� configur�.", Color.RED, true);
		}
		
		//Si les noms des fichiers csv et du texte sms sont renseign�s et que les fichiers sont pr�sent ou que la connection au ftp est disponible, on continue le traitement
		//if((csvOk && (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + csvfile)).exists()) || (csvOk && connectionOk))
		if(csvOk && smsOK && (((new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + csvfile)).exists() && (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + smsfile)).exists()) || connectionOk))
		{
			//Si la connection fonctionne, on va tent� de t�l�charger le fichier csv
			if(connectionOk)
			{
				try {
					this.main.updateStatusMsg("T�l�chargement du fichier : " + csvfile,Color.BLUE,false);
					//T�lchagement du fichier de contact
					ftpManager.downloadCSVfile(csvfile);
					//T�lchagement du fichier du texte sms
					ftpManager.downloadSMSfile(smsfile);
				} catch (Exception e) {
					this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
				}
			}
			
			//TODO: V�rifier la validit� du texte du message r�cup�r� depuis le fichier
			
			
			//On lance le Thread d'envoi des messages
			CampagneThread mt = new CampagneThread(this.main,csvfile);
			mt.start();
		}
	}
}
