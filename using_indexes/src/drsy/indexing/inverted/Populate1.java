package drsy.indexing.inverted;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import drsy.indexing.Book;
import drsy.indexing.Document;
import drsy.indexing.Insertion;
import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;
import drsy.stemming.Stemmer;

public class Populate1 {

	private ArrayList<String> files = new ArrayList<String>();
	ArrayList<Book> books = new ArrayList<Book>();
	private StopWords ignore;

	public Populate1(StopWords stopWords) throws Exception {
		ignore = stopWords;
	}

	public void listFilesForFolder(final File folder) {
		System.out.println(folder);
		for (final File fileEntry : folder.listFiles()) {
			if(!fileEntry.getName().contains(".DS_Store")){
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {
					//System.out.println(fileEntry.getName());
					files.add(folder + "\\" +fileEntry.getName());
				}
			}
		}
	}

	public List<Document> bulkGenerate(String path) throws IOException {
		Insertion i = new Insertion();
		List<Document> docs = new ArrayList<Document>();
		final File folder = new File(path);
		listFilesForFolder(folder);
		for (String f: files) {
			Book b = new Book();
			Scanner sc;
			String[] stddev = null;
			File file = new File(path, f);
			Document d = new Document(file);
			file.getAbsoluteFile();
			i.insert("Name", f, f);
			b.setBook_name(f);
			int relPosition = 0;
			//System.out.println();
			String[] parmOrder = null;
			int contents = 0;
			int prbcount = 0;
			long numWords = 0;
			StringBuilder strbld = new StringBuilder();
			FileInputStream fsStream = new FileInputStream(f);
			DataInputStream diStream = new DataInputStream(fsStream);
			BufferedReader biStream = new BufferedReader(new InputStreamReader(diStream));
			String strLine;
			while((strLine = biStream.readLine()) != null)
			{
				if (strLine.trim().startsWith("Title:"))
				{
					String[] master = strLine.trim().split(":");
					//System.out.println("Title: " + master[1].trim());
					b.setTitle(master[1].trim());
					i.insert("Title", master[1].trim(), f);
				}
				else if (strLine.trim().startsWith("Author:"))
				{
					String[] master = strLine.trim().split(":");
					if(master.length > 1) {
						//System.out.println("Author: " + master[1].trim());
						b.setAuthor(master[1].trim());
						i.insert("Author", master[1].trim(), f);
					}
				}
				else if (strLine.trim().startsWith("Release Date"))
				{
					String[] master = strLine.trim().split(":");
					//System.out.println("Release Date: " + master[1].trim());
					b.setRelease_date(master[1].trim());
					i.insert("ReleaseDate", master[1].trim(), f);
				}
				else if (strLine.trim().startsWith("Posting Date"))
				{
					String[] master = strLine.trim().split(":");
					//System.out.println("Posting Date: " + master[1].trim());
					b.setPosting_date(master[1].trim());
					i.insert("PostingDate", master[1].trim(), f);
				}
				else if (strLine.trim().startsWith("Language:"))
				{
					String[] master = strLine.trim().split(":");
					//System.out.println("Language: " + master[1].trim());
					b.setLanguage(master[1].trim());
					i.insert("Language", master[1].trim(), f);
				}
				else if (strLine.trim().startsWith("Translator:"))
				{
					String[] master = strLine.trim().split(":");
					if(master.length > 1){
						//System.out.println("Translator: " + master[1].trim());
						b.setTranslator(master[1].trim());
						i.insert("Translator", master[1].trim(), f);
					}
				}
				else if (strLine.trim().startsWith("***")) { ;	}
				else if (strLine.trim().isEmpty()) { ; }
				else if (strLine.trim().startsWith("Produced by"))
				{
					if(prbcount == 0) {
						String[] master = strLine.trim().split("by");
						//System.out.println("Produced by: " + master[1].trim());
						b.setProduced_by(master[1].trim());
						i.insert("ProducedBy", master[1].trim(), f);
						prbcount++;
					}

				}
				else
				{
					strLine = strLine.replaceAll("\\s+", " ");
					strbld.append(strLine);
					String[] parts = strLine.toString().trim().split(
							"[\\s,\\.:;\\-#~\\(\\)\\?\\!\\&\\*\\\"\\/\\'\\`]");
					//System.out.println("---------------->"+st1.toString());
					//String[] parts = st1.toString().trim().split(",");
					numWords += parts.length;

					for (String p : parts) {
						if (!ignore.contains(p)){
							//System.out.println(p);
							d.addKeyword(p, relPosition);
						}
						// location (word position) in document allows use to
						// calculate strength by relative location and frequency
						relPosition++;
					}
				}
				
			}
			d.setNumWords(numWords);
			docs.add(d);
			String contents1 = strbld.toString();
			b.setContent(contents1);
			i.insert("Content", strbld.toString(), f);
			books.add(b);
			biStream.close();

		}
		return docs;
		//			char[] w = new char[501];
		//		      Stemmer s = new Stemmer();
		//		      StringBuilder st1 = new StringBuilder();
		//			File ff = new File("contents.txt");
		//			if(!ff.exists()){
		//    			ff.createNewFile();
		//    		}
		// 
		//    		//true = append file
		//    		FileWriter fileWritter = new FileWriter(ff.getName(),true);
		//    	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		//    	    bufferWritter.write(strbld.toString());
		//    	    bufferWritter.close();
		//    	    try
		//    	      {
		//    	         FileInputStream in = new FileInputStream("contents.txt");
		//
		//    	         try
		//    	         { while(true)
		//
		//    	           {  int ch = in.read();
		//    	              if (Character.isLetter((char) ch))
		//    	              {
		//    	                 int j = 0;
		//    	                 while(true)
		//    	                 {  ch = Character.toLowerCase((char) ch);
		//    	                    w[j] = (char) ch;
		//    	                    if (j < 500) j++;
		//    	                    ch = in.read();
		//    	                    if (!Character.isLetter((char) ch))
		//    	                    {
		//    	                       for (int c = 0; c < j; c++) s.add(w[c]);
		//    	                       s.stem();
		//    	                       {  String u;
		//    	                          u = s.toString();
		//    	                          st1.append(u);
		//    	                       }
		//    	                       break;
		//    	                    }
		//    	                 }
		//    	              }
		//    	              if (ch < 0) break;
		//    	              //st1.append(ch+ " ");
		//    	           }
		//    	         }
		//    	         catch (IOException e)
		//    	         {  System.out.println("error reading " + "contents.txt");
		//    	            break;
		//    	         }
		//    	      }
		//    	      catch (FileNotFoundException e)
		//    	      {  System.out.println("file " + "contents.txt" + " not found");
		//    	         break;
		//    	      }
		//	       


	}

	public List<Document> load(String path) throws Exception {
		List<Document> b = bulkGenerate(path);
		return b;
	}
}
