package bib.wypozyczenie;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.sql.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
@SessionScoped
public class WypozyczenieListBB implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String PAGE_MY_BORROWS = "position?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	public String strona = "1";
	
	public String getStrona() {
		return strona;
	}

	public void setStrona(String strona) {
		this.strona = strona;
	}

	@ManagedProperty("#{txtError}")
	private ResourceBundle txtError;
	
	public void setTxtError(ResourceBundle txtError) {
		this.txtError = txtError;
	}
	
	HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
			.getExternalContext().getSession(true);
	public Uzytkownik u = (Uzytkownik) session.getAttribute("user");
	private int userid = u.getId();
	private int bookid;
	
	Date dataWypoz = Date.valueOf(LocalDate.now());
	Date dataOdd = Date.valueOf(LocalDate.now().plusDays(30));
	
	public int getBookid() {
		return bookid;
	}
	
	public void setBookId(int bookid) {
		this.bookid = bookid;
	}
	//sprawdz dla pól ksiazki
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

	@EJB
	WypozyczenieDAO wypozyczenieDAO;
	KsiazkaDAO ksiazkaDAO;
	
	//for pagination
	public String incrPage(String stronaaa) {
		int stronaa = 0;
		stronaa = Integer.parseInt(stronaaa);
		stronaa++;
		strona = String.valueOf(stronaa);
		return strona;
	}
	
	public String decPage(String stronaaa) {
		int stronaa = 0;
		stronaa = Integer.parseInt(stronaaa);
		stronaa--;
		if (stronaa == 0) 
			return strona;
		strona = String.valueOf(stronaa);
		return strona;
	}
	//for pagination
	
	public List<Wypozyczenie> getFullList(){
		return wypozyczenieDAO.getFullList();
	}

	public List<Wypozyczenie> getList(){
		List<Wypozyczenie> list = null;
		
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		searchParams.put("status", status);
		searchParams.put("imie", imie);
		searchParams.put("nazwisko", nazwisko);
		searchParams.put("login", login);
		searchParams.put("autor", autor);
		searchParams.put("tytul", tytul);

		list = wypozyczenieDAO.getList(searchParams);
		
		return list;
	}
	
	public List<Wypozyczenie> getListForUser(){
		List<Wypozyczenie> list = null;
		
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		searchParams.put("autor", autor);
		searchParams.put("tytul", tytul);
		searchParams.put("status", status);
		searchParams.put("userid", userid);
		
		list = wypozyczenieDAO.getListForUser(searchParams, strona);
		
		return list;
	}

	
	public String newWypozyczenie(Ksiazka ksiazka){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("ksiazka", ksiazka);
		Wypozyczenie wypozyczenie = new Wypozyczenie();
		session.setAttribute("wypozyczenie", wypozyczenie);
		int temp = ksiazka.getIle();
		if (temp - 1 >= 0 ) {
			ksiazka.setIle(temp - 1);
			wypozyczenie.setKsiazka(ksiazka);
			wypozyczenie.setUzytkownik(u);
			wypozyczenie.setStatus("oczekiwany");
			wypozyczenie.setDataOd(dataOdd);
			wypozyczenie.setDataWyp(dataWypoz);
			wypozyczenieDAO.create(wypozyczenie);
			//ksiazkaDAO.merge(ksiazka);
			return PAGE_MY_BORROWS;
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, 
						txtError.getString("cantBorrowBook"), null));
		return PAGE_STAY_AT_THE_SAME;
	}

	public String editWypozyczenie(Wypozyczenie wypozyczenie){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("wypozyczenie", wypozyczenie);
		return PAGE_STAY_AT_THE_SAME;
	}

	public String deleteWypozyczenie(Wypozyczenie wypozyczenie){
		wypozyczenieDAO.remove(wypozyczenie);
		return PAGE_STAY_AT_THE_SAME;
	}

}
