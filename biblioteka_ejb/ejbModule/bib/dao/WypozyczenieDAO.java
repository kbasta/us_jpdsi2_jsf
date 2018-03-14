package bib.dao;

import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import bib.entities.Wypozyczenie;

@Stateless
public class WypozyczenieDAO {
	
	@PersistenceContext(unitName = "bibPU")
	protected EntityManager em;
	
	public void create(Wypozyczenie wypozyczenie) {
		em.persist(wypozyczenie);
		// em.flush();
	}

	public Wypozyczenie merge(Wypozyczenie wypozyczenie) {
		return em.merge(wypozyczenie);
	}

	public void remove(Wypozyczenie wypozyczenie) {
		em.remove(em.merge(wypozyczenie));
	}

	public Wypozyczenie find(Object id) {
		return em.find(Wypozyczenie.class, id);
	}
	
	public List<Wypozyczenie> getFullList() {
		List<Wypozyczenie> list = null;

		Query query = em.createQuery("select p from Wypozyczenie p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Wypozyczenie> getList(Map<String, Object> searchParams) {
		List<Wypozyczenie> list = null;

		// 1. Build query string with parameters
		String select = "select w ";
		String from = "from Wypozyczenie w ";
		String join = "LEFT JOIN w.ksiazka k LEFT JOIN w.uzytkownik u ";
		String where = "";
		String orderby = "order by w.dataOd asc";
		
		String status = (String) searchParams.get("status");
		if (status != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "w.status like :status ";
		}

		String imie = (String) searchParams.get("imie");
		if (imie != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "u.imie like :imie ";
		}
		
		String nazwisko = (String) searchParams.get("nazwisko");
		if (nazwisko != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "u.nazwisko like :nazwisko ";
		}
		
		String login = (String) searchParams.get("login");
		if (login != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "u.login like :login ";
		}
		
		String autor = (String) searchParams.get("autor");
		if (autor != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "k.autor like :autor ";
		}
		
		String tytul = (String) searchParams.get("tytul");
		if (tytul != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "k.tytul like :tytul ";
		}
		
		// 2. Create query object
		Query query = em.createQuery(select + from + join + where + orderby);

		// 3. Set configured parameters
		if (status != null) {
			query.setParameter("status", "%"+status+"%");
		}
		if (imie != null) {
			query.setParameter("imie", "%"+imie+"%");
		}
		if (nazwisko != null) {
			query.setParameter("nazwisko", "%"+nazwisko+"%");
		}
		if (login != null) {
			query.setParameter("login", "%"+login+"%");
		}
		if (autor != null) {
			query.setParameter("autor", "%"+autor+"%");
		}
		if (tytul != null) {
			query.setParameter("tytul", "%"+tytul+"%");
		}
		
		
		// ... other parameters ... 

		// 4. Execute query and retrieve list of Person objects
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Wypozyczenie> getListForUser(Map<String, Object> searchParams, String strona) {
		List<Wypozyczenie> list = null;

		// 1. Build query string with parameters
		String select = "select w ";
		String from = "from Wypozyczenie w ";
		String join = "LEFT JOIN w.ksiazka k LEFT JOIN w.uzytkownik u ";
		String where = "";
		String orderby = "order by w.dataOd asc";

		// search for surname
		int userid = (int) searchParams.get("userid");
		if (userid != 0) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "u.id = :userid ";
		}
		
		String status = (String) searchParams.get("status");
		if (status != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "w.status like :status ";
		}
		
		String autor = (String) searchParams.get("autor");
		if (autor != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "k.autor like :autor ";
		}
		
		String tytul = (String) searchParams.get("tytul");
		if (tytul != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "k.tytul like :tytul ";
		}
	
		// ... other parameters ... 

		// 2. Create query object
		Query query = em.createQuery(select + from + join + where + orderby);

		// 3. Set configured parameters
		if (userid != 0) {
			query.setParameter("userid", userid);
		}
		if (status != null) {
			query.setParameter("status", "%"+status+"%");
		}
		if (autor != null) {
			query.setParameter("autor", "%"+autor+"%");
		}
		if (tytul != null) {
			query.setParameter("tytul", "%"+tytul+"%");
		}
		
		// ... other parameters ... 
		int stronaa = 1;
		stronaa = Integer.parseInt(strona);
		// 4. Execute query and retrieve list of Person objects
		try {
			query.setFirstResult(( stronaa - 1 ) * 5);
			query.setMaxResults(5);
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
