package com.isd360.philingerie_sms.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Environment;
import android.util.Log;

public class FTPManager {

	private FTPManager() {}

	/**
	 * Télécharge le fichier csv depuis le ftp
	 * @param context
	 * @param filename
	 * @return
	 */
	public static boolean DownloadCSVfile( String filename) {
		FTPClient ftp = new FTPClient();
		ftp = new FTPClient();
		
		String workDir = "";
		BufferedInputStream buffIn = null;
		
		try {
			
			ftp.connect("94.23.35.183",21); // Using port no=21
			if(FTPReply.isPositiveCompletion(ftp.getReplyCode()))
			{
				boolean status = ftp.login("tablette", "tablette");
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				
				String localpath = /*Environment.getExternalStoragePublicDirectory(null) + "/" +*/ filename;
				workDir = ftp.printWorkingDirectory();
				
				buffIn = new BufferedInputStream(new FileInputStream("smsg05.csv"));
				ftp.enterLocalPassiveMode();
				ftp.storeFile(workDir + filename, buffIn);
				buffIn.close();
				ftp.logout();
				ftp.disconnect();
			}
		/*
		FileTransferClient ftp = new FileTransferClient();
		Log.i("test", "test");
		try 
		{
			ftp.setRemoteHost("94.23.35.183"); // smsg05.csv
			ftp.setUserName("tablette");
			ftp.setPassword("tablette");
			Log.i("test", "here");
			ftp.connect();
			String[] files = ftp.directoryNameList();
			Log.i("test", "where");
			
			ftp.downloadFile(Environment.getExternalStorageDirectory() + "/" + filename, "ftp://94.23.35.183/" + filename);
			//ftp.downloadURLFile(filename, "ftp://94.23.35.183/" + filename);
			
			File file = new File(Environment.getExternalStorageDirectory() + "/" + filename);
			if(file.exists())
				return true;
			else 
				return false;
				*/
			return true;
		} catch (Exception e) {
			Log.e("Erreur FTP", "La connexion a échoué");
			return false;
		}
	}
}
