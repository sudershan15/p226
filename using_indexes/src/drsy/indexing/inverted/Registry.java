package drsy.indexing.inverted;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import drsy.indexing.Document;
import drsy.indexing.KeyWord;
import drsy.indexing.stopwords.StopWords;
import drsy.indexing.stopwords.StopWordsFile;

public class Registry {
	public static final String sStopWords = "stop.words.file";
	public static final String sDataStorage = "data.storage";
	public static final String sIndexStorage = "index.storage";

	public enum Match {
		Or, And, Proximity
	}

	// TODO abstract/isolate storage. oh yeah and persist it too
	private HashMap<String, ArrayList<Document>> index = new HashMap<String, ArrayList<Document>>();

	private Loader loader;
	private Properties conf;

	public Registry(Properties conf) {
		this.conf = conf;
		setup();
	}

	/**
	 * find all documents containing these key words - union.
	 * 
	 * TODO allow for union and intersection
	 * 
	 * @param keywords
	 * @return
	 */
	public List<Document> query(List<String> keywords) {
		if (keywords == null || keywords.size() == 0)
			return null;

		// TODO break down the keywords into separate words for searching (e.g.,
		// 'san jose' => 'san' 'jose')

		ArrayList<Document> r = new ArrayList<Document>();
		for (String w : keywords) {
			ArrayList<Document> sub = index.get(w);
			if (sub != null) {
				for (Document d : sub) {
					if (!r.contains(d))
						r.addAll(sub);
				}
			}
		}

		return r;
	}

	public void register(File f) {
		if (f == null)
			return;

		// TODO prevent double registration

		List<Document> docs = loader.load(f);
		for (Document d : docs) {
			for (KeyWord kw : d.keywords()) {
				ArrayList<Document> list = index.get(kw.word);
				if (list == null) {
					list = new ArrayList<Document>();
					index.put(kw.word, list);
				}

				list.add(d);
			}
		}
	}

	private void setup() {
		try {
			File idir = new File(conf.getProperty(sIndexStorage));

			File swf = new File(conf.getProperty(sStopWords));
			StopWords swords = new StopWordsFile(swf);
			loader = new Loader(swords);
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup registry.", e);
		}
	}
}
