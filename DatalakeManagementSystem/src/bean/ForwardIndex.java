package bean;

public class ForwardIndex {

	private String path;
	private String value;
	
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
