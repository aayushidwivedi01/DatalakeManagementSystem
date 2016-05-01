package bean;

import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class InvertedIndex {

	@PrimaryKey
	private String word;
	private List<Occurance> occurs;

	public InvertedIndex() {
	}

	public InvertedIndex(String word, List<Occurance> occurs) {
		super();
		this.word = word;
		this.occurs = occurs;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<Occurance> getOccurs() {
		return occurs;
	}

	public void setOccurs(List<Occurance> occurs) {
		this.occurs = occurs;
	}

	@Override
	public String toString() {
		return "InvertedIndex [word=" + word + ", occurs=" + occurs + "]";
	}

}
