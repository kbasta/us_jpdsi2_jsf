package bib.wypozyczenie;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

	private static final String PAGE_ADMIN_BORROWS_LIST = "/pages/admin/borrows/borrowList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	@ManagedProperty("#{txtError}")
	private ResourceBundle txtError;
	
	public void setTxtError(ResourceBundle txtError) {
		this.txtError = txtError;
	}
	
	@EJB
	WypozyczenieDAO wypozyczenieDAO;
	@EJB
	KsiazkaDAO ksiazkaDAO;
	UzytkownikDAO uzytkownikDAO;
	
	private String idWyp;
	private Date dataOdd;
	private Date dataWyp;
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

	public Date getDataOdd() {
		return dataOdd;
	}

	public void setDataOdd(Date dataOdd) {
		this.dataOdd = dataOdd;
	}

	public Date getDataWyp() {
		return dataWyp;
	}

	public void setDataWyp(Date dataWyp) {
		this.dataWyp = dataWyp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private Wypozyczenie wypozyczenie = null;

	@PostConstruct
	public void postConstruct() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		wypozyczenie = (Wypozyczenie) session.getAttribute("wypozyczenie");

		if (wypozyczenie != null) {
			session.removeAttribute("wypozyczenie");
		}

		if (wypozyczenie == null) {
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			idWyp = req.getParameter("p");
			if (idWyp != null) {
				Integer ident = null;
				try {
					ident = Integer.parseInt(idWyp);
				} catch (NumberFormatException e) {
				}
				if (ident != null) {
					wypozyczenie = wypozyczenieDAO.find(ident);
				}
			}
		}

		if (wypozyczenie != null && wypozyczenie.getIdWyp() != 0) {
			setUzytkownik(wypozyczenie.getUzytkownik());
			setKsiazka(wypozyczenie.getKsiazka());
			setDataWyp(wypozyczenie.getDataWyp());
			setDataOdd(wypozyczenie.getDataOd());
			setStatus(wypozyczenie.getStatus());
		}
	}

	private boolean validate() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String tempDate1 = sdf.format(new Date());
		
		if(wypozyczenie.getStatus().equals("oddany")) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("cantUpdateBorrowStatus"), null));
			return result;
		}
		
		if (status.equals("oddany"))
			if (!sdf.format(dataOdd).equals(tempDate1)) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
						txtError.getString("changeGivedBackDate"), null));
				return result;
		}
		
		if (ctx.getMessageList().isEmpty()) {
			wypozyczenie.setUzytkownik(uzytkownik);
			wypozyczenie.setKsiazka(ksiazka);
			wypozyczenie.setDataWyp(dataWyp);
			wypozyczenie.setDataOd(dataOdd);
			wypozyczenie.setStatus(status); 
			
			if (wypozyczenie.getStatus().equals("oddany")) {
				Ksiazka k = ksiazka;
				int temp = ksiazka.getIle();
				temp++;
				k.setIle(temp);
				ksiazkaDAO.merge(k);
			}
			result = true;
		}

		return result;
	}

	public String saveData() {
		if (wypozyczenie == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, 
							txtError.getString("unknownSystemError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (wypozyczenie.getIdWyp() == 0) {
				wypozyczenieDAO.create(wypozyczenie);
			} else {
				wypozyczenieDAO.merge(wypozyczenie);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, 
							txtError.getString("unknownDatabaseError"), null));
			return PAGE_ADMIN_BORROWS_LIST;
		}

		return PAGE_ADMIN_BORROWS_LIST;
	}
}
