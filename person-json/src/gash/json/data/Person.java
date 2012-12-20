
package gash.json.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Person {

	// non-private fields
	// see
	// http://developer.android.com/guide/practices/design/performance.html#package_inner
	String id;
	String firstName;
	String lastName;
	String nickName;
	Long created;
	String role;
	List<Contact> contacts;

	public Person() {

	}

	public Person(String id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	// getters and setters

	// id

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// firstName

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// lastName

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// nickName

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	// created

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	// role

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// contacts

	public List<Contact> getContactsList() {
		return contacts;
	}

	public void setContactsList(List<Contact> contacts) {
		this.contacts = contacts;
	}
}
