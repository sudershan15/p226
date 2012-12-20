package gash.proto.engine.indexing;

import gash.proto.data.Person;
import gash.proto.util.GeneratePeople;

import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class IndexMemoryTest {
	// in memory testing
	static HashMap<String, Person> ppl = new HashMap<String, Person>();

	@BeforeClass
	public static void before() {
		GeneratePeople gen = new GeneratePeople();
		int N = 100;
		for (int n = 0; n < N; n++) {
			Person p = gen.createUser();
			ppl.put(p.getId(), p);
		}
	}

	@Test
	public void testCriteriaSearch() {
		MemoryIndexing mi = new MemoryIndexing();
		for (Person p : ppl.values())
			mi.addIndexes(p);

		Person criteria = new Person();
		criteria.setRole("work");
		List<String> got = mi.find(criteria);
		System.out.println("\n---> query: role\n");
		for (String id : got) {
			Person p = ppl.get(id);
			System.out.println("- " + p.getLastName() + ", ID = " + p.getId());
		}

		criteria = new Person();
		criteria.setLastName(ppl.get(ppl.keySet().iterator().next()).getLastName());
		got = mi.find(criteria);
		System.out.println("\n---> query: " + criteria.getLastName() + "\n");
		for (String id : got) {
			Person p = ppl.get(id);
			System.out.println("- " + p.getLastName() + ", ID = " + p.getId());
		}

		System.out.println("\n---> maps:\n\n" + mi);
	}
}
