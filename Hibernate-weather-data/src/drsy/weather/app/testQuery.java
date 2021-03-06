package drsy.weather.app;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import drsy.weather.data.Network;
import drsy.weather.data.Station;
import drsy.weather.data.Wdata;
import drsy.weather.data.WdataKey;

public class testQuery {

	protected LocationHash locator;

	public testQuery() {
		// TODO node names, and number of shards (nodes) should be obtained from
		// a config file
		locator = new LocationHash(2);
	}
	
	public Network find(int id) {
		if (id == 0)
			return null;

		Network r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			r = (Network) session.load(Network.class, id);

			// forcing the proxy to load contacts - this not good as it costs us
			// another trip to the database
			if (r != null)
				r.getName();

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}

	public Station find(String id) {
		if (id.isEmpty())
			return null;

		Station r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			r = (Station) session.load(Station.class, id);

			// forcing the proxy to load contacts - this not good as it costs us
			// another trip to the database
			if (r != null)
				r.getName();

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}

	public Wdata find(WdataKey id) {
		if (id == null)
			return null;

		Wdata r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			r = (Wdata) session.load(Wdata.class, id);

			// forcing the proxy to load contacts - this not good as it costs us
			// another trip to the database
			if (r != null)
				r.getAlti();

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	public List<Wdata> find(float alti) {
		if (alti == 0.0)
			return null;

		List<Wdata> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			Query query = session.createQuery("from Wdata as p where p.alti = :alti");
			query.setDouble("alti", alti);

			// the join creates duplicate records - this will remove them
			Set<Wdata> set = new LinkedHashSet<Wdata>(query.list());
			r = new ArrayList<Wdata>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;	
	}

	public List<Station> findNetworks(int network) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			Query query = session.createQuery("from Station as s where s.network = :network");
			query.setDouble("network", network);
			Set<Station> set = new LinkedHashSet<Station>(query.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	public List<Station> findNetworks(Station template) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			c.add(Restrictions.eq("network", template.getNetwork()));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
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
	public List<Station> find(Station template) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			//c.setFetchMode("contacts", FetchMode.JOIN);

			//if (template.getId() != null)
			{
				c.add(Restrictions.idEq(template.getId()));
				c.setMaxResults(10);
			} 
			//else
			{
				/*if (template.getLastName() != null)
					c.add(Restrictions.like("firstname", template.getFirstName()));

				if (template.getLastName() != null)
					c.add(Restrictions.like("lastname", template.getLastName()));

				if (template.getRole() != null)
					c.add(Restrictions.like("role", template.getRole()));

				if (template.getNickName() != null)
					c.add(Restrictions.like("nickname", template.getNickName()));*/
			}

			//System.out.println("find() " + c.toString());

			// the join creates duplicate records - this will remove them
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}

	public List<Station> getStation(Station template) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			c.add(Restrictions.idEq(template.getId()));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	public List<Station> countStations(String country) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			c.add(Restrictions.ne("sskey.country", country));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	
	public List<Station> getDataonDate(int date, String state) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			String sql = "wdatas2_.date = " + date; 
			String sql1 = "wdatas2_.tmpf > -99";
			c.add(Restrictions.sqlRestriction(sql));
			c.add(Restrictions.sqlRestriction(sql1));
			c.add(Restrictions.eq("sskey.state", state));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	public List<Station> getStationsbetweenlatnlon(float lat1, float lat2, float lon1, float lon2) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			c.add(Restrictions.between("latitude", lat1, lat2));
			c.add(Restrictions.between("longitude", lon1, lon2));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	public List<Station> getStationsonDate(int date) {
		ArrayList<Station> r = null;
		Session[] slist = locator.allLocations();
		for (Session session : slist) {
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(Station.class);
			c.setFetchMode("wdata", FetchMode.JOIN);
			String sql = "wdatas2_.date = " + date; 
			String sql1 = "wdatas2_.tmpf > -99";
			c.add(Restrictions.sqlRestriction(sql));
			c.add(Restrictions.sqlRestriction(sql1));
			//c.add(Restrictions.eq("sskey.country", "US"));
			Set<Station> set = new LinkedHashSet<Station>(c.list());
			r = new ArrayList<Station>(set);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		}
		return r;
	}
	
	protected boolean validate(Station p) {
		if (p == null)
			return false;

		// TODO validate values
		return true;
	}
}
