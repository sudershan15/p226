package gash.json.engine;

import gash.json.data.Person;

import java.util.List;

public interface Engine {

	/**
	 * open/enable the collection
	 * 
	 * @param collection
	 */
	public abstract void open();

	/**
	 * release resources of the collection - this will shutdown the collection.
	 * To use the collection again, you will need to open() the collection again
	 */
	public abstract void close();

	/**
	 * current collection
	 * 
	 * @return
	 */
	public abstract String getCollectionName();

	/**
	 * ideally this needs to be abstracted to a byte data. however, with a
	 * template we don't know what data to index
	 * 
	 * @param p
	 * @return the UUID of the stored person instance
	 */
	public abstract String create(Person p);

	public abstract List<Person> find(Person like);

	/**
	 * find by ID
	 * 
	 * @param id
	 * @return
	 */
	public abstract Person read(String id);

	/**
	 * save an previously saved person instance
	 * 
	 * @param p
	 */
	public abstract void update(Person p);

	/**
	 * delete the person
	 * 
	 * @param p
	 */
	public abstract void delete(Person p);

}