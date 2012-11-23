package gash.indexing;

import java.util.ArrayList;
import java.util.List;

/**
 * keyword counting and relative position within a document. This will be used
 * for determining distances and clustering
 * 
 * @author gash1
 * 
 */
public class KeyWord {
	public String word;
	public List<Integer> position = new ArrayList<Integer>();

	public KeyWord(String word, int position) {
		this.word = word;
		add(position);
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(word).append(": ").append(position);
		return sb.toString();
	}
	public void add(int position) {
		this.position.add(position);
	}

	public double stdDev() {
		// shouldn't happen
		if (position.size() == 0)
			return 0;

		double sd = 0.0f;

		double ave = 0.0;
		for (Integer n : position)
			ave += n;
		ave /= position.size();

		for (Integer n : position)
			sd += Math.pow((ave - n), 2);
		sd /= position.size();

		return Math.sqrt(sd);
	}
}
