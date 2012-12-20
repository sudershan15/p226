package gash.hibernate.app;

import gash.hibernate.data.FamilyMember;
import gash.hibernate.data.Person;
import gash.hibernate.util.GeneratePeople;
import gash.hibernate.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Populate extends People {

	public static void main(String[] args) {
		System.out.println("Loading data to pplhib");
		Populate p = new Populate();
		p.bulkGenerate(5);
	}

	public void bulkGenerate(int count) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			GeneratePeople gen = new GeneratePeople();
			Transaction tx = session.beginTransaction();
			for (int n = 0; n < count; n++) {
				Person p = gen.createUser();
				session.save(p);
				if (n % 100 == 0) {
					session.flush();
					session.clear();
				}
			}
			
			for ( int n = 0 ;n < 5 ; n++){
				FamilyMember fm = gen.createFM();
				session.save(fm);
			}
			
			tx.commit();
		} finally {
			// session.close();
		}
	}
}
