package com.isd360.philingerie_sms.util;

import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Environment;

public class FTPManager {

	private FTPManager() {}

	/**
	 * Télécharge le fichier csv depuis le ftp
	 * @param filename
	 * @param cnt
	 * @return
	 */
	public static void DownloadCSVfile(String filename) throws Exception{
		// On instancie les variables ainsi que le client FTP
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		String workDir = "", localpath = "";
		
		String host = "94.23.35.183";
		String login = "tablette";
		String pass = "tablette";
		
		// On donne l'emplacement local qu'aura le ficheir CSV
		localpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ filename;
		
		// On se conencte au serveur en utilisant le bon pour pour le ftp, par défaut 21
		ftp.connect(host, 21);
		
		// On vérifie que le serveur est bien disponible
		if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			// On s'identifie sur le ftp
			if (!ftp.login(login, pass))
				throw new Exception("L'identification au serveur ftp : " + host +  " a échoué.");
				
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			
			// On récupère le répertoire racine du ftp
			workDir = ftp.printWorkingDirectory();

			ftp.enterLocalPassiveMode();
			// On télécharge le fichier en utilisant l'outpuStream du fichier local pour y écrire
			
			fos = new FileOutputStream(localpath,false);
	        if(!ftp.retrieveFile(workDir + filename, fos))
	        	throw new Exception ("Le téléchargement du fichier ftp://" + host + "/" + filename + " a échoué. Veuillez vérifier les paramètres et l'existance du fichier.");
	        
	        //On ferme tout
	        fos.close();
			ftp.logout();
			ftp.disconnect();
		}
		else
			throw new Exception("Le serveur ftp : " + host +" n'existe pas ou n'est pas disponible.");

	}
}
