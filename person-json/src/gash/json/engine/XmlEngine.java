package gash.json.engine;

import gash.json.data.Person;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class XmlEngine extends JsonEngine {

	public XmlEngine(Properties conf) {
		super(conf);
	}

	@Override
	protected void store(Person p) {
		BufferedOutputStream bos = null;
		try {
			File here = new File(collectionData, p.getId());
			// log.info("storing: " + here);
			bos = new BufferedOutputStream(new FileOutputStream(here));
			String data = XmlBuilder.encode(p);
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

	@Override
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
			p = XmlBuilder.decode(new String(raw));
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
