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
import bean.Links;

public class LinksDA {

	private static MongoClientURI URI = new MongoClientURI(
			"mongodb://dlms_webapp:webapp@ds013971.mlab.com:13971/webappdb");
	public static String COLLECTION_NAME = "links";
	public static String SOURCE_KEY = "source";
	public static String RELATIONS_KEY = "relations";
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	
	public LinksDA(){
		super();
		this.client = new MongoClient(URI);
		this.db = client.getDatabase(URI.getDatabase());
		db.getCollection(COLLECTION_NAME).createIndex(new Document(SOURCE_KEY, 1), new IndexOptions().unique(true));
		this.collection = db.getCollection(COLLECTION_NAME);
	}
	
	public MongoClient getClient() {
		return client;
	}

	public MongoDatabase getDb() {
		return db;
	}
	
	@SuppressWarnings("unchecked")
	public Links fetch(String source){
		Document doc = collection.find(eq(SOURCE_KEY, source)).first();
		Links links = null;
		if(doc != null){
			List<JSONObject>relations = (List<JSONObject>) doc.get(RELATIONS_KEY);
			links = new Links(source, relations);
		}
		return links;
	}
	
	public void store(Links links){
		Document doc = Document.parse(new JSONObject(links).toString());
		collection.insertOne(doc);
	}
	
	public void update(Links links){
		delete(links);
		store(links);
	}
	public void delete(Links links) {
		collection.deleteOne(eq(SOURCE_KEY, links.getSource()));
	}

	public void delete(String source) {
		collection.deleteOne(eq(SOURCE_KEY, source));
	}

	public void close() {
		client.close();
	}
	
	public static void main(String[] args){
		ArrayList<JSONObject>relations = new ArrayList<JSONObject>();
		JSONObject jsonObj = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
		relations.add(jsonObj);
		relations.add(jsonObj);
		Links links = new Links("mankit", relations);
		LinksDA lDa = new LinksDA();
		try{
			lDa.store(links);
			System.out.println("Store successful");
			System.out.println("Fetched:" + lDa.fetch("mankit"));
			relations.add(jsonObj);
			lDa.update(links);
			System.out.println("Update successful");
			System.out.println("Fetched:" + lDa.fetch("mankit"));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			lDa.delete("mankit");
			System.out.println(lDa.fetch("mankit"));
			lDa.close();
		}
	}
	
}
