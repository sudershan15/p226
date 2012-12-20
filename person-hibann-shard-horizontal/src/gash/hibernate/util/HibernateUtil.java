package gash.hibernate.util;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	public enum Shard {
		one, two
	};

	private static final SessionFactory sessionFactory[];

	static {
		sessionFactory = new SessionFactory[2];
		sessionFactory[0] = buildSessionFactory1();
		sessionFactory[1] = buildSessionFactory2();
	}

	public static SessionFactory getSessionFactory(Shard shard) {
		if (shard == Shard.one)
			return sessionFactory[0];
		else
			return sessionFactory[1];
	}

	public static SessionFactory[] getLocations() {
		return sessionFactory;
	}

	private static SessionFactory buildSessionFactory1() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static SessionFactory buildSessionFactory2() {
		try {
			// TODO need to pass the location where the property file is
			// located!
			File cfgF = new File("/Users/gash/workspace/databases/person-hibann-shard-horizontal/resources2/hibernate.cfg.xml");
			Configuration cfg = new Configuration();
			cfg.configure(cfgF);
			return cfg.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

}