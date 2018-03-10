package bib.ksiazka;

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
import bib.entities.Ksiazka;

@ManagedBean
@ViewScoped
public class KsiazkaAdminEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_ADMIN_BOOK_LIST = "/pages/admin/books/bookList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	@EJB
	KsiazkaDAO ksiazkaDAO;

	private String id;
	private String tytul;
	private String autor;
	private String gatunek;
	private int ile;
	
	// getters, setters (properties used at the JSF page)


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

	public String getGatunek() {
		return gatunek;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}

	public int getIle() {
		return ile;
	}

	public void setIle(int ile) {
		this.ile = ile;
	}


	// object to be edited/inserted
	private Ksiazka ksiazka = null;

	@PostConstruct
	public void postConstruct() {
		// A. load ksiazka if passed through session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		ksiazka = (Ksiazka) session.getAttribute("ksiazka");

		// cleaning: attribute received => delete it from session
		if (ksiazka != null) {
			session.removeAttribute("ksiazka");
		}

		// B. if object not loaded try to get Ksiazka by id passed as GET
		// parameter
		if (ksiazka == null) {
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			id = req.getParameter("p");
			if (id != null) {
				// parse id
				Integer ident = null;
				try {
					ident = Integer.parseInt(id);
				} catch (NumberFormatException e) {
				}
				if (ident != null) {
					// if parsing successful
					ksiazka = ksiazkaDAO.find(ident);
				}
			}
		}
		// if loaded record is to be edited then copy data to properties
		if (ksiazka != null && ksiazka.getId() != 0) {
			setAutor(ksiazka.getAutor());
			setTytul(ksiazka.getTytul());
			setGatunek(ksiazka.getGatunek());
			setIle(ksiazka.getIle());
		}
	}

	private boolean validate() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;

		if (autor == null || autor.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Autor wymagane"));
		}
		if (tytul == null || tytul.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Tytuł wymagane"));
		}
		if (gatunek == null || gatunek.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Gatunek wymagane"));
		}
		if ((Integer)ile == null || ile < 0) {
			ctx.addMessage(null, new FacesMessage("Liczba sztuk wymagane"));
		}
		
		if (id == null)
			if (ksiazkaDAO.getBookForAddNew(tytul, autor) ) ctx.addMessage(null, new FacesMessage("Wprowadz inna ksiazke lub edytuj nowa"));

		// if no errors
		if (ctx.getMessageList().isEmpty()) {
			ksiazka.setTytul(tytul.trim());
			ksiazka.setAutor(autor.trim());
			ksiazka.setGatunek(gatunek.trim());
			ksiazka.setIle(ile);
			result = true;
		}

		return result;
	}

	public String saveData() {

		// no Ksiazka object passed
		if (ksiazka == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Błędne uzycie systemu"));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (ksiazka.getId() == null) {
				// new record
				ksiazkaDAO.create(ksiazka);
			} else {
				// existing record
				ksiazkaDAO.merge(ksiazka);
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
