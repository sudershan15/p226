package gash.indexing.inverted;

import gash.indexing.Document;
import gash.indexing.stopwords.StopWords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Loader {
	private StopWords ignore;
	private List<File> list = new ArrayList<File>();

	public Loader(StopWords stopWords) throws Exception {
		ignore = stopWords;
	}

	public List<Document> load(File f) {
		if (f == null)
			return null;

		if (f.isFile())
			list.add(f);
		else {
			discoverFiles(f);
		}

		// System.out.println("---> " + list);
		return gatherData();
	}

	public List<File> files() {
		return list;
	}

	private List<Document> gatherData() {
		// TODO this should be ran in parallel
		ArrayList<Document> r = new ArrayList<Document>(list.size());
		for (File f : list) {
			BufferedReader rdr = null;
			Document d = new Document(f);

			try {
				rdr = new BufferedReader(new FileReader(f));
				String raw = rdr.readLine();
				int relPosition = 0;
				long numWords = 0;
				int prbcount = 0;
				String title = "";
				while (raw != null) {
					if (raw.trim().startsWith("Title"))
					{
						String[] master = raw.trim().split(":");
						title = master[1].trim();
						System.out.println("Title: " + master[1].trim());
					}
					else if (raw.trim().startsWith("Author"))
					{
						String[] master = raw.trim().split(":");
						System.out.println("Author: " + master[1].trim());
					}
					else if (raw.trim().startsWith("Release Date"))
					{
						String[] master = raw.trim().split(":");
						System.out.println("Release Date: " + master[1].trim());
					}
					else if (raw.trim().startsWith("Posting Date"))
					{
						String[] master = raw.trim().split(":");
						System.out.println("Posting Date: " + master[1].trim());
					}
					else if (raw.trim().startsWith("Language"))
					{
						String[] master = raw.trim().split(":");
						System.out.println("Language: " + master[1].trim());
					}
					else if (raw.trim().startsWith("Translator"))
					{
						String[] master = raw.trim().split(":");
						System.out.println("Translator: " + master[1].trim());
					}
					else if (raw.trim().startsWith("***")) { ;	}
					else if (raw.trim().isEmpty()) { ; }
					else if (raw.trim().startsWith("Produced by"))
					{
						if(prbcount == 0) {
							String[] master = raw.trim().split("by");
							System.out.println("Produced by: " + master[1].trim());
							prbcount++;
						}
						
					}
					else {
					String[] parts = raw.trim().split(
							"[\\s,\\.:;\\-#~\\(\\)\\?\\!\\&\\*\\\"\\/\\'\\`]");
					
					numWords += parts.length;

					for (String p : parts) {
						if (!ignore.contains(p))
							d.addKeyword(p, relPosition);

						// location (word position) in document allows use to
						// calculate strength by relative location and frequency
						relPosition++;
					}
					}
					raw = rdr.readLine();
					
				}

				d.setNumWords(numWords);
				r.add(d);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (rdr != null)
						rdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return r;
	}

	/**
	 * depth search
	 * 
	 * @param dir
	 */
	private void discoverFiles(File dir) {
		if (dir == null || dir.isFile())
			return;

		File[] dirs = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isFile())
					list.add(f);
				else if (f.getName().startsWith("."))
					; // ignore
				else if (f.isDirectory()) {
					discoverFiles(f);
				}

				return false;
			}
		});
	}
}
