package gash.hibernate.app;

import gash.hibernate.data.Person;
import gash.hibernate.util.HibernateUtil;
import gash.hibernate.util.HibernateUtil.Shard;

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
	protected LocationHash locator;

	public People() {
		// TODO node names, and number of shards (nodes) should be obtained from
		// a config file
		locator = new LocationHash(2);
	}

	public Person find(Long id) {
		if (id == null)
			return null;

		Person r = null;
		Session[] slist = locator.allLocations();

		for (Session session : slist) {
			try {
				session.beginTransaction();
				r = (Person) session.load(Person.class, id);
				if (r == null)
					continue;

				r.getContacts().size();
				session.getTransaction().commit();

				break;
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
			}
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

		Session[] slist = locator.allLocations();
		for (Session session : slist) {
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

				// System.out.println("find() " + c.toString());

				// the join creates duplicate records - this will remove them
				Set<Person> set = new LinkedHashSet<Person>(c.list());
				if (set != null && set.size() > 0)
					r = new ArrayList<Person>(set);
				session.getTransaction().commit();
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
				throw e;
			}

			if (r != null)
				break;
		}
		return r;
	}

	public void createPerson(Person p) {
		if (!validate(p))
			throw new RuntimeException("Invalid person");

		Session session = locator.locate(p.getLastName());
		try {
			session.beginTransaction();
			session.save(p);
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
}
