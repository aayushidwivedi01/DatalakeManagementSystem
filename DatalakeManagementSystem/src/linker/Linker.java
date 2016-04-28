package linker;

import bean.FlatDocument;
import bean.ForwardIndex;
import storage.FlatDocumentDA;
import storage.ForwardIndexDA;

public class Linker {

	private LinkCreator linkCreator;
	
	public Linker() {
		linkCreator = new LinkCreator();
	}
	
	public void linkNewDocuments() {
		FlatDocumentDA fDA = new FlatDocumentDA();
		ForwardIndexDA fIndexDA = new ForwardIndexDA();
		for (FlatDocument flatDocument : fDA.fetchAll()) {
			for (String fIndexPath : flatDocument.getForwardIndex()) {
				ForwardIndex fIndex = fIndexDA.fetch(fIndexPath);
				System.out.println(fIndex);
			}
		}
	}
	
	public static void main(String[] args) {
		
		Linker linker = new Linker();
		linker.linkNewDocuments();
	}
}
