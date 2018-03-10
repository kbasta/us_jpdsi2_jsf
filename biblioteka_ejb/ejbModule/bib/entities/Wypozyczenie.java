package bib.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the wypozyczenie database table.
 * 
 */
@Entity
@NamedQuery(name="Wypozyczenie.findAll", query="SELECT w FROM Wypozyczenie w")
public class Wypozyczenie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idWyp;

	@Temporal(TemporalType.DATE)
	@Column(name="data_od")
	private Date dataOd;

	@Temporal(TemporalType.DATE)
	@Column(name="data_wyp")
	private Date dataWyp;

	private String status;

	//bi-directional many-to-one association to Ksiazka
	@ManyToOne
	@JoinColumn(name="bookid")
	private Ksiazka ksiazka;

	//bi-directional many-to-one association to Uzytkownik
	@ManyToOne
	@JoinColumn(name="userid")
	private Uzytkownik uzytkownik;

	public Wypozyczenie() {
	}

	public int getIdWyp() {
		return this.idWyp;
	}

	public void setIdWyp(int idWyp) {
		this.idWyp = idWyp;
	}

	public Date getDataOd() {
		return this.dataOd;
	}

	public void setDataOd(Date dataOd) {
		this.dataOd = dataOd;
	}

	public Date getDataWyp() {
		return this.dataWyp;
	}

	public void setDataWyp(Date dataWyp) {
		this.dataWyp = dataWyp;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Ksiazka getKsiazka() {
		return this.ksiazka;
	}

	public void setKsiazka(Ksiazka ksiazka) {
		this.ksiazka = ksiazka;
	}

	public Uzytkownik getUzytkownik() {
		return this.uzytkownik;
	}

	public void setUzytkownik(Uzytkownik uzytkownik) {
		this.uzytkownik = uzytkownik;
	}

}