package gash.proto.app;

import gash.proto.data.Person;
import gash.proto.engine.ProtoConf;
import gash.proto.util.GeneratePeople;

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
		cfg.setProperty(ProtoConf.sBaseDir, "/opt/226/ppl");
		cfg.setProperty(ProtoConf.sIndexEngine, "gash.proto.engine.indexing.MemoryIndexing");
		cfg.setProperty(ProtoConf.sCollection, "test");

		ppl = new People(cfg);
	}

	@Test
	public void testLoadUsers() {
		final int count = 1000;

		ArrayList<Person> list = new ArrayList<Person>();
		GeneratePeople gen = new GeneratePeople();
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
