package bib.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the ksiazka database table.
 * 
 */
@Entity
@NamedQuery(name="Ksiazka.findAll", query="SELECT k FROM Ksiazka k")
public class Ksiazka implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String autor;

	private String gatunek;

	private int ile;

	private String tytul;

	//bi-directional many-to-one association to Wypozyczenie
	@OneToMany(mappedBy="ksiazka")
	private List<Wypozyczenie> wypozyczenies;

	public Ksiazka() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAutor() {
		return this.autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getGatunek() {
		return this.gatunek;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}

	public int getIle() {
		return this.ile;
	}

	public void setIle(int ile) {
		this.ile = ile;
	}

	public String getTytul() {
		return this.tytul;
	}

	public void setTytul(String tytul) {
		this.tytul = tytul;
	}

	public List<Wypozyczenie> getWypozyczenies() {
		return this.wypozyczenies;
	}

	public void setWypozyczenies(List<Wypozyczenie> wypozyczenies) {
		this.wypozyczenies = wypozyczenies;
	}

	public Wypozyczenie addWypozyczeny(Wypozyczenie wypozyczeny) {
		getWypozyczenies().add(wypozyczeny);
		wypozyczeny.setKsiazka(this);

		return wypozyczeny;
	}

	public Wypozyczenie removeWypozyczeny(Wypozyczenie wypozyczeny) {
		getWypozyczenies().remove(wypozyczeny);
		wypozyczeny.setKsiazka(null);

		return wypozyczeny;
	}

}