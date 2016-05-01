package storage;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import bean.FlatDocument;

public class FlatDocumentDA {

	private boolean forNewDocs;

	public FlatDocumentDA() {
		this.forNewDocs = true;
	}

	public FlatDocumentDA(boolean forNewDocs) {
		this.forNewDocs = forNewDocs;
	}

	public FlatDocument fetch(String FlatDocumentId) {
		FlatDocument FlatDocument = null;
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex;
			if (forNewDocs) {
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}
			if (userPrimaryIndex != null) {
				FlatDocument = userPrimaryIndex.get(FlatDocumentId);
			}
		}
		return FlatDocument;
	}

	public List<FlatDocument> fetchAll() {
		PrimaryIndex<String, FlatDocument> userPrimaryIndex;
		List<FlatDocument> flatDocuments = new ArrayList<FlatDocument>();
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			if (forNewDocs) {
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}

			EntityCursor<FlatDocument> flatDocCursor = userPrimaryIndex.entities();
			try {
				for (FlatDocument flatDocument : flatDocCursor) {
					flatDocuments.add(flatDocument);
				}
			} finally {
				flatDocCursor.close();
			}
		}
		return flatDocuments;
	}

	public FlatDocument store(FlatDocument FlatDocument) {
		FlatDocument insertedFlatDocument = null;
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex;
			if (forNewDocs) {
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}
			if (userPrimaryIndex != null) {
				insertedFlatDocument = userPrimaryIndex.put(FlatDocument);
			}
		}
		return insertedFlatDocument;
	}

	public boolean delete(String flatDocumentId) {
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex;
			if (forNewDocs) {
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}
			if (userPrimaryIndex != null) {
				return userPrimaryIndex.delete(flatDocumentId);
			}
		}
		return false;
	}

	public boolean delete(FlatDocument flatDocument) {
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex;
			if (forNewDocs) {
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}
			if (userPrimaryIndex != null) {
				return userPrimaryIndex.delete(flatDocument.getDocument());
			}
		}
		return false;
	}

	public boolean update(FlatDocument flatDocument) {
		FlatDocument oldFlatDocument = fetch(flatDocument.getDocument());
		if (oldFlatDocument != null) {
			oldFlatDocument.setDocument(flatDocument.getDocument());
			if (store(oldFlatDocument) != null) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null && DBWrapper.getNewDocStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex;
			if (forNewDocs) {
				System.out.println(DBWrapper.getNewDocStore().getStoreName());
				userPrimaryIndex = DBWrapper.getNewDocStore().getPrimaryIndex(String.class, FlatDocument.class);
			} else {
				System.out.println(DBWrapper.getStore().getStoreName());
				userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, FlatDocument.class);
			}
			if (userPrimaryIndex != null) {
				result = userPrimaryIndex.count();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		DBWrapper.setup("/home/cis550/db");

		List<String> fIdx = new ArrayList<String>();
		fIdx.add("id2");
		FlatDocument flatDocument = new FlatDocument("flatdoc2", fIdx);

		FlatDocumentDA fDA = new FlatDocumentDA(true);
		FlatDocumentDA fDA2 = new FlatDocumentDA(false);
		fDA.store(flatDocument);
		

		System.out.println(fDA2.fetch("flatdoc2"));
		System.out.println(fDA2.fetchAll());
		System.out.println(fDA.fetchAll());
		System.out.println(fDA.getSize());
		System.out.println(fDA2.getSize());
		DBWrapper.close();
	}

}
