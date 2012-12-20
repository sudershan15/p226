package gash.json.engine.indexing;

import gash.json.data.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * test/example - this is not MT safe!
 * 
 * @author gash1
 * 
 */
public class MemoryIndexing implements IndexEngine {

	private HashMap<String, List<String>> mapLN = new HashMap<String, List<String>>();
	private HashMap<String, List<String>> mapR = new HashMap<String, List<String>>();

	public void close() {
	}

	public void addIndexes(Person p) {
		if (p == null)
			return;

		String key = p.getLastName().toLowerCase();
		List<String> list = mapLN.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			mapLN.put(key, list);
		}
		list.add(p.getId());

		if (p.getRole() != null) {
			key = p.getRole().toLowerCase();
			list = mapR.get(key);
			if (list == null) {
				list = new ArrayList<String>();
				mapR.put(key, list);
			}
			list.add(p.getId());
		}
	}

	public void removeIndexes(Person p) {
		String key = p.getLastName().toLowerCase();
		List<String> list = mapLN.get(key);
		if (list != null) {
			list.remove(key);
		}

		key = p.getLastName().toLowerCase();
		list = mapR.get(key);
		if (list != null) {
			list.remove(key);
		}
	}

	/**
	 * OR-ing search returns the IDs of matches
	 */
	public List<String> find(Person criteria) {
		// only a subset of fields are supported
		if (criteria == null || (criteria.getLastName() == null && criteria.getRole() == null))
			return null;

		List<String> r = new ArrayList<String>();

		if (criteria.getLastName() != null)
			searchMap(r, mapLN, criteria.getLastName().toLowerCase());

		if (criteria.getRole() != null)
			searchMap(r, mapR, criteria.getRole().toLowerCase());

		return r;
	}

	/**
	 * debugging only!
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LastName map:\n");
		sb.append(mapLN);
		sb.append("\nRole map:\n");
		sb.append(mapR);

		return sb.toString();
	}

	private void searchMap(List<String> rtn, HashMap<String, List<String>> map, String value) {
		for (String k : map.keySet()) {
			if (k.indexOf(value) != -1) {
				for (String id : map.get(k)) {
					if (!rtn.contains(id))
						rtn.add(id);
				}
			}
		}
	}
}
