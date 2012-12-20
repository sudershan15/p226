package gash.hibernate.app;

import gash.hibernate.data.Contact;
import gash.hibernate.data.Person;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * query test (line 29): to run you will need to know an ID of one of your
 * person rows
 * 
 * @author gash
 * 
 */
public class PeopleQueryTest {

	@Before
	public void before() {
		System.out.println("\n-----------------------------------------------------");
	}

	@Test
	public void testFindById() {
		People ppl = new People();
		Person p = ppl.find(new Long(40271));
		Assert.assertNotNull(p);

		System.out.println(p);

		List<Contact> contacts = ppl.findContacts(p.getId());
		if (contacts != null) {
			for (Contact c : contacts)
				System.out.println("   -- " + c);
		}
	}

}
