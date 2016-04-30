package storage;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import bean.ForwardIndex;

public class ForwardIndexDA {

	private static MongoClientURI URI = new MongoClientURI(
			"mongodb://dlms_webapp:webapp@ds013971.mlab.com:13971/webappdb");
	public static String COLLECTION_NAME = "fi_test";
	public static String PATH_KEY = "path";
	public static String VALUE_KEY = "value";
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;

	public ForwardIndexDA() {
		super();
		this.client = new MongoClient(URI);
		this.db = client.getDatabase(URI.getDatabase());
		db.getCollection(COLLECTION_NAME).createIndex(new Document(PATH_KEY, 1), new IndexOptions().unique(true));
		this.collection = db.getCollection(COLLECTION_NAME);
	}

	public MongoClient getClient() {
		return client;
	}

	public MongoDatabase getDb() {
		return db;
	}

	public ForwardIndex fetch(String fIndexPath) {
		Document doc = collection.find(eq(PATH_KEY, fIndexPath)).first();
		ForwardIndex fIndex = null;
		if (doc != null) {
			fIndex = new ForwardIndex(doc.getString(PATH_KEY), doc.getString(VALUE_KEY));
		}
		return fIndex;
	}

	public void store(ForwardIndex fIndex) {
		Document doc = new Document(PATH_KEY, fIndex.getPath()).append(VALUE_KEY, fIndex.getValue());
		collection.insertOne(doc);
	}

	public void update(ForwardIndex fIndex) {
		collection.updateOne(eq(PATH_KEY, fIndex.getPath()), set(VALUE_KEY, fIndex.getValue()));
	}

	public void delete(ForwardIndex fIndex) {
		collection.deleteOne(eq(PATH_KEY, fIndex.getPath()));
	}

	public void delete(String username) {
		collection.deleteOne(eq(PATH_KEY, username));
	}

	public void close() {
		client.close();
	}

	public static void main(String[] args) {
		ForwardIndex fIndex = new ForwardIndex("user1_tst.xml/title", "my doc");
		ForwardIndexDA fIndexDA = new ForwardIndexDA();
		fIndexDA.store(fIndex);
		System.out.println(fIndexDA.fetch("user1_tst.xml/title"));
		fIndexDA.update(new ForwardIndex("user1_tst.xml/title", "my doc 2"));
		System.out.println(fIndexDA.fetch("user1_tst.xml/title"));
		fIndexDA.delete("user1_tst.xml/title");
		System.out.println(fIndexDA.fetch("user1_tst.xml/title"));
		fIndexDA.close();
	}

}
