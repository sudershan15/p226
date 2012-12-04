package drsy.indexing.termcount;

import drsy.indexing.Document;
import drsy.indexing.termcount.TermCountModel;
import drsy.indexing.termcount.TermRank;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import drsy.indexing.inverted.Loader;
import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;

public class TermCountModelTest {
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
	public void testTermModelBuilding() throws Exception {
		TermCountModel tcm = new TermCountModel();
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