package com.isd360.philingerie_sms.controller;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
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
	 * @param main Activit� principale de l'application qui sera mis � jour 
	 */
	public MainController(Activity main){
		this.main = (main instanceof MainActivity) ? (MainActivity)main : null;
	}
	
	/**
	 * Charge les diff�rents statuts dans l'interface graphique
	 */
	public void loadPrerequisites(){
		//Acc�s r�seau
		String network = PhoneState.getConnectivityState(this.main);
		this.main.setNetworkState(!network.equals(""), network);
		
		//Pr�sence carte SD
		this.main.setSDcardState(PhoneState.getSDcardState());
		
		//Etat connection ftp
		String errorFtp = "";
		boolean ftpOK = false;
		//On initialise les param�tres � partir des pr�f�rences pr�configur�es
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.main);
		FTPManager ftpManager = new FTPManager(prefs.getString("param_serveur_ip", ""),
				prefs.getString("param_serveur_login", ""),
				prefs.getString("param_serveur_pass", ""));
	
		if(prefs.getString("param_serveur_ip", "").equals("") || prefs.getString("param_serveur_login", "").equals("") || prefs.getString("param_serveur_pass", "").equals(""))
			errorFtp = "Param�tres ftp manquant(s)";
		else
		{
			//Si un type de r�seau existe
			if(!network.equals(""))
			{
				ftpOK = ftpManager.tryFtpConnection();
				if(!ftpOK) errorFtp = "Echec de connexion";
			}
			else
				errorFtp = "Pas de r�seau";
		}
		
		this.main.setFtpState(ftpOK, errorFtp);
		
		//Date et heure du dernier envoi
		this.main.setDateEnvoi(prefs.getString("date_envoi", ""));
	}
	
	/**
	 * Pr�aparation et v�rification des param�tres n�cessaires au lancement de la campagne
	 * @return Vrai si la campagne a �t� correctement pr�par�e
	 */
	public boolean prepareCampaign(){
		if(this.main != null && !CampagneThread.RUNNING)
		{
			//On initialise les param�tres � partir des pr�f�rences pr�configur�es
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
			
			//On teste la validit� des param�tres
			this.main.updateStatusMsg("V�rification des param�tres de l'application...", MainActivity.COLOR_BLUE,false);
			if(!ftpManager.tryFtpConnection())
			{
				connectionOk = false;
				this.main.updateStatusMsg("La connexion au serveur ftp a �chou�, veuillez v�rifier les param�tres de connection et la 3g.", MainActivity.COLOR_RED, true);
			}
			
			if(this.csvfile.equals(""))
			{
				csvOk = false;
				this.main.updateStatusMsg("Le nom du fichier de contact csv n'a pas �t� configur�.", MainActivity.COLOR_RED, true);
			}
			
			if(this.smsfile.equals(""))
			{
				smsOK = false;
				this.main.updateStatusMsg("Le nom du fichier du texte sms n'a pas �t� configur�.", MainActivity.COLOR_RED, true);
			}
			
			//Si les noms des fichiers csv et du texte sms sont renseign�s et que les fichiers sont pr�sent ou que la connection au ftp est disponible, on continue le traitement		
			if(csvOk && smsOK && (((new File(MainController.PHIL_DIRECTORY + this.csvfile)).exists() && (new File(MainController.PHIL_DIRECTORY + smsfile)).exists()) || connectionOk))
			{
				//Si la connection fonctionne, on va tent� de t�l�charger le fichier csv
				if(connectionOk)
				{
					this.main.updateStatusMsg("T�l�chargement des fichiers : " + this.csvfile + " et " + this.smsfile,MainActivity.COLOR_BLUE,false);
					try {
						//T�lchagement du fichier de contact
						ftpManager.downloadCSVfile(this.csvfile);
						//T�lchagement du fichier du texte sms
						ftpManager.downloadSMSfile(this.smsfile);
					} catch (Exception e) {
						this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);
						//On bascule sur l'�cran d'acceuil
						this.main.flipLayout(0);
					}
				}
				
				try 
				{
					//On teste la valid� du sms contenu dans le fichier
					this.smsText = SmsSender.readSMSfile(this.smsfile);
					if(!StringFormater.validSMStext(this.smsText))
						throw new Exception("Le texte du fichier sms n'est pas correct, veuillez v�rifier sa longueur (135 max) et sa validit�.");
				} 
				catch (Exception e) {
					//En cas d'exception, on s'assure que la variable running soit � false
					CampagneThread.RUNNING= false;
					this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);
					//On bascule sur l'�cran d'acceuil
					this.main.flipLayout(0);
					return false;
				}
				
				return true;
			}
			else
			{
				//On bascule sur l'�cran d'acceuil
				this.main.flipLayout(0);
				return false;
			}
		}
		else
		{
			this.main.updateStatusMsg("Un probl�me � l'initialisation ne permet pas de continuer (activit� null).", MainActivity.COLOR_RED, true);
			return false;
		}
	}

	/**
	 * Lancement de la campagne apr�s avoir v�rifi� les param�tres avec la fonction prepareCampaign()
	 * @param csvfile Nom du fichier csv de contact
	 * @param smsfile Nom du fichier texte contenant le mod�le du sms
	 */
	public void launchCampaign() {
		ParserCSV psr = null;
		ArrayList<Destinataire> listDest = null;
		
		if(!this.csvfile.equals("") && !this.smsfile.equals("") && !this.smsText.equals(""))
		{
			try 
			{
				this.main.updateStatusMsg("Chargement du fichier CSV",MainActivity.COLOR_BLUE,false);
					
				//On r�cup�re la liste des destinataires � partir du fichier CSV
				psr = new ParserCSV(this.csvfile);
				listDest = psr.parseRecipient(';');
				
				//On lance le Thread d'envoi des messages
				CampagneThread ct = new CampagneThread(this.main,listDest,this.smsText);
				ct.start();
			} 
			catch (Exception e) {
				//En cas d'exception, on s'assure que la variable running soit � false
				CampagneThread.RUNNING= false;
				this.main.updateStatusMsg(e.getMessage(),MainActivity.COLOR_RED,true);
				this.main.setCampagneState("[ERREUR]", MainActivity.COLOR_RED);
				//On r�active le button d'envoi
				//this.main.setButtonEnable(true);
				this.main.flipLayout(0);
			}
		}
	}
}
