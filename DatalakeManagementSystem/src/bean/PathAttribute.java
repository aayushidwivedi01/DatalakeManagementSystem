package bean;

public class PathAttribute {

	private String file;
	private String path;
	private String attribute;
	private boolean hasAttribute;

	public PathAttribute(String file, String path, String attribute, boolean hasAttribute) {
		this.file = file;
		this.path = path;
		this.attribute = attribute;
		this.hasAttribute = hasAttribute;
	}

	public PathAttribute(String path) {
		String[] pathNodes = path.split("/");
		if (pathNodes.length > 1) {
			this.file = path.substring(0, path.indexOf("/"));
			this.path = path.substring(0, path.lastIndexOf("/"));
			this.attribute = path.substring(path.lastIndexOf("/") + 1);
			this.hasAttribute = true;
		} else {
			this.file = path;
			this.path = path;
			this.attribute = null;
			this.hasAttribute = false;
		}
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

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean getHasAttribute() {
		return hasAttribute;
	}

	public void setHasAttribute(boolean hasAttribute) {
		this.hasAttribute = hasAttribute;
	}

	@Override
	public String toString() {
		return "PathAttribute [file=" + file + ", path=" + path + ", attribute=" + attribute + ", hasAttribute="
				+ hasAttribute + "]";
	}

}
