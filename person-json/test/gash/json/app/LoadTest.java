package gash.json.app;

import gash.json.app.People;
import gash.json.data.Person;
import gash.json.engine.EngineConf;
import gash.json.util.GeneratePeople;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class LoadTest {
	public static String sBase = "dist";
	public static People ppl;

	@BeforeClass
	public static void setupCollection() {

		Properties cfg = new Properties();
		//cfg.setProperty(ProtoConf.sBaseDir, "/RAID1/tmp/ppl");
		cfg.setProperty(EngineConf.sBaseDir, "C:\226");
		//cfg.setProperty(EngineConf.sBaseDir, "/opt/226/ppl-xml");
		cfg.setProperty(EngineConf.sIndexEngine, "gash.json.engine.indexing.MemoryIndexing");
		cfg.setProperty(EngineConf.sCollection, "test");

		ppl = new People(cfg);
	}

	@Test
	public void testLoadUsers() {
		final int count = 1000;

		ArrayList<Person> list = new ArrayList<Person>();
		GeneratePeople gen = new GeneratePeople();
		
		// create test data
		for (int n = 0; n < count; n++)
			list.add(gen.createUser());

		long st = System.currentTimeMillis();
		for (Person p : list)
			ppl.createPerson(p);
		long et = System.currentTimeMillis();
		System.out.println("---> create took " + (et - st) + " msec, " + ((double) (et - st) / (double) count) + " msec/entry");

		st = System.currentTimeMillis();
		for (Person p : list)
			ppl.find(p.getId());
		et = System.currentTimeMillis();
		System.out.println("---> load took " + (et - st) + " msec, " + ((double) (et - st) / (double) count) + " msec/entry");

		// query new data by last name
		Person criteria = new Person();
		criteria.setLastName(list.get(0).getLastName());
		List<Person> got = ppl.find(criteria);
		System.out.println("\n---> query: " + criteria.getLastName() + "\n");
		for (Person p : got) {
			System.out.println("- " + p.getLastName() + ", ID = " + p.getId());
		}

	}
}
