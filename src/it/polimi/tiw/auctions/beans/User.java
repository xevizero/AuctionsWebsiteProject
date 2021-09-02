package it.polimi.tiw.auctions.beans;

public class User {

	private int id;
	private String username;
	private String name;
	private String surname;
	private String email;
	
	public User() {
		
	}
	
	public User(int id) {
		this.id = id;
	}
	
	
	public User(int id, String username, String name, String surname, String email) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.email = email;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
