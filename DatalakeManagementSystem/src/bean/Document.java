package bean;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Document {

	@PrimaryKey
	private String documentId;
	private String owner;
	private String path;
	private String permission;

	public Document() {

	}

	public Document(String documentId, String owner, String path, String permission) {
		super();
		this.documentId = documentId;
		this.owner = owner;
		this.path = path;
		this.permission = permission;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "Document [documentId=" + documentId + ", owner=" + owner + ", path=" + path + ", permission="
				+ permission + "]";
	}

}
