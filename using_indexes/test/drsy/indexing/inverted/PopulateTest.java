package drsy.indexing.inverted;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import drsy.indexing.Document;
import drsy.indexing.Insertion;
import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;

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
	@Ignore
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
					//System.out.println("HERE!" + p.toString() + "   " + p.length + "  " + p[maxlength]);
				} else {
					//System.out.println("DKFA" + key1);
					p[maxlength] = key1;
				}
				if (s.hasNext()) {
					String frq = s.next();
					String mean = s.next();
					String st = s.next();
					System.out.println();
					if(st.contains("\n")) {
						stddev = st.split("\n");
						//	System.out.println(p[maxlength] + "----------------" + frq + "----------------" + mean  + " ----- " + stddev[0]);
						i.insert(p[maxlength], frq, mean, stddev[0], d.getName(), d.getNumWords());
						if(stddev.length > 1 && stddev[1] != null){ 
							String sw = s.next(),h = s.next();
							//				System.out.println(stddev[1] + "----------------" + sw + "----------------" + h );
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

	@Test
	@Ignore
	public void testSearchBookFromKeyword() throws Exception {
		String keyword = conf.getProperty("search.keyword");
		Insertion ins = new Insertion();
		String k = ins.get(keyword);
		String books[] = k.split("\n"); 
		int lenght = books.length;
		//System.out.println("------->"+k+".");
		while(lenght > 1) {
			if(!books[lenght-1].trim().equals(null) || !books[lenght-1].trim().isEmpty() || books[lenght-1].trim() != "") {
				String[] bookname = books[lenght-1].split(",");
				System.out.println(bookname[1]);
				System.out.println(ins.search(conf.getProperty(path_of_files)+"\\"+bookname[1].trim()));
			}
			lenght--;
			System.out.println();
		}
	}

	@Test
	public void getBooksbyRank() throws Exception {
		String keyword = conf.getProperty("search.keyword");
		Insertion ins = new Insertion();
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		String k = ins.get(keyword);
		String books[] = k.split("\n"); 
		int lenght = books.length;
		while(lenght > 1) {
			if(!books[lenght-1].trim().equals(null) || !books[lenght-1].trim().isEmpty() || books[lenght-1].trim() != "") {
				String[] bookname = books[lenght-1].split(",");
				map.put(bookname[1], Double.parseDouble(bookname[2].trim()));
			}
			lenght--;
		}
		for(String m:map.descendingMap().keySet())
			System.out.println(ins.search(conf.getProperty(path_of_files)+"\\"+m.trim()));
	}

}


