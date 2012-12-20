package gash.hibernate.app;

import gash.hibernate.data.Person;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PeopleQueryTest {

	@Before
	public void before() {
		System.out.println("\n-----------------------------------------------------");
	}

	@Test
	public void testFindById() {
		People ppl = new People();
		Person p = ppl.find(new Long(1));
		Assert.assertNotNull(p);
		System.out.println("\nfind() found: " + p);
	}
}
