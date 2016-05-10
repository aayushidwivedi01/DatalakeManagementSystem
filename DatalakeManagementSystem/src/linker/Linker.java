package linker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bean.FlatDocument;
import bean.ForwardIndex;
import bean.ForwardIndexPair;
import bean.Link;
import storage.DBWrapper;
import storage.FlatDocumentDA;
import storage.ForwardIndexDA;
import threads.Queue;
import threads.ThreadPool;

public class Linker {

	private static boolean NEW_COLLECTION = true;
	private static boolean OLD_COLLECTION = false;
	private static final int LINKER_THREAD_COUNT = 128;
	private ThreadPool linkCreatorPool;
	private Queue<ForwardIndexPair> fIndexQueue;
	private Queue<Set<Link>> linksQueue;
	private ForwardIndexDA fIndexDA;

	public Linker() {
		fIndexQueue = new Queue<ForwardIndexPair>();
		linksQueue = new Queue<Set<Link>>();
		this.linkCreatorPool = new ThreadPool(LINKER_THREAD_COUNT, fIndexQueue, linksQueue);
		this.fIndexDA = new ForwardIndexDA();
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

	public void linkNewDocuments() throws InterruptedException {
		long startTime = System.nanoTime();

		FlatDocumentDA fDAOld = new FlatDocumentDA(OLD_COLLECTION);
		FlatDocumentDA fDANew = new FlatDocumentDA(NEW_COLLECTION);

		for (FlatDocument newDoc : fDANew.fetchAll()) {

			System.out.println("New Doc - " + newDoc.getDocument());
			List<ForwardIndex> f1List = getForwardIndicesForDoc(newDoc);
			for (ForwardIndex f1 : f1List) {
				fIndexQueue.enqueue(new ForwardIndexPair(f1, null));
			}
			System.out.println("Got fIndices for New Doc - " + newDoc.getDocument() + ", " + f1List.size());
			for (FlatDocument oldDoc : fDAOld.fetchAll()) {
				System.out.println("Old Doc - " + oldDoc.getDocument());
				List<ForwardIndex> f2List = getForwardIndicesForDoc(oldDoc);
				System.out.println("Got fIndices for Old Doc - " + oldDoc.getDocument() + ", " + f2List.size());
				System.out.println("Creating links ");
				for (ForwardIndex f1 : f1List) {
					for (ForwardIndex f2 : f2List) {
						fIndexQueue.enqueue(new ForwardIndexPair(f1, f2));
					}
				}
				System.out.println("Done with for Old Doc - " + oldDoc.getDocument());
			}
			System.out.println("Moving into old doc table  - " + newDoc.getDocument());
			fDANew.delete(newDoc);
			fDAOld.store(newDoc);
			System.out.println("Done with for New Doc - " + newDoc.getDocument());
		}
		System.out.println("Linker Main Thread - All indices assigned with queue size - " + fIndexQueue.getSize());
		linkCreatorPool.joinThreads();
		System.out.println("Linker Main Thread Finished!");
		long endTime = System.nanoTime();
		System.out.println("Time to link - " + (endTime - startTime) / 1000000 + " mSec");
	}

	public static void main(String[] args) throws InterruptedException {

		Linker linker = new Linker();
		FlatDocumentDA fDAOld = new FlatDocumentDA(OLD_COLLECTION);
		FlatDocumentDA fDANew = new FlatDocumentDA(NEW_COLLECTION);
		linker.moveDocs(fDAOld, fDANew);
		DBWrapper.setup("/home/cis550/db");
		linker.linkNewDocuments();
		DBWrapper.close();
	}
}