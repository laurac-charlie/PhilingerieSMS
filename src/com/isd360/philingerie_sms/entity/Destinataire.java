package com.isd360.philingerie_sms.entity;

public class Destinataire {

	public Destinataire(){}
	
	/**
	 * 
	 * @param civilite
	 * @param prenom
	 * @param nom
	 * @param numero
	 */
	public Destinataire(String civilite, String prenom, String nom,String numero) {
		super();
		this.civilite = civilite;
		this.prenom = prenom;
		this.nom = nom;
		this.numero = numero;
	}
	
	private String civilite = "";
	private String prenom = "";
	private String nom = "";
	private String numero = "";
	
	
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
}
