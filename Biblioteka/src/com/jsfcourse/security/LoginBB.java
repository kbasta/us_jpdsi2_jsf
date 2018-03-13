package com.jsfcourse.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import bib.dao.UzytkownikDAO;

import bib.entities.Uzytkownik;

@ManagedBean
public class LoginBB {
	private static final String PAGE_MAIN = "/pages/bib/main";
	private static final String PAGE_ADMIN_MAIN = "/pages/admin/main";
	private static final String PAGE_LOGIN = "/pages/login";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String login;
	private String pass;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@ManagedProperty("#{txtError}")
	private ResourceBundle txtError;
	
	public void setTxtError(ResourceBundle txtError) {
		this.txtError = txtError;
	}
	
	public boolean validateData() {
		boolean result = true;
		FacesContext ctx = FacesContext.getCurrentInstance();

		// check if not empty
		if (login == null || login.length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("setLogin"), null));
		}

		if (pass == null || pass.length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("setPasswd"), null));
		}

		if (ctx.getMessageList().isEmpty()) {
			result = true;
		} else {
			result = false;
		}
		return result;

	}

	public String doLogin() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Uzytkownik user = null;

		// 1. check parameters and stay if errors
		if (!validateData()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		// 2. verify login and password - get User from "database"
		user = getUserFromDatabase(login, pass);

		// 3. if bad login or password - stay with error info
		if (user == null) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("wrongLoginProp"), null));
			return PAGE_STAY_AT_THE_SAME;
		}

		// 4. if login ok - save User object in session
		HttpSession session = (HttpSession) ctx.getExternalContext()
				.getSession(true);
		session.setAttribute("user", user);

		// and enter the system
		if (user.getRola().equals("user")) 
			return PAGE_MAIN;
		else if (user.getRola().equals("admin"))
			return PAGE_ADMIN_MAIN;
		return PAGE_STAY_AT_THE_SAME;
	}

	public Uzytkownik getUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		return (Uzytkownik) session.getAttribute("user");
	}
	
	public String doLogout(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		//Invalidate session
		// - all objects within session will be destroyed
		// - new session will be created (with new ID)
		session.invalidate();
		return PAGE_LOGIN;
	}
	
	
	@EJB
	UzytkownikDAO uzytkownikDAO;
	// simulate finding user in database
	private Uzytkownik getUserFromDatabase(String login, String pass) {
		Uzytkownik u = null;
		List<Uzytkownik> list = null;
		
		Map<String,Object> loginParams = new HashMap<String, Object>();
		loginParams.put("login", login);
		loginParams.put("passwd", pass);
		
		list = uzytkownikDAO.getUserForLogin(loginParams);
		if(list.size() == 1) {
			Uzytkownik identyfikator = list.get(0);
			
			u = new Uzytkownik(login, pass);
			u.setImie(identyfikator.getImie());
			u.setNazwisko(identyfikator.getNazwisko());
			u.setId(identyfikator.getId());
			u.setRola(identyfikator.getRola());
			u.setKara(identyfikator.getKara());
		}	
		return u;
	}
	
}
