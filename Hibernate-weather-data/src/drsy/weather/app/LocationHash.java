package drsy.weather.app;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import drsy.weather.util.HibernateUtil;
import drsy.weather.util.HibernateUtil.Shard;

public class LocationHash {
	private int numNodes;

	public LocationHash(int numNodes) {
		this.numNodes = numNodes;
	}

	protected int locateIndex(String name) {
		int c = name.charAt(0);
		return c % numNodes;
	}

	public Session[] allLocations() {
		SessionFactory list[] = HibernateUtil.getLocations();
		Session[] r = new Session[list.length];
		for (int n = 0; n < list.length; n++)
			r[n] = list[n].getCurrentSession();

		return r;
	}

	public Session locate(String name) {
		Shard shard = locateIndex(name) == 0 ? Shard.one : Shard.two;
		System.out.println("----> " + name + " maps to " + shard);
		return HibernateUtil.getSessionFactory(shard).getCurrentSession();
	}
}
