package gash.json.engine;

import gash.json.data.Contact;
import gash.json.data.Person;
import gash.json.engine.indexing.IndexEngine;

import java.beans.Beans;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * storage engine for the people example.
 * 
 * @author gash
 * 
 */
public class JsonEngine implements Engine {
	protected Logger log = LoggerFactory.getLogger(JsonEngine.class);

	protected Properties conf;
	protected boolean initialized = false;

	protected String collection;
	protected File collectionData;
	protected File collectionIndexes;
	protected IndexEngine indexer;

	public JsonEngine(Properties conf) {
		if (conf == null)
			throw new RuntimeException("Null configuration");

		this.conf = conf;
		init();
	}

	/**
	 * initialize and locate resources for the collection
	 */
	protected void init() {
		if (conf == null)
			throw new RuntimeException("Missing configuration properties");

		close();

		String name = conf.getProperty(EngineConf.sCollection);
		if (name == null || name.trim().length() == 0)
			throw new RuntimeException("Invalid collection name");

		collection = name.trim();
		log.info("opening collection: " + collection);

		File basedir = new File(conf.getProperty(EngineConf.sBaseDir));

		String s = conf.getProperty(EngineConf.sDataDir);
		if (s == null)
			collectionData = new File(basedir, collection + "/" + "data");
		else
			collectionData = new File(s);

		if (!collectionData.exists() && !collectionData.mkdirs())
			throw new RuntimeException("Unable to find/create data storage for collection: " + collectionData.getAbsolutePath());

		s = conf.getProperty(EngineConf.sDataDir);
		if (s == null)
			collectionIndexes = new File(basedir, collection + "/" + "index");
		else
			collectionIndexes = new File(s);

		if (!collectionIndexes.exists() && !collectionIndexes.mkdirs())
			throw new RuntimeException("Unable to find/create indexes for collection: " + collectionData.getAbsolutePath());

		initialized = true;

		log.info("collection data: " + collectionData);
		log.info("collection index: " + collectionIndexes);

		// indexing engine
		String cls = conf.getProperty(EngineConf.sIndexEngine);
		if (cls == null)
			throw new RuntimeException("Missing " + EngineConf.sIndexEngine + " property");
		try {
			indexer = (IndexEngine) Beans.instantiate(this.getClass().getClassLoader(), cls);
			// indexer = new MemoryIndexing();
		} catch (Exception e) {
			log.error("Unable to create index engine: " + cls, e);
		}
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#open()
	 */
	@Override
	public void open() {
		init();
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#close()
	 */
	@Override
	public void close() {
		log.debug("closing collection " + collection);

		if (indexer != null)
			indexer.close();

		indexer = null;
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#getCollectionName()
	 */
	@Override
	public String getCollectionName() {
		if (collection != null)
			return collection;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#create(gash.json.data.Person)
	 */
	@Override
	public String create(Person p) {
		if (!initialized)
			throw new RuntimeException("Engine has not been initialized");

		String id = UUID.randomUUID().toString();
		p.setId(id);
		if (p.getContactsList() != null) {
			for (Contact c : p.getContactsList())
				c.setId(UUID.randomUUID().toString());
		}

		store(p);

		return id;
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#find(gash.json.data.Person)
	 */
	@Override
	public List<Person> find(Person like) {
		List<Person> r = null;
		try {
			List<String> ids = indexer.find(like);
			if (ids != null) {
				r = new ArrayList<Person>();
				for (String id : ids) {
					Person p = this.load(id);
					if (p != null)
						r.add(p);
				}
			}
		} catch (Exception e) {
			log.error("Failed to find()", e);
			return null;
		}

		return r;
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#read(java.lang.String)
	 */
	@Override
	public Person read(String id) {
		if (id == null)
			throw new RuntimeException("Null ID");

		try {
			return load(id);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read Person: " + id, ex);
		}
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#update(gash.json.data.Person)
	 */
	@Override
	public void update(Person p) {
		if (p == null)
			throw new RuntimeException("Null person");

		try {
			// ensure a correct ID
			UUID u = UUID.fromString(p.getId());
			if (u == null)
				throw new RuntimeException("Not a persisted Person, ID is invalid.");

			store(p);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to save Person", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gash.json.engine.Engine#delete(gash.json.data.Person)
	 */
	@Override
	public void delete(Person p) {
		if (p == null)
			throw new RuntimeException("Null person");

		remove(p);
	}

	/* ------------------------------------------------------------------------- */
	/* all protected CRUD methods assume public CRUD methods have validated data */

	protected void remove(Person p) {
		File here = new File(collectionData, p.getId());
		if (here.exists()) {
			indexer.removeIndexes(p);
			here.delete();
		}
	}

	protected void store(Person p) {
		BufferedOutputStream bos = null;
		try {
			File here = new File(collectionData, p.getId());
			// log.info("storing: " + here);
			bos = new BufferedOutputStream(new FileOutputStream(here));
			String data = JsonBuilder.encode(p);
			if (data != null) {
				bos.write(data.getBytes());
				indexer.addIndexes(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			remove(p);
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (Exception e) {
			}
		}
	}

	protected Person load(String id) {
		File here = new File(collectionData, id);
		if (!here.exists())
			return null;

		Person p = new Person();
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(here));
			byte[] raw = new byte[(int) here.length()];
			bis.read(raw);
			p = JsonBuilder.decode(new String(raw), Person.class);
		} catch (Exception e) {
			p = null;
			log.error("Failed to load person: " + id, e);
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return p;
	}

}
