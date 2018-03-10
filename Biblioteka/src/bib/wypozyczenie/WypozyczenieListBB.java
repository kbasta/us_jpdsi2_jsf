package bib.wypozyczenie;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import bib.dao.KsiazkaDAO;
import bib.dao.WypozyczenieDAO;
import bib.entities.Ksiazka;
import bib.entities.Uzytkownik;
import bib.entities.Wypozyczenie;


import com.jsfcourse.security.*;

@ManagedBean
@ViewScoped
public class WypozyczenieListBB implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String PAGE_PERSON_EDIT = "search?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
			.getExternalContext().getSession(true);
	public Uzytkownik u = (Uzytkownik) session.getAttribute("user");
	private int userid = u.getId();
	private int bookid;
	
	Date dataWypoz = new Date();
	//String dataOddan = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	
	public int getBookid() {
		return bookid;
	}
	
	public void setBookId(int bookid) {
		this.bookid = bookid;
	}
	
	
	//Dependency injection
	// - no setter method needed in this case
	@EJB
	WypozyczenieDAO wypozyczenieDAO;
	KsiazkaDAO ksiazkaDAO;
	
	private int idWyp;
	
	public int getIdWyp() {
		return idWyp;
	}
	
	public void setIdWyp(int idWyp) {
		this.idWyp = idWyp;
	}
	
	private Wypozyczenie wypozyczenie = null;
	
	public List<Wypozyczenie> getFullList(){
		return wypozyczenieDAO.getFullList();
	}

	public List<Wypozyczenie> getListForUser(){
		List<Wypozyczenie> list = null;
		
		//1. Prepare search params
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		searchParams.put("userid", userid);
		
		//2. Get list
		list = wypozyczenieDAO.getListForUser(searchParams);
		
		return list;
	}
	
	public String newWypozyczenie(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		Wypozyczenie wypozyczenie = new Wypozyczenie();
		session.setAttribute("wypozyczenie", wypozyczenie);
		return PAGE_PERSON_EDIT;
	}
	
	public String newWypozyczenie(Ksiazka ksiazka){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("ksiazka", ksiazka);
		Wypozyczenie wypozyczenie = new Wypozyczenie();
		session.setAttribute("wypozyczenie", wypozyczenie);
	/*	wypozyczenie.setUzytkownik(uzytkownik);
		wypozyczenie.setKsiazka(ksiazka);
		wypozyczenie.setStatus("oczekiwany");
		wypozyczenie.setDataOd(dataWypoz);
		wypozyczenie.setDataOd(dataWypoz);
		wypozyczenieDAO.create(wypozyczenie);*/
		saveData();
		return PAGE_STAY_AT_THE_SAME;
	}

	public String editWypozyczenie(Wypozyczenie wypozyczenie){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("wypozyczenie", wypozyczenie);
		return PAGE_PERSON_EDIT;
	}

	public String deleteWypozyczenie(Wypozyczenie wypozyczenie){
		wypozyczenieDAO.remove(wypozyczenie);
		return PAGE_STAY_AT_THE_SAME;
	}
	
	
	private int id;
	
	public int getIdBook() {
		return id;
	}
	
	public void setIdBook(int id) {
		this.id = id;
	}
	
	private Ksiazka ksiazka = new Ksiazka();
	private Uzytkownik uzytkownik = new Uzytkownik();
	
	@PostConstruct
	public void postConstruct() {
		// A. load person if passed through session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		ksiazka = (Ksiazka) session.getAttribute("ksiazka");
		uzytkownik = (Uzytkownik) session.getAttribute("user");

		// cleaning: attribute received => delete it from session
		/*if (ksiazka != null) {
			session.removeAttribute("ksiazka");
		}*/

		// if loaded record is to be edited then copy data to properties
		if (ksiazka != null && ksiazka.getId() != 0) {
			setIdBook(ksiazka.getId());
	//		String dataWypoz = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	//		String dataOddan = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
	}
	
	private boolean validate() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;
		
		// if no errors
		if (ctx.getMessageList().isEmpty()) {
			wypozyczenie.setUzytkownik(uzytkownik);
			wypozyczenie.setKsiazka(ksiazka);
			wypozyczenie.setStatus("oczekiwany");
			wypozyczenie.setDataOd(dataWypoz);
			wypozyczenie.setDataOd(dataWypoz);
			
		}

		return result;
	}
	
	public String saveData() {


		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
				wypozyczenieDAO.create(wypozyczenie);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("wystąpił błąd podczas zapisu"));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_STAY_AT_THE_SAME;
	}
}
