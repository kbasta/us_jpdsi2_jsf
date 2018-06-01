package bib.uzytkownik;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import bib.dao.UzytkownikDAO;
import bib.entities.Uzytkownik;

@ManagedBean
@ViewScoped
public class UzytkownikEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_PERSON_LIST = "/pages/login";
	private static final String PAGE_ADMIN_USER_LIST = "/pages/admin/users/userList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	@ManagedProperty("#{txtUser}")
	private ResourceBundle txtUser;
	
	@ManagedProperty("#{txtError}")
	private ResourceBundle txtError;
	
	public void setTxtUser(ResourceBundle txtUser) {
		this.txtUser = txtUser;
	}
	
	public void setTxtError(ResourceBundle txtError) {
		this.txtError = txtError;
	}
	
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
				Integer ident = null;
				try {
					ident = Integer.parseInt(id);
				} catch (NumberFormatException e) {
				}
				if (ident != null) {
					uzytkownik = uzytkownikDAO.find(ident);
				}
			}
		}
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
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtUser.getString("nameRequired"), null));
		}
		if (nazwisko == null || nazwisko.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtUser.getString("surnameRequired"), null));
		}
		if (login == null || login.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtUser.getString("loginRequired"), null));
		}
		if (haslo == null || haslo.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtUser.getString("passwdRequired"), null));
		}
		
		if (uzytkownikDAO.getLoginForRegister(login) ) 
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("loginOccupied"), null));

		if (ctx.getMessageList().isEmpty()) {
			uzytkownik.setImie(imie.trim());
			uzytkownik.setNazwisko(nazwisko.trim());
			uzytkownik.setLogin(login.trim());
			uzytkownik.setHaslo(passwdHash(haslo.trim()));
			uzytkownik.setRola("user");
			uzytkownik.setKara(0);
			result = true;
		}

		return result;
	}

	public String saveData() {
		if (uzytkownik == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, 
							txtError.getString("unknownSystemError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (uzytkownik.getId() == null) {
				uzytkownikDAO.create(uzytkownik);
			} else {
				uzytkownikDAO.merge(uzytkownik);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, 
							txtError.getString("unknownDatabaseError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
		return PAGE_PERSON_LIST;
	}
	
	public static String passwdHash(String text) {
        String hashText = "";
        try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] md5HashBytes = md5.digest(text.getBytes());
                
                StringBuilder sb = new StringBuilder();
                for (byte b : md5HashBytes) {
                        String toAppend = String.format("%2X", b).replace(" ", "0");
                        sb.append(toAppend);
                }
                
                hashText = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
        }
        return hashText;
	}
}
