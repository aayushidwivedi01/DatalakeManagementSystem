package bean;

import java.util.List;

public class InvertedIndex {

	private String _id;
	private String word;
	private List<Occurance> occurs;
	
	public InvertedIndex(String word, List<Occurance> occurs) {
		super();
		this.word = word;
		this.occurs = occurs;
	}
	public InvertedIndex(String _id, String word, List<Occurance> occurs) {
		super();
		this._id = _id;
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
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	@Override
	public String toString() {
		return "InvertedIndex [_id=" + _id + ", word=" + word + ", occurs=" + occurs + "]";
	}
	
	
	
}
