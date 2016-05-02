package bean;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Document {

	@PrimaryKey
	private String documentId;
	private String username;
	private String path;
	private String permission;

	public Document() {

	}

	public Document(String documentId, String username, String path, String permission) {
		super();
		this.documentId = documentId;
		this.username = username;
		this.path = path;
		this.permission = permission;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
		return "Document [documentId=" + documentId + ", username=" + username + ", path=" + path + ", permission="
				+ permission + "]";
	}

}
