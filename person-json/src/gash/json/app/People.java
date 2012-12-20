package gash.json.app;

import gash.json.data.Person;
import gash.json.engine.Engine;
import gash.json.engine.JsonEngine;
import gash.json.engine.XmlEngine;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample application to demonstrate how we can provide storage through a
 * file-based solution
 * 
 * @author gash
 * 
 */
public class People {
	protected Logger log = LoggerFactory.getLogger(JsonEngine.class);

	private Engine engine;

	public People(Properties conf) {
		throw new RuntimeException ("You need to choose which encoding to use!");
		//engine = new JsonEngine(conf);
		//engine = new XmlEngine(conf);
	}

	/**
	 * find a person by their ID
	 * 
	 * @param id
	 * @return
	 */
	public Person find(String id) {
		return engine.read(id);
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
	public List<Person> find(Person template) {
		return engine.find(template);
	}

	/**
	 * demonstration of HQL and setting the fetch depth of the graph
	 * 
	 * @param template
	 * @return
	 */
	public List<Card> findPhoneNumbersByRole(String role) {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * demonstration of HQL and retrieving associations using joins
	 * 
	 * @param template
	 * @return
	 */
	public List<Person> findEmailByRole(String role) {
		throw new RuntimeException("Not implemented");
	}

	public void createPerson(Person p) {
		if (!validate(p))
			throw new RuntimeException("Invalid person");

		String id = engine.create(p);
		p.setId(id);
	}

	public void updatePerson(Person p) {
		if (!validate(p) || p.getId() == null)
			throw new RuntimeException("Invalid person");

		engine.update(p);
	}

	public void removePerson(Person p) {
		if (p == null)
			return;

		engine.delete(p);
	}

	protected boolean validate(Person p) {
		if (p == null)
			return false;

		// TODO validate values
		return true;
	}
}
