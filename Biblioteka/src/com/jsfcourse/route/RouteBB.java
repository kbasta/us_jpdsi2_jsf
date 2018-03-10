package com.jsfcourse.route;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import bib.dao.UzytkownikDAO;
import bib.entities.Uzytkownik;

@ManagedBean
public class RouteBB {
	private static final String PAGE_POSITION = "/pages/bib/position";
	private static final String PAGE_SEARCH = "/pages/bib/search";
	private static final String PAGE_RULES = "/pages/bib/rules";
	private static final String PAGE_LOGIN = "/pages/login";
	private static final String PAGE_ADMIN_USER = "/pages/admin/users/userList";
	private static final String PAGE_ADMIN_BOOK = "/pages/admin/books/bookList";
	
	@EJB
	UzytkownikDAO uzytkownikDAO;
	
	public List<Uzytkownik> getFullList(){
		return uzytkownikDAO.getFullList();
	}
	
	public List<Uzytkownik> getList(){
		List<Uzytkownik> list = null;
		list = uzytkownikDAO.getFullList();
		return list;
	}
	
	public String position(){
		return PAGE_POSITION;
	}
	
	public String search(){
		return PAGE_SEARCH;
	}
	
	public String rules(){
		return PAGE_RULES;
	}

	public String login() {
		return PAGE_LOGIN;
	}
	
	public String adminUsers() {
		return PAGE_ADMIN_USER;
	} 

	public String adminBooks() {
		return PAGE_ADMIN_BOOK;
	}
}
