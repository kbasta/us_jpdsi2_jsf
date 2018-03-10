package bib.uzytkownik;

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
import bib.dao.UzytkownikDAO;
import bib.entities.Uzytkownik;

@ManagedBean
@ViewScoped
public class UzytkownikAdminEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_PERSON_LIST = "/pages/login";
	private static final String PAGE_ADMIN_USER_LIST = "/pages/admin/users/userList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
			.getExternalContext().getSession(true);
	private Uzytkownik logedUser = (Uzytkownik) session.getAttribute("user");
	
	@EJB
	UzytkownikDAO uzytkownikDAO;

	private String id;
	private String imie;
	private String nazwisko;
	private String login;
	private String haslo;
	private String rola;
	private int kara;
	
	// getters, setters (properties used at the JSF page)

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

	public String getHaslo() {
		return haslo;
	}

	public void setHaslo(String haslo) {
		this.haslo = haslo;
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



	// object to be edited/inserted
	private Uzytkownik uzytkownik = null;

	@PostConstruct
	public void postConstruct() {
		// A. load uzytkownik if passed through session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		uzytkownik = (Uzytkownik) session.getAttribute("uzytkownik");

		// cleaning: attribute received => delete it from session
		if (uzytkownik != null) {
			session.removeAttribute("uzytkownik");
		}

		// B. if object not loaded try to get Uzytkownik by id passed as GET
		// parameter
		if (uzytkownik == null) {
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
					uzytkownik = uzytkownikDAO.find(ident);
				}
			}
		}
		// if loaded record is to be edited then copy data to properties
		if (uzytkownik != null && uzytkownik.getId() != null) {
			setImie(uzytkownik.getImie());
			setNazwisko(uzytkownik.getNazwisko());
			setLogin(uzytkownik.getLogin());
			setHaslo(uzytkownik.getHaslo());
			setKara(uzytkownik.getKara());
			setRola(uzytkownik.getRola());
		}
	}

	private boolean validate() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;

		if (imie == null || imie.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Imie wymagane"));
		}
		if (nazwisko == null || nazwisko.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Nazwisko wymagane"));
		}
		if (login == null || login.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Login wymagane"));
		}
		if (haslo == null || haslo.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("Hasło wymagane"));
		}
		


		// if no errors
		if (ctx.getMessageList().isEmpty()) {
			uzytkownik.setImie(imie.trim());
			uzytkownik.setNazwisko(nazwisko.trim());
			uzytkownik.setLogin(login.trim());
			uzytkownik.setHaslo(haslo.trim());
			uzytkownik.setRola("user");
			uzytkownik.setKara(kara);
			result = true;
		}

		return result;
	}

	public String saveData() {

		// no Uzytkownik object passed
		if (uzytkownik == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Błędne uzycie systemu"));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (uzytkownik.getId() == null) {
				// new record
				uzytkownikDAO.create(uzytkownik);
			} else {
				// existing record
				uzytkownikDAO.merge(uzytkownik);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("bląd"));
			return PAGE_ADMIN_USER_LIST;
		}

		if (logedUser.getRola().equals("admin")) return PAGE_ADMIN_USER_LIST;
		return PAGE_PERSON_LIST;
	}
}
