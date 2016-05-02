package storage;

import com.sleepycat.persist.PrimaryIndex;

import bean.Document;

public class DocumentDA {

	public Document fetch(String documentId) {
		Document Document = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Document> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Document.class);
			if (docPrimaryIndex != null) {
				Document = docPrimaryIndex.get(documentId);
			}
		}
		return Document;
	}

	public Document store(Document document) {
		Document insertedDocument = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Document> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Document.class);
			if (docPrimaryIndex != null) {
				insertedDocument = docPrimaryIndex.put(document);
			}
		}
		return insertedDocument;
	}

	public boolean delete(String documentId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Document> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Document.class);
			if (docPrimaryIndex != null) {
				return docPrimaryIndex.delete(documentId);
			}
		}
		return false;
	}

	public boolean delete(Document document) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Document> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Document.class);
			if (docPrimaryIndex != null) {
				return docPrimaryIndex.delete(document.getDocumentId());
			}
		}
		return false;
	}

	public boolean update(Document document) {
		Document oldDocument = fetch(document.getDocumentId());
		if (oldDocument != null) {
			oldDocument.setPath(document.getPath());
			oldDocument.setPermission(document.getPermission());
			oldDocument.setOwner(document.getPermission());
			if (store(oldDocument) != null) {
				return true;
			} else
				return false;

		} else
			return false;
	}

	public long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Document> docPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Document.class);
			if (docPrimaryIndex != null) {
				result = docPrimaryIndex.count();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		DBWrapper.setup("/home/cis550/db");

		Document document = new Document("test_doc_id", "test_username", "test_path", "Public");

		DocumentDA docDA = new DocumentDA();

		docDA.store(document);

		System.out.println(docDA.fetch("test_doc_id"));
		System.out.println(docDA.getSize());
		docDA.delete(document);
		System.out.println(docDA.fetch("test_doc_id"));
		DBWrapper.close();
	}

}
