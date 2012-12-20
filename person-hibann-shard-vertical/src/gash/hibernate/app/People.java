package gash.hibernate.app;

import gash.hibernate.data.Contact;
import gash.hibernate.data.Person;
import gash.hibernate.util.HibernateUtil;
import gash.hibernate.util.HibernateUtil.Shard;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * This has been modified to show how sharding affects the database design and
 * RI. Note that we have to perform two TX when retrieving a person and their
 * contacts. This is not bad if our queries model the behavior of the
 * application in that user data and contact data are separate.
 * 
 * Arguments from the pro-lazy-loaders would state that the TX should extend
 * into the business and presentation layers to allow the layers to utilize the
 * TX to retrieve data as needed. However, this does not work if the layers are
 * on different processes!
 * 
 * @author gash1
 * 
 */
public class People {

	public Person find(Long id) {
		if (id == null)
			return null;

		Person r = null;
		Session session = HibernateUtil.getSessionFactory(Shard.persons).getCurrentSession();
		try {
			session.beginTransaction();
			r = (Person) session.load(Person.class, id);
			if (r != null)
				r.getId();

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	@SuppressWarnings("unchecked")
	public List<Contact> findContacts(Long personId) {
		if (personId == null)
			return null;

		ArrayList<Contact> r = null;
		Session session = HibernateUtil.getSessionFactory(Shard.contacts).getCurrentSession();
		try {
			session.beginTransaction();
			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Contact.class);
			c.setFetchMode("contacts", FetchMode.JOIN);
			c.add(Restrictions.eq("parent_id", personId));
			r = new ArrayList<Contact>(c.list());

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	@SuppressWarnings("unchecked")
	public List<Contact> findContactsOnlyWorksInTX(Long personId) {
		if (personId == null)
			return null;

		ArrayList<Contact> r = null;
		Session session = HibernateUtil.getSessionFactory(Shard.contacts).getCurrentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery("select c from Contact as c where c.parent_id = :id");

			// WARNING: because of lazy fetching, this is only valid in the TX,
			// unless you touch the objects!
			r = new ArrayList<Contact>(query.list());

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

		Session session = HibernateUtil.getSessionFactory(Shard.persons).getCurrentSession();
		try {
			session.beginTransaction();
			session.save(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	public void addContacts(Long personId, List<Contact> list) {
		if (personId == null || list == null)
			throw new RuntimeException("Invalid person or null list");

		Session session = HibernateUtil.getSessionFactory(Shard.contacts).getCurrentSession();
		try {
			session.beginTransaction();
			for (Contact c : list) {
				c.setId(personId);
				session.save(c);
			}
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
