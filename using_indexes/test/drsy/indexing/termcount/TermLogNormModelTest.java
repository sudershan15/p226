package drsy.indexing.termcount;

import drsy.indexing.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import drsy.indexing.inverted.Loader;
import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;
import drsy.indexing.termcount.TermLogNormModel;
import drsy.indexing.termcount.TermRank;

public class TermLogNormModelTest {
	static List<Document> docs;

	@BeforeClass
	public static void loadData() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics");
		Loader ldr = new Loader(swords);
		docs = ldr.load(dir);
	}

	@Test
	public void testTermLogModelBuilding() throws Exception {
		TermLogNormModel tcm = new TermLogNormModel();
		for (Document d : docs)
			tcm.addDocument(d);

		List<String> words = new ArrayList<String>();
		words.add("love");

		long st = System.currentTimeMillis();
		List<TermRank> results = tcm.search(words, 45);
		long et = System.currentTimeMillis();

		System.out.println("\nSearch: " + words + " in " + (et - st) + " msec");
		for (TermRank tr : results)
			System.out.println("Doc: " + tr.name + " (rank " + tr.rank + ")");
	}
}
