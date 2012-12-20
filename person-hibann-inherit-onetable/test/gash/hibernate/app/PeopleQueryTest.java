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

	@Test
	public void testOne() {
		People ppl = new People();

		Person template = new Person();
		template.setId(new Long(1));
		List<Person> list = ppl.find(template);
		Assert.assertNotNull(list);
		System.out.println("\nfound " + list.size() + " results");
		for (Person p : list)
			System.out.println(p);
	}

	@Test
	public void testCriteria() {
		People ppl = new People();

		Person template = new Person();
		template.setRole("Friend");
		List<Person> list = ppl.find(template);
		Assert.assertNotNull(list);
		System.out.println("\nfound " + list.size() + " results");
		for (Person p : list)
			System.out.println(p);
	}

	@Test
	public void testQueryHQL() {
		People ppl = new People();
		List<Card> list = ppl.findPhoneNumbersByRole("Friend");
		Assert.assertNotNull(list);
		System.out.println("\nrole search found " + list.size() + " results");
		for (Card p : list)
			System.out.println(p);
	}

	@Test
	public void testQueryJoin() {
		People ppl = new People();
		List<Person> list = ppl.findEmailByRole("Friend");
		Assert.assertNotNull(list);
		System.out.println("\nemail search found " + list.size() + " results");
		for (Person p : list)
			System.out.println(p);
	}
}
