package com.isd360.philingerie_sms.util;

import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Environment;

/**
 * Classe de gestion de la connexion avec le serveur FTP
 * @author Charlie
 * 
 */
public class FTPManager {

	/**
	 * Initialise un objet de gestion du FTP
	 * @param host Adress IP du serveur a test�
	 * @param login utilisateur pour la connection
	 * @param pass mot de passe de l'utilisateur
	 */
	public FTPManager(String host, String login, String pass) {
		this.host = host;
		this.login = login;;
		this.pass = pass;
	}

	private String host = "";
	private String login = "";
	private String pass = "";
	
	public String getHost() {
		return host;
	}

	public String getLogin() {
		return login;
	}

	public String getPass() {
		return pass;
	}

	/**
	 * Test la connection au serveur ftp
	 * @return Vrai si la connection a r�ussi, faux dans le cas contraire
	 */
	public boolean tryFtpConnection(){
		
		if(this.host.equals("") || this.login.equals("") || this.pass.equals(""))
			return false;
		
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		try 
		{
			ftp.connect(this.host, 21);
			if(FTPReply.isPositiveCompletion(ftp.getReplyCode()) == false)
				throw new Exception("La connection au serveur ftp a �chou�.");
			return true;
		} 
		catch (Exception e) 
		{	return false;	}
	}
	
	/**
	 * T�l�charge le fichier csv depuis le ftp
	 * @param csvfile fichier de contact du serveur ftp
	 * @throws Exception Provoque une erreur si la connexion ou le t�l�chargement du fichier a �chou�
	 */
	public void downloadCSVfile(String csvfile) throws Exception{
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		String workDir = "", localpath = "";
		
		// On donne l'emplacement local qu'aura le ficheir CSV
		localpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ csvfile;
		
		// On se connecte au serveur en utilisant le bon pour pour le ftp, par d�faut 21
		ftp.connect(this.host, 21);
		
		// On v�rifie que le serveur est bien disponible
		if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			// On s'identifie sur le ftp
			if (!ftp.login(this.login, this.pass))
				throw new Exception("L'identification au serveur ftp : " + this.host +  " a �chou�. Veuillez v�rifier les param�tres de connexion");
				
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			// On r�cup�re le r�pertoire racine du ftp
			workDir = ftp.printWorkingDirectory();

			ftp.enterLocalPassiveMode();
			// On t�l�charge le fichier en utilisant l'outpuStream du fichier local pour y �crire
			
			fos = new FileOutputStream(localpath,false);
	        if(!ftp.retrieveFile(workDir + csvfile, fos))
	        	throw new Exception ("Le t�l�chargement du fichier ftp://" + this.host + "/" + csvfile + " a �chou�. Veuillez v�rifier les param�tres et l'existance du fichier de contact csv.");
	        
	        //On ferme tout
	        fos.close();
			ftp.logout();
			ftp.disconnect();
		}
		else
			throw new Exception("Le serveur ftp : " + host +" n'existe pas ou n'est pas disponible.");

	}
	
	/**
	 * T�l�charge le fichier du texte sms depuis le ftp
	 * @param smsfile fichier du texte sms du serveur ftp
	 * @throws Exception Provoque une erreur si la connexion ou le t�l�chargement du fichier a �chou�
	 */
	public void downloadSMSfile(String smsfile) throws Exception{
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		String workDir = "", localpath = "";
		
		// On donne l'emplacement local qu'aura le ficheir CSV
		localpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ smsfile;
		
		// On se connecte au serveur en utilisant le bon pour pour le ftp, par d�faut 21
		ftp.connect(this.host, 21);
		
		// On v�rifie que le serveur est bien disponible
		if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			// On s'identifie sur le ftp
			if (!ftp.login(this.login, this.pass))
				throw new Exception("L'identification au serveur ftp : " + this.host +  " a �chou�. Veuillez v�rifier les param�tres de connexion");
				
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			// On r�cup�re le r�pertoire racine du ftp
			workDir = ftp.printWorkingDirectory();

			ftp.enterLocalPassiveMode();
			// On t�l�charge le fichier en utilisant l'outpuStream du fichier local pour y �crire
			
			fos = new FileOutputStream(localpath,false);
	        if(!ftp.retrieveFile(workDir + smsfile, fos))
	        	throw new Exception ("Le t�l�chargement du fichier ftp://" + this.host + "/" + smsfile + " a �chou�. Veuillez v�rifier les param�tres et l'existance du fichier de sms.");
	        
	        //On ferme tout
	        fos.close();
			ftp.logout();
			ftp.disconnect();
		}
		else
			throw new Exception("Le serveur ftp : " + host +" n'existe pas ou n'est pas disponible.");

	}
}
