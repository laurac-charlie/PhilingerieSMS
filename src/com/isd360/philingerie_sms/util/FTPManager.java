package com.isd360.philingerie_sms.util;

import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.isd360.philingerie_sms.view.MainActivity;

import android.os.Environment;

public class FTPManager {

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
	public void setHost(String host) {
		this.host = host;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}


	public boolean tryFtpConnection(MainActivity activity){
		
		if(this.host.equals("") || this.login.equals("") || this.pass.equals(""))
			return false;
		
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		try 
		{
			ftp.connect(this.host, 21);
			if(FTPReply.isPositiveCompletion(ftp.getReplyCode()) == false)
				throw new Exception("La connection au serveur ftp a échoué.");
			return true;
		} 
		catch (Exception e) 
		{	return false;	}
	}
	
	/**
	 * Télécharge le fichier csv depuis le ftp
	 * @param csvfile fichier de contact du serveur ftp
	 * @throws Exception Porovoque une erreur si la connexion ou le téléchargement du fichier a échoué
	 */
	public void DownloadCSVfile(String csvfile) throws Exception{
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		String workDir = "", localpath = "";
		
		//String host = "94.23.35.183";
		//String login = "tablette";
		//String pass = "tablette";
		
		// On donne l'emplacement local qu'aura le ficheir CSV
		localpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ csvfile;
		
		// On se connecte au serveur en utilisant le bon pour pour le ftp, par défaut 21
		ftp.connect(this.host, 21);
		
		// On vérifie que le serveur est bien disponible
		if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			// On s'identifie sur le ftp
			if (!ftp.login(this.login, this.pass))
				throw new Exception("L'identification au serveur ftp : " + this.host +  " a échoué. Veuillez vérifier les paramètres de connexion");
				
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			// On récupère le répertoire racine du ftp
			workDir = ftp.printWorkingDirectory();

			ftp.enterLocalPassiveMode();
			// On télécharge le fichier en utilisant l'outpuStream du fichier local pour y écrire
			
			fos = new FileOutputStream(localpath,false);
	        if(!ftp.retrieveFile(workDir + csvfile, fos))
	        	throw new Exception ("Le téléchargement du fichier ftp://" + this.host + "/" + csvfile + " a échoué. Veuillez vérifier les paramètres et l'existance du fichier.");
	        
	        //On ferme tout
	        fos.close();
			ftp.logout();
			ftp.disconnect();
		}
		else
			throw new Exception("Le serveur ftp : " + host +" n'existe pas ou n'est pas disponible.");

	}
}
