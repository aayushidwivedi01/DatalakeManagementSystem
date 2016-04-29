package bean;

import java.util.Set;

import org.json.JSONObject;

public class Links {
	private String source;
	private Set<JSONObject> relations;
	
	public Links(String source, Set<JSONObject> relations){
		this.source = source;
		this.relations = relations;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Set<JSONObject> getRelations() {
		return relations;
	}

	public void setRelations(Set<JSONObject> relations) {
		this.relations = relations;
	}

	@Override
	public String toString() {
		return "Links [source=" + source + ", relations=" + relations + "]";
	}

}
