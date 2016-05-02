package bean;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class Occurance {

	private String path;
	private String attribute;

	public Occurance() {
	}

	public Occurance(String path, String attribute) {
		super();
		this.path = path;
		this.attribute = attribute;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		return "Occurance [path=" + path + ", attribute=" + attribute + "]";
	}

}
