package bib.ksiazka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import bib.dao.KsiazkaDAO;
import bib.entities.Ksiazka;

@ManagedBean
public class KsiazkaListBB {
	private static final String PAGE_BOOK_EDIT = "/pages/admin/books/bookEdit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String tytul;
	private String autor;
	private String gatunek;
	
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

	//Dependency injection
	// - no setter method needed in this case
	@EJB
	KsiazkaDAO ksiazkaDAO;
	
	public List<Ksiazka> getFullList(){
		return ksiazkaDAO.getFullList();
	}

	public List<Ksiazka> getList(){
		List<Ksiazka> list = null;
		
		//1. Prepare search params
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		if (tytul != null && tytul.length() > 0){
			searchParams.put("tytul", tytul);
		}
		if (autor != null && autor.length() > 0){
			searchParams.put("autor", autor);
		}
		if (gatunek != null && gatunek.length() > 0){
			searchParams.put("gatunek", gatunek);
		}
		
		//2. Get list
		list = ksiazkaDAO.getList(searchParams);
		
		return list;
	}
	
	public String newKsiazka(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		Ksiazka ksiazka = new Ksiazka();
		session.setAttribute("ksiazka", ksiazka);
		return PAGE_BOOK_EDIT;
	}

	public String editKsiazka(Ksiazka ksiazka){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("ksiazka", ksiazka);
		return PAGE_BOOK_EDIT;
	}

	public String deleteKsiazka(Ksiazka ksiazka){
		ksiazkaDAO.remove(ksiazka);
		return PAGE_STAY_AT_THE_SAME;
	}
}
