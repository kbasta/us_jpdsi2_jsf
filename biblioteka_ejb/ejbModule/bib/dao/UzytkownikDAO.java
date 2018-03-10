package bib.dao;

import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import bib.entities.Uzytkownik;

@Stateless
public class UzytkownikDAO {
	
	@PersistenceContext(unitName = "bibPU")
	protected EntityManager em;
	
	public void create(Uzytkownik uzytkownik) {
		em.persist(uzytkownik);
		// em.flush();
	}

	public Uzytkownik merge(Uzytkownik uzytkownik) {
		return em.merge(uzytkownik);
	}

	public void remove(Uzytkownik uzytkownik) {
		em.remove(em.merge(uzytkownik));
	}

	public Uzytkownik find(Object id) {
		return em.find(Uzytkownik.class, id);
	}
	
	public List<Uzytkownik> getFullList() {
		List<Uzytkownik> list = null;

		Query query = em.createQuery("select p from Uzytkownik p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Uzytkownik> getList(Map<String, Object> searchParams) {
		List<Uzytkownik> list = null;

		// 1. Build query string with parameters
		String select = "select p ";
		String from = "from Uzytkownik p ";
		String where = "";
		String orderby = "order by p.nazwisko asc, p.id";

		// search for surname
		String imie = (String) searchParams.get("imie");
		if (imie != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.imie like :imie ";
		}
		
		String nazwisko = (String) searchParams.get("nazwisko");
		if (nazwisko != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.nazwisko like :nazwisko ";
		}
		
		String login = (String) searchParams.get("login");
		if (login != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.login like :login ";
		}
		
		// ... other parameters ... 

		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);

		// 3. Set configured parameters
		if (imie != null) {
			query.setParameter("imie", "%"+imie+"%");
		}
		
		if (nazwisko != null) {
			query.setParameter("nazwisko", "%"+nazwisko+"%");
		}
		
		if (login != null) {
			query.setParameter("login", "%"+login+"%");
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
	
	public List<Uzytkownik> getUserForLogin(Map<String, Object> loginParams) {
		List<Uzytkownik> list = null;
		String select = "select p ";
		String from = "from Uzytkownik p ";
		String where = "where ";
		
		String login = (String) loginParams.get("login");
		String passwd = (String) loginParams.get("passwd");
		where += "p.login like :login and p.haslo like :passwd";
		
		Query query = em.createQuery(select + from + where);
		query.setParameter("login", login);
		query.setParameter("passwd", passwd);
		list = query.getResultList();
		return list;
	}
	
	public boolean getLoginForRegister(String login) {
		List<Uzytkownik> logList = null;
		String select = "select p ";
		String from = "from Uzytkownik p ";
		String where = "where ";
		where += "p.login like :login";
		
		Query query = em.createQuery(select + from + where);
		query.setParameter("login", login);
		logList = query.getResultList();
		
		//Uzytkownik user = list.get(0);
		//if (user.getLogin() != null) return false;
		//else return true;
		//return logList;
		if (logList.isEmpty()) return false;
		else return true;
	}
}
