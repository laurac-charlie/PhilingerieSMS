package com.isd360.philingerie_sms.util;

import java.io.*;
import java.util.ArrayList;

import android.os.Environment;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.isd360.philingerie_sms.entity.Destinataire;

/**
 * Classe de Parasage des fichier CSV
 * 
 * @author Charlie
 * @version 1.0
 */
public class ParserCSV {

	/**
	 * Crée un objet de traitement de fichier CSV
	 * 
	 * @param fileName
	 *            nom du fichier
	 * @throws FileNotFoundException
	 *             Si on ne trouve pas le fichier une exception est levé
	 */
	public ParserCSV(String fileName) throws FileNotFoundException {
		// On reconstitue le path avec le support de sauvegarde interne
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + fileName;
		File file = new File(path);
		if (file.exists())
			this.csvFile = file;
		else
			throw new FileNotFoundException("Le fichier à l'emplacement: "
					+ path + " n'a pas été trouvé. Vérifier qu'il existe.");
	}

	private File csvFile = null;

	public File getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	/**
	 * Crée à partir du fichier csv de l'objet une liste de destinataires
	 * 
	 * @param carac
	 *            Delimiteur de champ dans le fichier CSV
	 * @return liste des destinataires créées à partir du fichier csv
	 */
	public ArrayList<Destinataire> parseRecipient(char carac) throws IOException {
		ArrayList<Destinataire> list = new ArrayList<Destinataire>();
		FileReader fr = null;
		BufferedReader br = null;

		// On ouvre le fichier csv pour le lire
		fr = new FileReader(this.csvFile);
		br = new BufferedReader(fr);

		// On initialise un csv parser pour lire le fichier
		LabeledCSVParser lcsvp = new LabeledCSVParser(new CSVParser(br));
		
		// On change le délimiter pour correspondre au bon fichier csv
		lcsvp.changeDelimiter(carac);
		// On charge les données du fichier CSV dans une liste de destinataires
		while (lcsvp.getLine() != null) {
			list.add(new Destinataire(lcsvp.getValueByLabel("Civilite"), lcsvp
					.getValueByLabel("Prenom"), lcsvp.getValueByLabel("Nom"),
					lcsvp.getValueByLabel("Portable"), lcsvp
							.getValueByLabel("MoisNaissance"), lcsvp
							.getValueByLabel("MagasinCreation")));
		}
		// On ferme les readers
		br.close();
		fr.close();

		return list;
	}

}
