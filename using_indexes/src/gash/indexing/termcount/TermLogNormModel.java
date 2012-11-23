package gash.indexing.termcount;

import gash.indexing.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Improving the term count model to dampen the effect of the frequency
 * algorithm
 * 
 * 1) build a matrix of words (rows) counts (Cij) across each document (columns)
 * 
 * @author gash
 * 
 */
public class TermLogNormModel {
	private ArrayList<Document> docs = new ArrayList<Document>();
	private ArrayList<String> words; // word index order for rows
	private float[][] termdoc;
	private float[] edst;
	private boolean initialized = false;

	public TermLogNormModel() {
	}

	public void addDocument(Document doc) {
		initialized = false;
		docs.add(doc);
	}

	/**
	 * search and return the bestOf (top N) that match the keywords
	 * 
	 * @param keywords
	 * @param bestOf
	 * @return
	 */
	public List<TermRank> search(List<String> keywords, int bestOf) {
		if (keywords == null || keywords.size() == 0)
			return null;

		if (!initialized)
			createModel();

		// calculate query matrix and its eculidean distance
		Set<String> set = new HashSet<String>(keywords);
		float[] qt = new float[termdoc.length];
		float edstQ = 0.0f;
		for (int i = 0, I = qt.length; i < I; i++) {
			if (set.contains(words.get(i)))
				qt[i] = 1.0f;
			else
				qt[i] = 0;

			edstQ += Math.pow(qt[i], 2);
		}
		edstQ = (float) Math.sqrt(edstQ);

		// dot product of qt and termdoc
		float[] dotp = new float[termdoc.length];
		for (int j = 0, J = docs.size(); j < J; j++) {
			for (int i = 0, I = words.size(); i < I; i++) {
				dotp[j] += qt[i] * termdoc[i][j];
			}
		}

		// calculate the term vector w/ results of the search
		float[] tv = new float[docs.size()];
		HashMap<Float, Document> results = new HashMap<Float, Document>();
		for (int j = 0, J = tv.length; j < J; j++) {
			float fp = edstQ * edst[j];
			if (fp == 0.0f)
				tv[j] = 0;
			else
				tv[j] = ((float) dotp[j]) / fp;
			results.put(tv[j], docs.get(j));
		}

		// get the request N best answers
		List<TermRank> rtn = new ArrayList<TermRank>();
		List<Float> sortedKeys = new ArrayList<Float>(results.keySet());
		Collections.sort(sortedKeys, Collections.reverseOrder());
		int cnt = 0, max = sortedKeys.size();
		for (Float key : sortedKeys) {
			// System.out.println("K: " + key);
			TermRank tr = new TermRank(results.get(key).getName(),
					(int) (key * 100));
			rtn.add(tr);
			cnt++;
			if (cnt == bestOf || cnt >= max)
				break;
		}

		return rtn;
	}

	private synchronized void createModel() {
		if (initialized)
			return;

		long st = System.currentTimeMillis();

		// create a unique list of keywords
		Set<String> unique = new HashSet<String>();
		for (Document d : docs)
			unique.addAll(d.keys());

		words = new ArrayList<String>(unique);
		Collections.sort(words);

		// raw values
		termdoc = new float[words.size()][docs.size()];
		for (int i = 0, I = words.size(); i < I; i++) {
			for (int j = 0, J = docs.size(); j < J; j++) {
				termdoc[i][j] = docs.get(j).count(words.get(i));
			}
		}

		// calc average number of terms per doc
		int c = 0;
		for (int j = 0, J = docs.size(); j < J; j++) {
			for (int i = 0, I = words.size(); i < I; i++) {
				c += termdoc[i][j];
			}
		}
		float avt = c / docs.size();
		System.out.println("avt: " + avt);

		// adjust by log(n)/log(fd)
		for (int i = 0, I = words.size(); i < I; i++) {
			for (int j = 0, J = docs.size(); j < J; j++) {
				if (termdoc[i][j] != 0)
					termdoc[i][j] = (float) (Math.log10(termdoc[i][j]) / Math
							.log10(avt));
			}
		}

		// calculate the euclidean distance (frobenius norms): (a^2+b^2)^1/2
		edst = new float[docs.size()];
		for (int j = 0, J = termdoc[0].length; j < J; j++) {
			for (int i = 0, I = termdoc.length; i < I; i++) {
				edst[j] += Math.pow(termdoc[i][j], 2);
			}
		}

		for (int i = 0, I = edst.length; i < I; i++)
			edst[i] = (float) Math.sqrt(edst[i]);

		// we are ready for searches
		initialized = true;

		System.out.println("created Term Model of (" + words.size() + " x "
				+ docs.size() + ") in " + (System.currentTimeMillis() - st)
				+ " msec");
	}
}
