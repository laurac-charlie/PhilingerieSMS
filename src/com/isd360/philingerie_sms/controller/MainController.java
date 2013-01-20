package com.isd360.philingerie_sms.controller;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.isd360.philingerie_sms.model.Destinataire;
import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.util.StringFormater;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * Classe de traitement pour le lancement de la campagne
 * @author Charlie
 *
 */
public class MainController {

	public static final String PHIL_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	private MainActivity main = null;
	
	/**
	 * On instancie un controleur princpal pour l'application
	 * @param main Activit� principale de l'application qui sera mis � jour 
	 */
	public MainController(Activity main){
		this.main = (main instanceof MainActivity) ? (MainActivity)main : null;
	}
	
	/**
	 * Proc�dure principale lan�ant les traitements de l'application
	 */
	public void launchCampaign(){
		if(this.main != null && !CampagneThread.RUNNING)
		{
			//On d�sactive le button d'envoi
			this.main.setButtonEnable(false);
			//On initialise les param�tres � partir des pr�f�rences pr�configur�es
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.main);
			boolean connectionOk = true;
			String ftp_host = (StringFormater.validIP(prefs.getString("param_serveur_ip", ""))) ? prefs.getString("param_serveur_ip", "") : "";
			String ftp_login = prefs.getString("param_serveur_login", "");
			String ftp_pass = prefs.getString("param_serveur_pass", "");
			
			boolean csvOk = true;
			String csvfile = prefs.getString("param_fichier_csv", "");
			ParserCSV psr = null;
			ArrayList<Destinataire> listDest = null;
			boolean smsOK = true;
			String smsfile = prefs.getString("param_fichier_texte_sms", "");
			String smsText = "";
			
			FTPManager ftpManager = new FTPManager(ftp_host, ftp_login, ftp_pass);
			
			//On teste la validit� des param�tres
			this.main.updateStatusMsg("V�rification des param�tres de l'application...",Color.BLUE,false);
			if(!ftpManager.tryFtpConnection())
			{
				connectionOk = false;
				this.main.updateStatusMsg("La connexion au serveur ftp a �chou�, veuillez v�rifier les param�tres de connection et la 3g.", Color.RED, true);
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
			if(csvOk && smsOK && (((new File(MainController.PHIL_DIRECTORY + csvfile)).exists() && (new File(MainController.PHIL_DIRECTORY + smsfile)).exists()) || connectionOk))
			{
				//Si la connection fonctionne, on va tent� de t�l�charger le fichier csv
				if(connectionOk)
				{
					this.main.updateStatusMsg("T�l�chargement des fichiers : " + csvfile + " et " + smsfile,Color.BLUE,false);
					try {
						//T�lchagement du fichier de contact
						ftpManager.downloadCSVfile(csvfile);
						//T�lchagement du fichier du texte sms
						ftpManager.downloadSMSfile(smsfile);
					} catch (Exception e) {
						this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
						//On r�active le button d'envoi
						this.main.setButtonEnable(true);
					}
				}
				
				try 
				{
					//On teste la valid� du sms contenu dans le fichier
					smsText = SmsSender.readSMSfile(smsfile);
					if(!StringFormater.validSMStext(smsText))
						throw new Exception("Le texte du fichier sms n'est pas correct, veuillez v�rifier sa longueur (135 max) et sa validit�.");
					
					//TODO: Demander � l'utilisateur une validation du template
					
					
					this.main.updateStatusMsg("Chargement du fichier CSV",Color.BLUE,false);
						
					//On r�cup�re la liste des destinataires � partir du fichier CSV
					psr = new ParserCSV(csvfile);
					listDest = psr.parseRecipient(';');
					
					//On lance le Thread d'envoi des messages
					CampagneThread ct = new CampagneThread(this.main,listDest,smsText);
					ct.start();
				} 
				catch (Exception e) {
					//En cas d'exception, on s'assure que la variable running soit � false
					CampagneThread.RUNNING= false;
					this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
					//On r�active le button d'envoi
					this.main.setButtonEnable(true);
				}
			}
			else
			{
				//On r�active le button d'envoi
				this.main.setButtonEnable(true);
			}
		}
		else
			this.main.updateStatusMsg("Un probl�me � l'initialisation ne permet pas de continuer (activit� null).", Color.RED, true);
			
	}
}
