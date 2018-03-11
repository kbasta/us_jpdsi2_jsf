package bib.wypozyczenie;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import bib.dao.KsiazkaDAO;
import bib.dao.UzytkownikDAO;
import bib.dao.WypozyczenieDAO;
import bib.entities.Ksiazka;
import bib.entities.Uzytkownik;
import bib.entities.Wypozyczenie;

@ManagedBean
@ViewScoped
public class WypozyczenieAdminEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_ADMIN_BOOK_LIST = "/pages/admin/books/bookList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	@EJB
	WypozyczenieDAO wypozyczenieDAO;
	KsiazkaDAO ksiazkaDAO;
	UzytkownikDAO uzytkownikDAO;
	
	private String idWyp;
	private String dataOdd;
	private String dataWyp;
	private String status;
	private Ksiazka ksiazka;
	private Uzytkownik uzytkownik;
	
	public Ksiazka getKsiazka() {
		return ksiazka;
	}

	public void setKsiazka(Ksiazka ksiazka) {
		this.ksiazka = ksiazka;
	}

	public Uzytkownik getUzytkownik() {
		return uzytkownik;
	}

	public void setUzytkownik(Uzytkownik uzytkownik) {
		this.uzytkownik = uzytkownik;
	}
	
	public String getIdWyp() {
		return idWyp;
	}

	public void setIdWyp(String idWyp) {
		this.idWyp = idWyp;
	}

	public String getDataOdd() {
		return dataOdd;
	}

	public void setDataOdd(String dataOdd) {
		this.dataOdd = dataOdd;
	}

	public String getDataWyp() {
		return dataWyp;
	}

	public void setDataWyp(String dataWyp) {
		this.dataWyp = dataWyp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// object to be edited/inserted
	private Wypozyczenie wypozyczenie = null;

	@PostConstruct
	public void postConstruct() {
		// A. load wypozyczenie if passed through session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		wypozyczenie = (Wypozyczenie) session.getAttribute("wypozyczenie");

		// cleaning: attribute received => delete it from session
		if (wypozyczenie != null) {
			session.removeAttribute("wypozyczenie");
		}

		// B. if object not loaded try to get Wypozyczenie by id passed as GET
		// parameter
		if (wypozyczenie == null) {
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			idWyp = req.getParameter("p");
			if (idWyp != null) {
				// parse id
				Integer ident = null;
				try {
					ident = Integer.parseInt(idWyp);
				} catch (NumberFormatException e) {
				}
				if (ident != null) {
					// if parsing successful
					wypozyczenie = wypozyczenieDAO.find(ident);
				}
			}
		}
		// if loaded record is to be edited then copy data to properties
		if (wypozyczenie != null && wypozyczenie.getIdWyp() != 0) {
			setStatus(wypozyczenie.getStatus());
		}
	}

	private boolean validate() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;

		
		// if no errors
		if (ctx.getMessageList().isEmpty()) {
			wypozyczenie.setStatus(status); 
			result = true;
		}

		return result;
	}

	public String saveData() {

		// no Wypozyczenie object passed
		if (wypozyczenie == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Błędne uzycie systemu"));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (wypozyczenie.getIdWyp() == 0) {
				// new record
				wypozyczenieDAO.create(wypozyczenie);
			} else {
				// existing record
				wypozyczenieDAO.merge(wypozyczenie);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("bląd"));
			return PAGE_ADMIN_BOOK_LIST;
		}

		return PAGE_ADMIN_BOOK_LIST;
	}
}
