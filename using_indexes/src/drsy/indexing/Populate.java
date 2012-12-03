package drsy.indexing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Populate {

	private ArrayList<String> files = new ArrayList<String>();

	public Populate() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		String path = "C:/Users/smalpani/Desktop/subjects/226";
		Populate p = new Populate();
		p.bulkGenerate(path);
	}

	public void listFilesForFolder(final File folder) {
		System.out.println(folder);
		for (final File fileEntry : folder.listFiles()) {
			if(!fileEntry.getName().contains(".DS_Store")){
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {
					//System.out.println(fileEntry.getName());
					files.add(folder + "/" + fileEntry.getName());
				}
			}
		}
	}


	public void bulkGenerate(String path) throws IOException {

		final File folder = new File(path);
		listFilesForFolder(folder);
		for (String f: files) {
			System.out.println();
			String[] parmOrder = null;
			int contents = 0;
			int prbcount = 0;
			StringBuilder strbld = new StringBuilder();
			FileInputStream fsStream = new FileInputStream(f);
			DataInputStream diStream = new DataInputStream(fsStream);
			BufferedReader biStream = new BufferedReader(new InputStreamReader(diStream));
			String strLine;
			while((strLine = biStream.readLine()) != null)
			{
				if (strLine.trim().startsWith("Title"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Title: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("Author"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Author: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("Release Date"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Release Date: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("Posting Date"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Posting Date: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("Language"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Language: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("Translator"))
				{
					String[] master = strLine.trim().split(":");
					System.out.println("Translator: " + master[1].trim());
				}
				else if (strLine.trim().startsWith("***")) { ;	}
				else if (strLine.trim().isEmpty()) { ; }
				else if (strLine.trim().startsWith("Produced by"))
				{
					if(prbcount == 0) {
						String[] master = strLine.trim().split("by");
						System.out.println("Produced by: " + master[1].trim());
						prbcount++;
					}
					
				}
				else
				{
					//strLine = strLine.replaceAll("\\s+", " ");
					
					//strbld.append(strLine);
					//final StringTokenizer token = new StringTokenizer(strLine, " ");
					//final List<String> col = new ArrayList<String>();
					/*while(token.hasMoreTokens())
						System.out.print(token.nextToken() + " ");*/
					//contents += token.countTokens();
					;
				}
				
			}
			
			//System.out.println(" -- contents: " + strbld.length());
			biStream.close();
		}
		


	}
}
