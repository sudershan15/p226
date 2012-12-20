package gash.hibernate.app;

import gash.hibernate.data.Person;
import gash.hibernate.util.HibernateUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;

/**
 * Sample application to demonstrate basic hibernate persistence - this is
 * effectively a DAO or SOA-storage. Note this is a very naive approach in that
 * it accounts for cases where we have multiple DAOs working together under one
 * transaction but does not have full support for:
 * 
 * <ol>
 * <li>Use of JTA for transactions (when running in a application server - need
 * jndi)
 * <li>Validation
 * </ol>
 * 
 * @author gash
 * 
 */
public class People {

	public Person find(Long id) {
		if (id == null)
			return null;

		Person r = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			r = (Person) session.load(Person.class, id);

			// forcing the proxy to load contacts - this not good as it costs us
			// another trip to the database
			if (r != null)
				r.getContacts().size();

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	/**
	 * here we use a class to act as a template for what we are searching for.
	 * Demonstrates the Criteria type of searching. The nice feature of Criteria
	 * is the ability for some compile-time checking whereas, HQL is all runtime
	 * checking
	 * 
	 * @param template
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Person> find(Person template) {
		ArrayList<Person> r = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Person.class);
			c.setFetchMode("contacts", FetchMode.JOIN);

			if (template.getId() != null) {
				c.add(Restrictions.idEq(template.getId()));
				c.setMaxResults(1);
			} else {
				if (template.getLastName() != null)
					c.add(Restrictions.like("firstname", template.getFirstName()));

				if (template.getLastName() != null)
					c.add(Restrictions.like("lastname", template.getLastName()));

				if (template.getRole() != null)
					c.add(Restrictions.like("role", template.getRole()));

				if (template.getNickName() != null)
					c.add(Restrictions.like("nickname", template.getNickName()));
			}

			//System.out.println("find() " + c.toString());

			// the join creates duplicate records - this will remove them
			Set<Person> set = new LinkedHashSet<Person>(c.list());
			r = new ArrayList<Person>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	/**
	 * demonstration of HQL and setting the fetch depth of the graph
	 * 
	 * @param template
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Card> findPhoneNumbersByRole(String role) {
		ArrayList<Card> r = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Query query = session
					.createQuery("select new gash.hibernate.app.Card(p.firstName, p.lastName, c.value) from Person as p join p.contacts as c where p.role = :role and c.type = :type");
			query.setString("role", role);
			query.setString("type", "email");

			// the join creates duplicate records - this will remove them
			Set<Card> set = new LinkedHashSet<Card>(query.list());
			r = new ArrayList<Card>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	/**
	 * demonstration of HQL and retrieving associations using joins
	 * 
	 * @param template
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Person> findEmailByRole(String role) {
		ArrayList<Person> r = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery("from Person as p  left join fetch p.contacts  where p.role = :role");
			query.setString("role", role);

			// the join creates duplicate records - this will remove them
			Set<Person> set = new LinkedHashSet<Person>(query.list());
			r = new ArrayList<Person>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	public void createPerson(Person p) {
		if (!validate(p))
			throw new RuntimeException("Invalid person");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.save(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	public void updatePerson(Person p) {
		if (!validate(p) || p.getId() == null)
			throw new RuntimeException("Invalid person");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.saveOrUpdate(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	public void removePerson(Long id) {
		if (id == null)
			return;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Person p = (Person) session.get(Person.class, id);
			if (p != null)
				session.delete(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	protected boolean validate(Person p) {
		if (p == null)
			return false;

		// TODO validate values
		return true;
	}

	public void showStats() {
		try {
			Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
			double queryCacheHitCount = stats.getQueryCacheHitCount();
			double queryCacheMissCount = stats.getQueryCacheMissCount();
			double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount);
			System.out.println("--> Query Hit ratio: " + queryCacheHitRatio);

			System.out.println("--> TX count: " + stats.getTransactionCount());

			EntityStatistics entityStats = stats.getEntityStatistics(Person.class.getName());
			long changes = entityStats.getInsertCount() + entityStats.getUpdateCount() + entityStats.getDeleteCount();
			System.out.println("--> " + Person.class.getName() + " changed " + changes + " times");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
