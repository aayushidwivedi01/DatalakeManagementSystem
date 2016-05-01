package storage;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import bean.FlatDocument;

public class FlatDocumentDA {

	public FlatDocumentDA(){}

	public FlatDocument fetch(String FlatDocumentId) {
		FlatDocument FlatDocument = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					FlatDocument.class);
			if (userPrimaryIndex != null) {
				FlatDocument = userPrimaryIndex.get(FlatDocumentId);
			}
		}
		return FlatDocument;
	}
	
	public List<FlatDocument> fetchAll() {
		PrimaryIndex<String, FlatDocument> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
				FlatDocument.class);
		List<FlatDocument> flatDocuments = new ArrayList<FlatDocument>();
		EntityCursor<FlatDocument> flatDocCursor = docPrimaryIndex.entities();
		try{
			for(FlatDocument flatDocument : flatDocCursor){
				flatDocuments.add(flatDocument);
			}
		} finally{
			flatDocCursor.close();
		}
		return flatDocuments;
	}

	public FlatDocument store(FlatDocument FlatDocument) {
		FlatDocument insertedFlatDocument = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					FlatDocument.class);
			if (userPrimaryIndex != null) {
				insertedFlatDocument = userPrimaryIndex.put(FlatDocument);
			}
		}
		return insertedFlatDocument;
	}

	public boolean delete(String flatDocumentId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					FlatDocument.class);
			if (userPrimaryIndex != null) {
				return userPrimaryIndex.delete(flatDocumentId);
			}
		}
		return false;
	}
	
	public boolean delete(FlatDocument flatDocument) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					FlatDocument.class);
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
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, FlatDocument> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					FlatDocument.class);
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

		FlatDocumentDA fDA = new FlatDocumentDA();

		fDA.store(flatDocument);

		System.out.println(fDA.fetchAll());
		System.out.println(fDA.getSize());
		DBWrapper.close();
	}



}
