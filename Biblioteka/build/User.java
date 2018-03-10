package com.jsfcourse.security;

import java.util.HashMap;
import java.util.HashSet;

public class User {
	
	private String login;
	private String haslo;
	private String imie;
	private String nazwisko;
	private int id;

	private String rola;
	private int kara;
	//private HashSet<String> rola = new HashSet<String>();

	public User(String login, String haslo) {
		this.login = login;
		this.haslo = haslo;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getHaslo() {
		return haslo;
	}

	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	public String getImie() {
		return imie;
	}

	public void setImie(String imie) {
		this.imie = imie;
	}

	public String getNazwisko() {
		return nazwisko;
	}

	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getRola() {
		return rola;
	}

	public void setRola(String rola) {
		this.rola = rola;
	}

	public int getKara() {
		return kara;
	}

	public void setKara(int kara) {
		this.kara = kara;
	}
	
	
}
