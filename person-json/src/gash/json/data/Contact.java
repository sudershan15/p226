
package gash.json.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact {

	String id;
	String type;
	String value;
	String notes;

	public Contact() {

	}

	public Contact(String id, String type, String value) {
		this.id = id;
		this.type = type;
		this.value = value;
	}

	// getters and setters

	// id

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// type

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// value

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	// notes

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
