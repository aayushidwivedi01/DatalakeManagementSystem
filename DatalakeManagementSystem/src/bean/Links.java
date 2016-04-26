package bean;

import java.util.List;

import org.json.JSONObject;

public class Links {
	private String source;
	private List<JSONObject> relations;
	
	public Links(String source, List<JSONObject> relations){
		this.source = source;
		this.relations = relations;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<JSONObject> getRelations() {
		return relations;
	}

	public void setRelations(List<JSONObject> relations) {
		this.relations = relations;
	}

	@Override
	public String toString() {
		return "Links [source=" + source + ", relations=" + relations + "]";
	}

}
