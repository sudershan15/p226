package gash.hibernate.app;

import gash.hibernate.data.FamilyMember;
import gash.hibernate.data.Person;
import gash.hibernate.util.GeneratePeople;
import gash.hibernate.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Populate extends People {

	public static void main(String[] args) {
		System.out.println("Loading data to pplann");
		Populate p = new Populate();
		p.bulkGenerate(5);
	}

	public Populate() {
		super();

		if (locator == null)
			throw new RuntimeException("Missing locator");
	}

	public void bulkGenerate(int count) {
		GeneratePeople gen = new GeneratePeople();
		for (int n = 0; n < count; n++) {
			Person p = gen.createUser();
			System.out.println("--> creating: " + p);
			Session session = locator.locate(p.getLastName());
			try {
				Transaction tx = session.beginTransaction();
				session.save(p);
				if (n % 100 == 0) {
					session.flush();
					session.clear();
				}

				tx.commit();
			} finally {
				// session.close();
			}
		}
	}
}
