package com.isd360.philingerie_sms.entity;

public class Destinataire{

	public Destinataire(){}
	
	/**
	 * Constructeur de la classe destinataire avec tous les paramètres
	 * @param civilite
	 * @param prenom
	 * @param nom
	 * @param numero
	 */
	public Destinataire(String civilite, String prenom, String nom,String numero) {
		this.civilite = civilite;
		this.prenom = prenom;
		this.nom = nom;
		//On supprime les espaces dans le numéro
		//this.numero = numero.replaceAll("\\p{Zs}", "");
		this.numero = numero;
	}
	
	public Destinataire(String civilite, String prenom, String nom,String numero, String moisNaiss, String mag) {
		this.civilite = civilite;
		this.prenom = prenom;
		this.nom = nom;
		//On supprime les espaces dans le numéro
		//this.numero = numero.replaceAll("\\p{Zs}", "");
		this.numero = numero;
		this.moisNaissance = moisNaiss;
		this.magasin = mag;
	}
	
	private String civilite = "";
	private String prenom = "";
	private String nom = "";
	private String numero = "";
	private String moisNaissance = "";
	private String magasin = "";
	
	public String getCivility() {
		return civilite;
	}
	public void setCivility(String civilite) {
		this.civilite = civilite;
	}
	public String getFirstName() {
		return prenom;
	}
	public void setFirstName(String prenom) {
		this.prenom = prenom;
	}
	public String getLastName() {
		return nom;
	}
	public void setLastName(String nom) {
		this.nom = nom;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getMoisNaissance() {
		return moisNaissance;
	}

	public void setMoisNaissance(String moisNaissance) {
		this.moisNaissance = moisNaissance;
	}

	public String getMagasin() {
		return magasin;
	}

	public void setMagasin(String magasin) {
		this.magasin = magasin;
	}
}
