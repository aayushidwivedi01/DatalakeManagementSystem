package linker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bean.FlatDocument;
import bean.ForwardIndex;
import bean.Link;
import bean.Links;
import storage.DBWrapper;
import storage.FlatDocumentDA;
import storage.ForwardIndexDA;
import storage.LinksDA;

public class Linker {

	private static boolean NEW_COLLECTION = true;
	private static boolean OLD_COLLECTION = false;
	private LinkCreator linkCreator;
	private ForwardIndexDA fIndexDA;

	public Linker() {
		linkCreator = new LinkCreator();
		fIndexDA = new ForwardIndexDA();
	}

	private List<ForwardIndex> getForwardIndicesForDoc(FlatDocument newDoc) {
		List<ForwardIndex> fIndexList = new ArrayList<ForwardIndex>();
		for (String fIndexPathNew : newDoc.getForwardIndex()) {
			ForwardIndex fIndex = fIndexDA.fetch(fIndexPathNew);
			if (fIndex != null) {
				fIndexList.add(fIndex);
			}
		}
		return fIndexList;
	}

	private void moveDocs(FlatDocumentDA fDAOld, FlatDocumentDA fDANew) {
		for (FlatDocument newDoc : fDAOld.fetchAll()) {
			fDAOld.delete(newDoc);
			fDANew.store(newDoc);
		}
	}

	public void linkNewDocuments() {
		Map<String, List<ForwardIndex>> docForwardIndices = new HashMap<String, List<ForwardIndex>>();
		Set<Link> links = new HashSet<Link>();
		FlatDocumentDA fDAOld = new FlatDocumentDA(OLD_COLLECTION);
		FlatDocumentDA fDANew = new FlatDocumentDA(NEW_COLLECTION);

		for (FlatDocument newDoc : fDANew.fetchAll()) {

			System.out.println("New Doc - " + newDoc.getDocument());
			if (!docForwardIndices.containsKey(newDoc.getDocument())) {
				docForwardIndices.put(newDoc.getDocument(), getForwardIndicesForDoc(newDoc));
			}
			for (ForwardIndex f1 : docForwardIndices.get(newDoc.getDocument())) {
				links.addAll(linkCreator.createSelfLinks(f1));
			}
			System.out.println("Got fIndices for New Doc - " + newDoc.getDocument() + ", "
					+ docForwardIndices.get(newDoc.getDocument()).size());
			for (FlatDocument oldDoc : fDAOld.fetchAll()) {
				System.out.println("Old Doc - " + oldDoc.getDocument());
				if (!docForwardIndices.containsKey(oldDoc.getDocument())) {
					docForwardIndices.put(oldDoc.getDocument(), getForwardIndicesForDoc(oldDoc));
				}
				System.out.println("Got fIndices for Old Doc - " + oldDoc.getDocument() + ", "
						+ docForwardIndices.get(oldDoc.getDocument()).size());
				System.out.println("Creating links ");
				for (ForwardIndex f1 : docForwardIndices.get(newDoc.getDocument())) {
					for (ForwardIndex f2 : docForwardIndices.get(oldDoc.getDocument())) {
						links.addAll(linkCreator.createLinks(f1, f2));
					}
				}
				System.out.println("Done with for Old Doc - " + oldDoc.getDocument());
			}
			System.out.println("Moving into old doc table  - " + newDoc.getDocument());
			fDANew.delete(newDoc);
			fDAOld.store(newDoc);
			System.out.println("Done with for New Doc - " + newDoc.getDocument());

		}

		System.out.println("Done with all docs with links - " + links.size());

		// linkCreator.printLinks((linkCreator.mergeLinks(links)));
		// merge links and store them
		Map<String, Links> mapOfLinks = linkCreator.mergeLinks(links);
		System.out.println("Unique sources - " + mapOfLinks.size());
		long startTime = System.nanoTime();
		linkCreator.storeLinksSingle(mapOfLinks);
		long endTime = System.nanoTime();
		LinksDA lDA = new LinksDA();
		System.out.println("Time to store - " + (endTime - startTime) / 1000000 + " mSec");
		System.out.println("Stored Entries - " + lDA.getSize());
		System.out.println(lDA.fetch("will"));
	}

	public static void main(String[] args) {

		Linker linker = new Linker();
		FlatDocumentDA fDAOld = new FlatDocumentDA(OLD_COLLECTION);
		FlatDocumentDA fDANew = new FlatDocumentDA(NEW_COLLECTION);
		linker.moveDocs(fDAOld, fDANew);
		DBWrapper.setup("/home/cis550/db");
		linker.linkNewDocuments();
		DBWrapper.close();
	}
}