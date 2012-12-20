package gash.hibernate.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "contact")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "contact_id", updatable = false, nullable = false)
	private Long id;
	private String type;
	private String value;
	private String note;

	// vertical sharding - we need to introduce a reference to the owning person
	// since we cannot navigate through the person. Note the
	// @Column(nullable=false) is redundant, but it helps us remember that we
	// need this field

	@Column(nullable = false)
	private Long parent_id;

	@Version
	private int version;

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(": ");
		sb.append(value);
		// sb.append(" (belongs to ");
		// sb.append(parent_id);
		// sb.append(")");
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String notes) {
		this.note = note;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

}
