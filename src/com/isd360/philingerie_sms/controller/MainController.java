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
import com.isd360.philingerie_sms.util.PhoneState;
import com.isd360.philingerie_sms.util.SmsSender;
import com.isd360.philingerie_sms.util.StringFormater;
import com.isd360.philingerie_sms.view.MainActivity;

/**
 * Classe de traitement pour le lancement de la campagne
 * @author Charlie
 * @version 1.0
 *
 */
public class MainController {

	public static final String PHIL_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	private MainActivity main = null;
	private String smsfile = "";
	private String smsText = "";
	private String csvfile = "";
	
	public String getSmsText() {
		return smsText;
	}

	public void setSmstext(String smsText) {
		this.smsText = smsText;
	}
	
	/**
	 * On instancie un controleur princpal pour l'application
	 * @param main Activité principale de l'application qui sera mis à jour 
	 */
	public MainController(Activity main){
		this.main = (main instanceof MainActivity) ? (MainActivity)main : null;
	}
	
	/**
	 * Charge les différents statuts dans l'interface graphique
	 */
	public void loadPrerequisites(){
		//Accés réseau
		String network = PhoneState.getConnectivityState(this.main);
		this.main.setNetworkState(!network.equals(""), network);
		
		//Présence carte SD
		this.main.setSDcardState(PhoneState.getSDcardState());
		
		//Etat connection ftp
		String errorFtp = "";
		boolean ftpOK = false;
		//On initialise les paramètres à partir des préférences préconfigurées
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.main);
		FTPManager ftpManager = new FTPManager(prefs.getString("param_serveur_ip", ""),
				prefs.getString("param_serveur_login", ""),
				prefs.getString("param_serveur_pass", ""));
	
		if(prefs.getString("param_serveur_ip", "").equals("") || prefs.getString("param_serveur_login", "").equals("") || prefs.getString("param_serveur_pass", "").equals(""))
			errorFtp = "Paramètres ftp manquant(s)";
		else
		{
			//Si un type de réseau existe
			if(!network.equals(""))
			{
				ftpOK = ftpManager.tryFtpConnection();
				if(!ftpOK) errorFtp = "Echec de connexion";
			}
			else
			{
				errorFtp = "Pas de réseau";
			}
		}
		
		this.main.setFtpState(ftpOK, errorFtp);
	}
	
	/**
	 * Préaparation et vérification des paramètres nécessaires au lancement de la campagne
	 * @return Vrai si la campagne a été correctement préparée
	 */
	public boolean prepareCampaign(){
		if(this.main != null && !CampagneThread.RUNNING)
		{
			//On désactive le button d'envoi
			this.main.setButtonEnable(false);
			//On initialise les paramètres à partir des préférences préconfigurées
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.main);
			boolean connectionOk = true;
			String ftp_host = (StringFormater.validIP(prefs.getString("param_serveur_ip", ""))) ? prefs.getString("param_serveur_ip", "") : "";
			String ftp_login = prefs.getString("param_serveur_login", "");
			String ftp_pass = prefs.getString("param_serveur_pass", "");
			
			boolean csvOk = true;
			this.csvfile = prefs.getString("param_fichier_csv", "");
			
			boolean smsOK = true;
			this.smsfile = prefs.getString("param_fichier_texte_sms", "");
			
			FTPManager ftpManager = new FTPManager(ftp_host, ftp_login, ftp_pass);
			
			//On teste la validité des paramètres
			this.main.updateStatusMsg("Vérification des paramètres de l'application...",Color.BLUE,false);
			if(!ftpManager.tryFtpConnection())
			{
				connectionOk = false;
				this.main.updateStatusMsg("La connexion au serveur ftp a échoué, veuillez vérifier les paramètres de connection et la 3g.", Color.RED, true);
			}
			
			if(this.csvfile.equals(""))
			{
				csvOk = false;
				this.main.updateStatusMsg("Le nom du fichier de contact csv n'a pas été configuré.", Color.RED, true);
			}
			
			if(this.smsfile.equals(""))
			{
				smsOK = false;
				this.main.updateStatusMsg("Le nom du fichier du texte sms n'a pas été configuré.", Color.RED, true);
			}
			
			//Si les noms des fichiers csv et du texte sms sont renseignés et que les fichiers sont présent ou que la connection au ftp est disponible, on continue le traitement		
			if(csvOk && smsOK && (((new File(MainController.PHIL_DIRECTORY + this.csvfile)).exists() && (new File(MainController.PHIL_DIRECTORY + smsfile)).exists()) || connectionOk))
			{
				//Si la connection fonctionne, on va tenté de télécharger le fichier csv
				if(connectionOk)
				{
					this.main.updateStatusMsg("Téléchargement des fichiers : " + this.csvfile + " et " + this.smsfile,Color.BLUE,false);
					try {
						//Télchagement du fichier de contact
						ftpManager.downloadCSVfile(this.csvfile);
						//Télchagement du fichier du texte sms
						ftpManager.downloadSMSfile(this.smsfile);
					} catch (Exception e) {
						this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
						//On réactive le button d'envoi
						this.main.setButtonEnable(true);
					}
				}
				
				try 
				{
					//On teste la validé du sms contenu dans le fichier
					this.smsText = SmsSender.readSMSfile(this.smsfile);
					if(!StringFormater.validSMStext(this.smsText))
						throw new Exception("Le texte du fichier sms n'est pas correct, veuillez vérifier sa longueur (135 max) et sa validité.");
				} 
				catch (Exception e) {
					//En cas d'exception, on s'assure que la variable running soit à false
					CampagneThread.RUNNING= false;
					this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
					//On réactive le button d'envoi
					this.main.setButtonEnable(true);
					return false;
				}
				
				//this.launchCampaign();
				return true;
			}
			else
			{
				//On réactive le button d'envoi
				this.main.setButtonEnable(true);
				return false;
			}
		}
		else
		{
			this.main.updateStatusMsg("Un problème à l'initialisation ne permet pas de continuer (activité null).", Color.RED, true);
			return false;
		}
			
	}

	/**
	 * Lancement de la campagne après avoir vérifié les paramètres avec la fonction prepareCampaign()
	 * @param csvfile Nom du fichier csv de contact
	 * @param smsfile Nom du fichier texte contenant le modèle du sms
	 */
	public void launchCampaign() {
		ParserCSV psr = null;
		ArrayList<Destinataire> listDest = null;
		
		if(!this.csvfile.equals("") && !this.smsfile.equals("") && !this.smsText.equals(""))
		{
			try 
			{
				//TODO: Demander à l'utilisateur une validation du template
				this.main.updateStatusMsg("Chargement du fichier CSV",Color.BLUE,false);
					
				//On récupère la liste des destinataires à partir du fichier CSV
				psr = new ParserCSV(this.csvfile);
				listDest = psr.parseRecipient(';');
				
				//On lance le Thread d'envoi des messages
				CampagneThread ct = new CampagneThread(this.main,listDest,this.smsText);
				ct.start();
			} 
			catch (Exception e) {
				//En cas d'exception, on s'assure que la variable running soit à false
				CampagneThread.RUNNING= false;
				this.main.updateStatusMsg(e.getMessage(),Color.RED,true);
				//On réactive le button d'envoi
				this.main.setButtonEnable(true);
			}
		}
	}
}
