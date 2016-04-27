package storage;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import bean.FlatDocument;

public class FlatDocumentDA {
	private static MongoClientURI URI = new MongoClientURI(
			"mongodb://dlms_webapp:webapp@ds013971.mlab.com:13971/webappdb");
	public static String COLLECTION_NAME_NEW = "flat_documents";
	public static String DOC_KEY = "document";
	public static String INDEX_KEY = "forwardIndex";
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	
	public FlatDocumentDA(){
		super();
		this.client = new MongoClient(URI);
		this.db = client.getDatabase(URI.getDatabase());
		db.getCollection(COLLECTION_NAME_NEW).createIndex(new Document(DOC_KEY, 1), new IndexOptions().unique(true));
		this.collection = db.getCollection(COLLECTION_NAME_NEW);
	}
	
	public MongoClient getClient() {
		return client;
	}

	public MongoDatabase getDb() {
		return db;
	}
	
	@SuppressWarnings("unchecked")
	public FlatDocument fetch(String document){
		Document doc = collection.find(eq(DOC_KEY, document)).first();
		FlatDocument flatDocument= null;
		if (doc != null) {
			List<String>fIndex =(List<String>) doc.get(INDEX_KEY);
			flatDocument = new FlatDocument(doc.getString(DOC_KEY), fIndex);
		}
		return flatDocument;
	}

	public void store(FlatDocument flatDocument){
		Document doc = Document.parse(new JSONObject(flatDocument).toString());
		collection.insertOne(doc);
	}
	
	public void update(FlatDocument flatDocument){
		delete(flatDocument);
		store(flatDocument);
	}
	public void delete(FlatDocument flatDocument) {
		collection.deleteOne(eq(DOC_KEY, flatDocument.getDocument()));
	}

	public void delete(String document) {
		collection.deleteOne(eq(DOC_KEY, document));
	}

	public void close() {
		client.close();
	}
	
	public static void main(String[] args){
		ArrayList<String>fIndex = new ArrayList<String>();
		fIndex.add("f1");
		fIndex.add("f2");
		FlatDocument flatDocument = new FlatDocument("mankit", fIndex);
		FlatDocumentDA fDa = new FlatDocumentDA();
		try{
			fDa.store(flatDocument);
			System.out.println("Store successful");
			System.out.println("Fetched:" + fDa.fetch("mankit"));
			fIndex.add("f3");
			fDa.update(flatDocument);
			System.out.println("Update successful");
			System.out.println("Fetched:" + fDa.fetch("mankit"));
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			fDa.delete("mankit");
			System.out.println(fDa.fetch("mankit"));
			fDa.close();
		}
	}

}
