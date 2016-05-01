package bean;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ForwardIndex {

	@PrimaryKey
	private String path;
	private String value;

	public ForwardIndex() {
	}

	public ForwardIndex(String path, String value) {
		super();
		this.path = path;
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ForwardIndex [path=" + path + ", value=" + value + "]";
	}

}
