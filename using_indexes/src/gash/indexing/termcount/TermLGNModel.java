package gash.indexing.termcount;

import gash.indexing.Document;
import gash.indexing.KeyWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Improving the term count model to include local * global * normalized
 * 
 * TODO optimization/refactoring of the loops to perform multiple calculations
 * rather than a separate for-loop for each
 * 
 * @author gash
 * 
 */
public class TermLGNModel {
	private ArrayList<Document> docs = new ArrayList<Document>();
	private ArrayList<String> words; // word index order for rows
	private float[][] termdoc;
	private float[] edst;
	private float[] global;
	private float[] normalized;
	private boolean initialized = false;

	public TermLGNModel() {
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
			else {
				// N * G * L
				tv[j] = normalized[j] * global[j] * ((float) dotp[j]) / fp;
			}

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

	@SuppressWarnings("unused")
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
		boolean betterCounting = true;
		termdoc = new float[words.size()][docs.size()];
		for (int i = 0, I = words.size(); i < I; i++) {
			for (int j = 0, J = docs.size(); j < J; j++) {
				// naively count all words
				termdoc[i][j] = docs.get(j).count(words.get(i));
				if (betterCounting && docs.get(j).count(words.get(i)) > 1) {
					// consider distances between words
					int kwcnt = 1;
					KeyWord kw = docs.get(j).getWord(words.get(i));
					int lastpos = kw.position.get(0);
					for (Integer pos : kw.position) {
						// only count words if they are at least 3 positions
						// away
						if (pos - lastpos > 1) 
							kwcnt++;
						
						lastpos = pos;
					}
					termdoc[i][j] = kwcnt;
				}
			}
		}

		// global weighting (G)
		int[] hasterm = new int[termdoc.length];
		for (int j = 0, J = termdoc[0].length; j < J; j++) {
			for (int i = 0, I = termdoc.length; i < I; i++) {
				if (termdoc[i][j] > 0)
					hasterm[i] += 1;
			}
		}

		// the global weighting is a IDF (inverse frequency)

		// we can increase the dampening effect by increasing repository size
		global = new float[termdoc.length];
		for (int i = 0, I = termdoc.length, N = docs.size(); i < I; i++) {
			global[i] = (float) Math.log10((N - hasterm[i]) / hasterm[i]);
		}

		// normalized (N)
		float slope = 0.2f;
		float pivot = 0.0f;
		for (int i = 0, I = termdoc.length; i < I; i++)
			if (hasterm[i] > 0.0)
				pivot++;
		pivot = pivot / docs.size();

		normalized = new float[termdoc.length];
		for (int j = 0, J = docs.size(); j < J; j++) {
			normalized[j] = 1 / ((1 - slope) * pivot + slope * hasterm[j]);
		}

		// print our matrix
		if (1 == 0) {
			for (int i = 0, I = termdoc.length; i < I; i++) {
				System.out.print(String.format("%14s", words.get(i)));
				for (int j = 0, J = termdoc[0].length; j < J; j++) {
					System.out.print(String.format("%4d", (int) termdoc[i][j]));
				}
				System.out.println("");
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
