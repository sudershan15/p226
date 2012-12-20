package gash.hibernate.app;

public class Card {
	private String firstName;
	private String lastName;
	private String value;

	public Card(String firstName, String lastName, String value) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.value = value;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lastName);
		sb.append(", ");
		sb.append(firstName);
		sb.append(" = ");
		sb.append(value);

		return sb.toString();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
