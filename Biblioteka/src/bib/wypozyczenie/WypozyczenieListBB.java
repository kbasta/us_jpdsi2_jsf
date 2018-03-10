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

	private String status;
	private String imie;
	private String nazwisko;
	private String login;
	private String tytul;
	private String autor;
	private String idWyp;
	
	public String getIdWyp() {
		return idWyp;
	}
	
	public void setIdWyp(String idWyp) {
		this.idWyp = idWyp;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getTytul() {
		return tytul;
	}

	public void setTytul(String tytul) {
		this.tytul = tytul;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	//Dependency injection
	// - no setter method needed in this case
	@EJB
	WypozyczenieDAO wypozyczenieDAO;
	KsiazkaDAO ksiazkaDAO;
	
	
	
	private Wypozyczenie wypozyczenie = null;
	
	public List<Wypozyczenie> getFullList(){
		return wypozyczenieDAO.getFullList();
	}

	public List<Wypozyczenie> getList(){
		List<Wypozyczenie> list = null;
		
		//1. Prepare search params
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		searchParams.put("status", status);
		searchParams.put("imie", imie);
		searchParams.put("nazwisko", nazwisko);
		searchParams.put("login", login);
		searchParams.put("autor", autor);
		searchParams.put("tytul", tytul);

		//2. Get list
		list = wypozyczenieDAO.getList(searchParams);
		
		return list;
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
