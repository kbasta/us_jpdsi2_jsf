package bib.uzytkownik;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import bib.dao.UzytkownikDAO;
import bib.entities.Uzytkownik;

@ManagedBean
public class UzytkownikListBB implements Serializable  {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_PERSON_EDIT = "/pages/rej";
	private static final String PAGE_EDIT_USER = "/pages/admin/users/userEdit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String nazwisko;
	private String imie;
	private String login;
	
		
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	//Dependency injection
	// - no setter method needed in this case
	@EJB
	UzytkownikDAO uzytkownikDAO;
	
	public List<Uzytkownik> getFullList(){
		return uzytkownikDAO.getFullList();
	}

	public List<Uzytkownik> getList(){
		List<Uzytkownik> list = null;
		
		//1. Prepare search params
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		if (imie != null && imie.length() > 0){
			searchParams.put("imie", imie);
		}
		
		if (nazwisko != null && nazwisko.length() > 0){
			searchParams.put("nazwisko", nazwisko);
		}
		
		if (login != null && login.length() > 0){
			searchParams.put("login", login);
		}
		
		//2. Get list
		list = uzytkownikDAO.getList(searchParams);
		
		return list;
	}
	
	public String newUzytkownik(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		Uzytkownik uzytkownik = new Uzytkownik();
		session.setAttribute("uzytkownik", uzytkownik);
		return PAGE_PERSON_EDIT;
	}

	public String editUzytkownik(Uzytkownik uzytkownik){

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("uzytkownik", uzytkownik);	
		return PAGE_EDIT_USER;
	}
	

	public String deleteUzytkownik(Uzytkownik uzytkownik){
		uzytkownikDAO.remove(uzytkownik);
		return PAGE_STAY_AT_THE_SAME;
	}
}
