package gash.json.engine.indexing;

import gash.json.data.Person;

import java.util.List;

public interface IndexEngine {

	void close();

	void addIndexes(Person p);

	void removeIndexes(Person p);

	/**
	 * Union search returns the IDs of matches
	 */
	List<String> find(Person criteria);

}