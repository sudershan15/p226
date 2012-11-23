package gash.indexing.inverted;

import gash.indexing.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RegistryTest {
	static Registry data;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Properties conf = new Properties();
		conf.setProperty(Registry.sStopWords, "resources/stopwords-long.txt");
		conf.setProperty(Registry.sDataStorage, "TBD");
		conf.setProperty(Registry.sIndexStorage, "TBD");

		data = new Registry(conf);

		// load test data
		File dir = new File("C:/Users/smalpani/Desktop/subjects/226/data1");
		data.register(dir);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		data = null;
	}

	@Test
	public void testQuery() {
		List<String> words = new ArrayList<String>();
		words.add("love");
		//words.add("franciso");

		long st = System.nanoTime();
		List<Document> found = data.query(words);
		long et = System.nanoTime();

		System.out.println("---> found " + found.size() + " documents in " + (et - st) + " ns");
		for (Document d : found)
			System.out.println(d + "\n");
	}
	
}
