package bean;

import java.util.Set;

public class Links {
	private String source;
	private Set<Link> relations;
	
	public Links(String source, Set<Link> relations){
		this.source = source;
		this.relations = relations;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Set<Link> getRelations() {
		return relations;
	}

	public void setRelations(Set<Link> relations) {
		this.relations = relations;
	}

	@Override
	public String toString() {
		return "Links [source=" + source + ", relations=" + relations + "]";
	}

}
