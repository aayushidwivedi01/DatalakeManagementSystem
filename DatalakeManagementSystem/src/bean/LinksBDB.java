package bean;

import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class LinksBDB {
	
	@PrimaryKey
	private String source;
	private Set<Link> relations;
	
	public LinksBDB(){
		
	}
	public LinksBDB(String source, Set<Link>relations){
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
		return "LinksBDB [source=" + source + ", relations=" + relations + "]";
	}
	
	

}
