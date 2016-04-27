package bean;

public class Link {

	private String source;
	private String type;
	private String dest;
	private double weight;

	public Link(String source, String type, String dest, double weight) {
		super();
		this.source = source;
		this.type = type;
		this.dest = dest;
		this.weight = weight;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Link [source=" + source + ", type=" + type + ", dest=" + dest + ", weight=" + weight + "]";
	}

}
