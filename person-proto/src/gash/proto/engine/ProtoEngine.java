package gash.proto.engine;

import gash.proto.data.Contact;
import gash.proto.data.Person;
import gash.proto.engine.indexing.IndexEngine;

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

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;

/**
 * storage engine for the people example.
 * 
 * @author gash
 * 
 */
public class ProtoEngine {
	protected Logger log = LoggerFactory.getLogger(ProtoEngine.class);

	private Properties conf;
	private boolean initialized = false;

	private String collection;
	private File collectionData;
	private File collectionIndexes;
	private IndexEngine indexer;

	private final ThreadLocal<LinkedBuffer> perThreadBuffer = new ThreadLocal<LinkedBuffer>() {
		@Override
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(1024);
		}
	};

	public ProtoEngine(Properties conf) {
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

		String name = conf.getProperty(ProtoConf.sCollection);
		if (name == null || name.trim().length() == 0)
			throw new RuntimeException("Invalid collection name");

		collection = name.trim();
		log.info("opening collection: " + collection);

		File basedir = new File(conf.getProperty(ProtoConf.sBaseDir));

		String s = conf.getProperty(ProtoConf.sDataDir);
		if (s == null)
			collectionData = new File(basedir, collection + "/" + "data");
		else
			collectionData = new File(s);

		if (!collectionData.exists() && !collectionData.mkdirs())
			throw new RuntimeException("Unable to find/create data storage for collection: " + collectionData.getAbsolutePath());

		s = conf.getProperty(ProtoConf.sDataDir);
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
		String cls = conf.getProperty(ProtoConf.sIndexEngine);
		if (cls == null)
			throw new RuntimeException("Missing " + ProtoConf.sIndexEngine + " property");
		try {
			indexer = (IndexEngine) Beans.instantiate(this.getClass().getClassLoader(), cls);
			//indexer = new MemoryIndexing();
		} catch (Exception e) {
			log.error("Unable to create index engine: " + cls, e);
		}
	}

	/**
	 * open/enable the collection
	 * 
	 * @param collection
	 */
	public void open() {
		init();
	}

	/**
	 * release resources of the collection - this will shutdown the collection.
	 * To use the collection again, you will need to open() the collection again
	 */
	public void close() {
		log.debug("closing collection " + collection);

		if (indexer != null)
			indexer.close();

		indexer = null;
	}

	/**
	 * current collection
	 * 
	 * @return
	 */
	public String getCollectionName() {
		if (collection != null)
			return collection;
		else
			return null;
	}

	/**
	 * ideally this needs to be abstracted to a byte data or a template of
	 * Message<>. however, with a template we don't know what data to index
	 * 
	 * @param p
	 * @return the UUID of the stored person instance
	 */
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

	/**
	 * find by ID
	 * 
	 * @param id
	 * @return
	 */
	public Person read(String id) {
		if (id == null)
			throw new RuntimeException("Null ID");

		try {
			return load(id);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read Person: " + id, ex);
		}
	}

	/**
	 * save an previously saved person instance
	 * 
	 * @param p
	 */
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

	/**
	 * delete the person
	 * 
	 * @param p
	 */
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
		LinkedBuffer buffer = perThreadBuffer.get();
		BufferedOutputStream bos = null;
		try {
			File here = new File(collectionData, p.getId());
			// log.info("storing: " + here);
			bos = new BufferedOutputStream(new FileOutputStream(here));
			ProtostuffIOUtil.writeTo(bos, p, Person.getSchema(), buffer);
			indexer.addIndexes(p);
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

			buffer.clear();
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
			ProtostuffIOUtil.mergeFrom(bis, p, Person.getSchema());
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
