package bean;

import java.util.List;

public class FlatDocument {
	private String document;
	private List<String> forwardIndex;
	
	public FlatDocument(String document, List<String> forwardIndex){
		this.document = document;
		this.forwardIndex = forwardIndex;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public List<String> getForwardIndex() {
		return forwardIndex;
	}

	public void setForwardIndex(List<String> forwardIndex) {
		this.forwardIndex = forwardIndex;
	}

	@Override
	public String toString() {
		return "FlatDocument [document=" + document + ", forwardIndex=" + forwardIndex + "]";
	}

}
