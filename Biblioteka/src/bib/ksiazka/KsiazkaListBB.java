package bib.ksiazka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.chart.PieChartModel;

import bib.dao.KsiazkaDAO;
import bib.entities.Ksiazka;

@ManagedBean
public class KsiazkaListBB {
	private static final String PAGE_BOOK_EDIT = "/pages/admin/books/bookEdit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String tytul;
	private String autor;
	private String gatunek;
	public String strona = "1";
	
	private PieChartModel pieModel1;

	@ManagedProperty("#{txtError}")
	private ResourceBundle txtError;
	
	public void setTxtError(ResourceBundle txtError) {
		this.txtError = txtError;
	}
	
	public String getStrona() {
		return strona;
	}

	public void setStrona(String strona) {
		this.strona = strona;
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
	
	public String getGatunek() {
		return gatunek;
	}
	
	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}
	
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

	@EJB
	KsiazkaDAO ksiazkaDAO;
	
	public List<Ksiazka> getFullList(){
		return ksiazkaDAO.getFullList();
	}

	public List<Ksiazka> getList(){
		List<Ksiazka> list = null;
		
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
		if (strona != null && strona.length() > 0){
			searchParams.put("stona", strona);
		}
		
		list = ksiazkaDAO.getList(searchParams);
		
		return list;
	}
	
	public List<Ksiazka> getListPagination(){
		List<Ksiazka> listPagination = null;
		
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
		
		listPagination = ksiazkaDAO.getListPagination(searchParams, strona);
		
		return listPagination;
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
		try {
			ksiazkaDAO.remove(ksiazka);
		}
		catch (Exception e){
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					txtError.getString("cantDeleteBook"), null));
		}
		return PAGE_STAY_AT_THE_SAME;
	}
	
    @PostConstruct
    public void init() {
        createPieModels();
    }
     
    public PieChartModel getPieModel1() {
        return pieModel1;
    }
	
	private void createPieModels() {
        createPieModel1();
    }
     
    private void createPieModel1() {
        pieModel1 = new PieChartModel();
        List<Ksiazka> lista = getList(); 
        
        for(Ksiazka l : lista){
        	pieModel1.set(l.getGatunek(),l.getIle());
        }
         
        pieModel1.setTitle("Satystyka");
        pieModel1.setLegendPosition("e");
    }
}
