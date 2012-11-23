package gash.indexing.inverted;

import gash.indexing.Document;
import gash.indexing.Insertion;
import gash.indexing.stopwords.StopWords;
import gash.indexing.stopwords.StopWordsFile;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class LoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@SuppressWarnings("null")
	@Test
	public void testLoadDocs() throws Exception {
		File swf = new File("resources/stopwords.txt");
		StopWords swords = new StopWordsFile(swf);
		Insertion i = new Insertion();
		Scanner s;
		String[] stddev = null;
		File dir = new File("C:/Users/smalpani/Desktop/subjects/226/data2");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);
		//Assert.assertEquals("mismatch in number of files", 4, docs.size());
		System.out.println(docs.size());
		for (Document d : docs) {
			System.out.println(" -- "+ d.csvHeader() + " -- " + d.getLocation() );
			System.out.println(d.csvData());
			s = new Scanner(d.csvData());
			s.useDelimiter(",");

			do {
				String key1 = s.next();
				String[] p = null;
				int maxlength = 0;
				if(key1.contains("\n")) {
					p = key1.split("\n");
					maxlength = p.length - 1;
					//System.out.println("HERE!" + p.toString() + "   " + p.length + "  " + p[2]);
				} else {
					System.out.println("DKFA" + key1);
					p[maxlength] = key1;
				}
				if (s.hasNext()) {
				String frq = s.next();
				String mean = s.next();
				String st = s.next();

				if(st.contains("\n")) {
					stddev = st.split("\n");
					System.out.println(p[maxlength] + "----------------" + frq + "----------------" + mean  + " ----- " + stddev[0]);
					i.insert(p[maxlength], frq, mean, stddev[0], d.getName(), d.getNumWords());
					if(stddev.length > 1 && stddev[1] != null){ 
						String sw = s.next(),h = s.next();
						System.out.println(stddev[1] + "----------------" + sw + "----------------" + h );
						i.insert(stddev[1], sw, h, d.getName(), d.getNumWords());
					}
				} else {
					i.insert(key1, frq, mean, st, d.getName(), d.getNumWords());
				}}
			} while (s.hasNextLine());


			/*s1.nextLine();
			while (s1.nextLine() != null) {
				String key1 = s1.next();
				String frq = s1.next();
				String mean = s1.next();
				String st = s1.next();

				if(st.contains("\n")) {
				stddev = st.split("\n");
				System.out.println(key1 + "----------------" + frq + "----------------" + mean  + " ----- " + stddev[0]);
				i.insert(key1, frq, mean, stddev[0], d.getName(), d.getNumWords());
				if(stddev.length > 1 && stddev[1] != null) i.insert(stddev[1], s1.next(), s1.next(), d.getName(), d.getNumWords());
				}
				else {
					i.insert(key1, frq, mean, st, d.getName(), d.getNumWords());
				}
			}*/

			/*while((d.csvData()) != null)
			{
				{
					StringTokenizer st = new StringTokenizer(d.csvData(),",");
					String stname = st.nextToken().trim();
					String freq = st.nextToken().trim();
					String mean = st.nextToken().trim();
					String stddev = st.nextToken().trim();
					System.out.println(stname + " -- " + freq + " -- " + mean + " -- " + stddev +" -- "+ d.getName()+" -- "+d.getNumWords());
				}
			}*/
		}

	}

	@Test
	@Ignore
	public void testLoadLyricsOne() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics/taylor-swift.txt");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);
		Assert.assertEquals("mismatch in number of files", 1, docs.size());

		for (Document d : docs)
			System.out.println(d + "\n");
	}

	@Test
	@Ignore
	public void testLoadLyricsAll() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);

		// Ultimate Answer to the Ultimate Question of Life, the Universe, and
		// Everything
		Assert.assertEquals("mismatch in number of files", 42, docs.size());

		for (Document d : docs)
			System.out.println(d + "\n");
	}

	@Test
	@Ignore
	public void testLyricCompare() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/lyrics");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);

		for (int n = 0, N = docs.size(); n < N; n++) {
			Document d = docs.get(n);
			for (Document d2 : docs) {
				double rank = d.similarity(d2);
				if (d != d2 && rank > 0.3)
					System.out.println(d.getName() + " to " + d2.getName() + " matches " + rank);
			}
		}
	}

	@Test
	@Ignore
	public void testLoadDocsAll() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/docs");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);

		Assert.assertEquals("mismatch in number of files", 5, docs.size());

		for (Document d : docs)
			System.out.println(d + "\n");
	}

	@Test
	@Ignore
	public void testLoadDocsAllCSV() throws Exception {
		File swf = new File("resources/stopwords-long.txt");
		StopWords swords = new StopWordsFile(swf);
		File dir = new File("testdata/docs");
		Loader ldr = new Loader(swords);
		List<Document> docs = ldr.load(dir);

		Assert.assertEquals("mismatch in number of files", 5, docs.size());

		for (Document d : docs) {
			System.out.println(d.csvHeader());
			System.out.println(d.csvData());
			System.out.println("\n\n\n\n");
		}

	}
}
