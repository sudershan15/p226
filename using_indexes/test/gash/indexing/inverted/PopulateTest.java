package gash.indexing.inverted;

import gash.indexing.Book;
import gash.indexing.Document;
import gash.indexing.Insertion;
import gash.indexing.stopwords.StopWords;
import gash.indexing.stopwords.StopWordsFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import drsy.stemming.Stemmer;

public class PopulateTest {

	public PopulateTest() {
		// TODO Auto-generated constructor stub
	}

	static Properties conf = new Properties();
	String path_of_files = "path.files";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		InputStream is = new FileInputStream("resources/setup.properties");
		conf.load(is);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadDocs() throws Exception {
		File swf = new File("resources/stopwords.txt");
		StopWords swords = new StopWordsFile(swf);
		Insertion i = new Insertion();
		//Stemmer s = new Stemmer();
		
		String dir = conf.getProperty(path_of_files);
		Populate1 ldr = new Populate1(swords);
		char[] w = new char[501];
		List<Document> docs = ldr.load(dir);
		Scanner s;
		String[] stddev = null;
		//Assert.assertEquals("mismatch in number of files", 565, docs.size());
		//System.out.println(docs.size());
		for (Document d : docs) {
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
						System.out.println("HERE!" + p.toString() + "   " + p.length + "  " + p[maxlength]);
					} else {
						System.out.println("DKFA" + key1);
						p[maxlength] = key1;
					}
					if (s.hasNext()) {
						String frq = s.next();
						String mean = s.next();
						String st = s.next();
						System.out.println();
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
						}
					}
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
}


	//	}

	//}

