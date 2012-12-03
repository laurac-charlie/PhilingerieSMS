package com.isd360.philingerie_sms.util;

import java.io.*;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.isd360.philingerie_sms.entity.Destinataire;

public class ParserCSV {

	public ParserCSV(String fileName)throws FileNotFoundException {
		//On reconstitue le path avec le support de sauvegarde interne
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ fileName;
		File file = new File(path);
		if(file.exists())
			this.csvFile = file;
		else
			throw new FileNotFoundException("Le fichier � l'emplacement: " + path + " n'existe pas.");
	}
	
	private File csvFile = null;
	
	public File getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	public ArrayList<Destinataire> parseRecipient(){
		//TODO: Permettre � l'utilisateur de savoir si le fichier existe bien.
		ArrayList<Destinataire> list = new ArrayList<Destinataire>();
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{
			//On ouvre le fichier csv pour le lire
			fr = new FileReader(this.csvFile);
	        br = new BufferedReader(fr);
	        
	        //On initialise un csv parser pour lire le fichier
	        LabeledCSVParser lcsvp = new LabeledCSVParser( new CSVParser(br));
	        //On change le d�limiter pour correspondre au bon fichier csv
	        lcsvp.changeDelimiter(';');
	        //On charge les donn�es du fichier CSV dans une liste de destinataires
	        while(lcsvp.getLine() != null){
	        	list.add(new Destinataire(lcsvp.getValueByLabel("Civilite"),lcsvp.getValueByLabel("Prenom"),lcsvp.getValueByLabel("Nom"),lcsvp.getValueByLabel("Portable")));
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
