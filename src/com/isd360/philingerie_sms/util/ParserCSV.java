package com.isd360.philingerie_sms.util;

import java.io.*;
import java.util.ArrayList;

import android.util.Log;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.isd360.philingerie_sms.entity.Destinataire;

public class ParserCSV {

	public ParserCSV(String filePath){
		this.csvFile = new File("/mnt/sdcard/" + filePath);
		Log.i("info", this.csvFile.getAbsolutePath());
	}
	
	private File csvFile = null;
	
	public File getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	public ArrayList<Destinataire> parseRecipient(){
		//TODO: Permettre à l'utilisateur de savoir si le fichier existe bien.
		ArrayList<Destinataire> list = new ArrayList<Destinataire>();
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{
			//On lit ouvre le fichier csv
			fr = new FileReader(this.csvFile);
	        br = new BufferedReader(fr);
	        
	        //On initialise un csv parser pour lire le fichier
	        LabeledCSVParser lcsvp = new LabeledCSVParser( new CSVParser(br));
	        Log.i("info", "init");
	        //On charge les données du fichier CSV dans une liste de destinataires
	        while(lcsvp.getLine() != null){
	        	list.add(new Destinataire(lcsvp.getValueByLabel("Civilite"),lcsvp.getValueByLabel("Prenom"),lcsvp.getValueByLabel("Nom"),lcsvp.getValueByLabel("Portable")));
	        }
	        Log.i("info", "fin");
	        br.close();
	        fr.close();
		}
		catch(Exception fne)
		{
			Log.e("Erreur lors de la lecture du ficheir csv.", fne.getMessage());
		}
		
		return list;
	}
	
	public ArrayList<String> parseFile()
	{
		ArrayList<String> list = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;
		String ligne = "";
		try
		{
			//On lit ouvre le fichier csv
			fr = new FileReader(this.csvFile);
	        br = new BufferedReader(fr);
	        
	        while((ligne=br.readLine())!=null)
	        {
	        	list.add(ligne);
	        }
	        br.close();
	        fr.close();
		}
		catch(Exception fne)
		{
			Log.e("Erreur lors de la lecture du ficheir csv.", fne.getMessage());
		}
		
		return list;
	}
	
	
}
