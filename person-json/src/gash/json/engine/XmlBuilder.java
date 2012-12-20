package gash.json.engine;

import gash.json.data.Person;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

public class XmlBuilder {
	public static String encode(Person data) {
		String rtn = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Person.class);
			StringWriter writer = new StringWriter();
			jaxbContext.createMarshaller().marshal(data, writer);
			rtn = writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rtn;
	}

	public static Person decode(String data) {
		Person rtn = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Person.class);
			StringReader src = new StringReader(data);
			rtn = (Person) jaxbContext.createUnmarshaller().unmarshal(src);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rtn;
	}

}
