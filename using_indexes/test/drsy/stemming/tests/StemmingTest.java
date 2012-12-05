package drsy.stemming.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import drsy.stemming.Stemmer;

public class StemmingTest {

	private ArrayList<String> files = new ArrayList<String>();

	public StemmingTest() {
		// TODO Auto-generated constructor stub
	}

	public void listFilesForFolder(final File folder) {
		System.out.println(folder);
		int i = 0;
		for (final File fileEntry : folder.listFiles()) {
			if(!fileEntry.getName().contains(".DS_Store")){
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {
					//System.out.println(fileEntry.getName());
					String path = folder + "\\" +fileEntry.getName()+"\n";
					
					files.add(folder + "\\" +fileEntry.getName());
					//System.out.println(path);
					i++;
				}
			}
		}
	}

	@Test
	public void stemmingTest()
	{
		String f = "C:\\Users\\smalpani\\Desktop\\subjects\\226\\data2";
		//System.out.println(f);
		
		final File folder = new File(f);
		listFilesForFolder(folder);
		Object[] doclist = files.toArray();
		//for (String f1: files) {
			//String[] doclist = {};
			char[] w = new char[501];
			Stemmer s = new Stemmer();
			for (int i = 0; i < doclist.length; i++)
				try
			{
					FileInputStream in = new FileInputStream((String) doclist[i]);

					try
					{ while(true)

					{  int ch = in.read();
					if (Character.isLetter((char) ch))
					{
						int j = 0;
						while(true)
						{  ch = Character.toLowerCase((char) ch);
						w[j] = (char) ch;
						if (j < 500) j++;
						ch = in.read();
						if (!Character.isLetter((char) ch))
						{
							/* to test add(char ch) */
							for (int c = 0; c < j; c++) s.add(w[c]);

							/* or, to test add(char[] w, int j) */
							/* s.add(w, j); */

							s.stem();
							{  String u;

							/* and now, to test toString() : */
							u = s.toString();

							/* to test getResultBuffer(), getResultLength() : */
							/* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */

							System.out.print(u);
							}
							break;
						}
						}
					}
					if (ch < 0) break;
					System.out.print((char)ch);
					}
					}
					catch (IOException e)
					{  System.out.println("error reading " + doclist[i]);
					break;
					}
			}
			catch (FileNotFoundException e)
			{  System.out.println("file " + doclist[i] + " not found");
			break;
			}
		}
	//}
}