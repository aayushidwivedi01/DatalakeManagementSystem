package bean;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class Link {

	private String source;
	private String type;
	private String dest;
	private double weight;

	public Link() {

	}

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

	public boolean equals(Link obj) {
		return this.source.equals(obj.getSource()) && this.dest.equals(obj.getDest()) && this.type.equals(obj.getType())
				&& this.getWeight() == obj.getWeight() ? true : false;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object object) {
		Link obj = (Link) object;
		return this.source.equalsIgnoreCase(obj.getSource()) && this.dest.equalsIgnoreCase(obj.getDest())
				&& this.type.equalsIgnoreCase(obj.getType()) && this.getWeight() == obj.getWeight() ? true : false;
	}

}
