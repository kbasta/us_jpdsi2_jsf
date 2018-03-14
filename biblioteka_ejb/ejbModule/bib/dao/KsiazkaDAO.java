package bib.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import bib.entities.Ksiazka;

@Stateless
public class KsiazkaDAO {
	
	@PersistenceContext(unitName = "bibPU")
	protected EntityManager em;
	
	public void create(Ksiazka ksiazka) {
		em.persist(ksiazka);
		// em.flush();
	}

	public Ksiazka merge(Ksiazka ksiazka) {
		return em.merge(ksiazka);
	}

	public void remove(Ksiazka ksiazka) {
		em.remove(em.merge(ksiazka));
	}

	public Ksiazka find(Object id) {
		return em.find(Ksiazka.class, id);
	}
	
	public List<Ksiazka> getFullList() {
		List<Ksiazka> list = null;

		Query query = em.createQuery("select p from Ksiazka p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Ksiazka> getList(Map<String, Object> searchParams) {
		List<Ksiazka> list = null;

		// 1. Build query string with parameters
		String select = "select p ";
		String from = "from Ksiazka p ";
		String where = "";
		String orderby = "order by p.tytul asc, p.autor";

		// search for surname
		String tytul = (String) searchParams.get("tytul");
		if (tytul != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.tytul like :tytul ";
		}
		
		String autor = (String) searchParams.get("autor");
		if (autor != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.autor like :autor ";
		}
		
		String gatunek = (String) searchParams.get("gatunek");
		if (gatunek != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.gatunek like :gatunek ";
		}
		
		
		
		// ... other parameters ... 

		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);

		// 3. Set configured parameters
		if (tytul != null) {
			query.setParameter("tytul", "%"+tytul+"%");
		}
		if (autor != null) {
			query.setParameter("autor", "%"+autor+"%");
		}
		if (gatunek != null) {
			query.setParameter("gatunek", "%"+gatunek+"%");
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
	
	public List<Ksiazka> getListPagination(Map<String, Object> searchParams, String strona) {
		List<Ksiazka> listPagination = null;
		
		// 1. Build query string with parameters
		String select = "select p ";
		String from = "from Ksiazka p ";
		String where = "";
		String orderby = "order by p.tytul asc, p.autor ";
		String limit = "";

		// search for surname
		String tytul = (String) searchParams.get("tytul");
		if (tytul != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.tytul like :tytul ";
		}
		
		String autor = (String) searchParams.get("autor");
		if (autor != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.autor like :autor ";
		}
		
		String gatunek = (String) searchParams.get("gatunek");
		if (gatunek != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.gatunek like :gatunek ";
		}


		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby + limit);

		// 3. Set configured parameters
		if (tytul != null) {
			query.setParameter("tytul", "%"+tytul+"%");
		}
		if (autor != null) {
			query.setParameter("autor", "%"+autor+"%");
		}
		if (gatunek != null) {
			query.setParameter("gatunek", "%"+gatunek+"%");
		}

		// ... other parameters ... 

		int stronaa = 1;
		stronaa = Integer.parseInt(strona);
		// 4. Execute query and retrieve list of Person objects
		try {
			query.setFirstResult(( stronaa - 1 ) * 5);
			query.setMaxResults(5);
			listPagination = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listPagination;
	}
	
	public boolean getBookForAddNew(String tytul, String autor) {
		List<Ksiazka> bookList = null;
		String select = "select p ";
		String from = "from Ksiazka p ";
		String where = "where ";
		where += "p.tytul like :tytul and p.autor like :autor";
		
		Query query = em.createQuery(select + from + where);
		query.setParameter("tytul", tytul);
		query.setParameter("autor", autor);
		bookList = query.getResultList();
		
		if (bookList.isEmpty()) return false;
		else return true;
	}

}
