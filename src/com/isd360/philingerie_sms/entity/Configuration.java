package com.isd360.philingerie_sms.entity;
/**
 * @version 1
 * @author Charlie
 *
 */
public class Configuration {

	public Configuration(){ }
	
	public Configuration(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	private int id;
	private String name = "";
	private String description = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
