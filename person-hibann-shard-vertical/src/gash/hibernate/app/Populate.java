package gash.hibernate.app;

import gash.hibernate.data.Contact;
import gash.hibernate.data.Person;
import gash.hibernate.util.GeneratePeople;
import gash.hibernate.util.GeneratePeople.GenData;
import gash.hibernate.util.HibernateUtil;
import gash.hibernate.util.HibernateUtil.Shard;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This has been modified to support vertical sharding
 * 
 * @author gash1
 * 
 */
public class Populate extends People {

	public static void main(String[] args) {
		System.out.println("Loading data to pplhib");
		Populate p = new Populate();
		p.bulkGenerate(10);
	}

	public void bulkGenerate(int count) {

		Session session = HibernateUtil.getSessionFactory(Shard.persons).getCurrentSession();
		try {
			ArrayList<GenData> ppl = new ArrayList<GenData>();

			GeneratePeople gen = new GeneratePeople();
			Transaction tx = session.beginTransaction();

			for (int n = 0; n < count; n++) {
				GenData d = gen.createUser();

				// pass one: save person to generate ID
				Long id = (Long) session.save(d.p);
				ppl.add(d);
				System.out.println("created person: " + id);
			}

			tx.commit();

			Session session2 = HibernateUtil.getSessionFactory(Shard.contacts).getCurrentSession();
			Transaction tx2 = session2.beginTransaction();

			for (GenData d : ppl) {
				// pass two: set ID and save contacts
				for (Contact c : d.c) {
					c.setParent_id(d.p.getId());
					System.out.println("--> " + c);
					session2.save(c);
				}
			}

			tx2.commit();
		} finally {
			// session.close();
		}
	}
}
