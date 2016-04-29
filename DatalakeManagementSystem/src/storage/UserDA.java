package storage;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import bean.User;

public class UserDA {

	private static MongoClientURI URI = new MongoClientURI(
			"mongodb://ec2-54-174-67-253.compute-1.amazonaws.com:27017/dlms_db");
	public static String COLLECTION_NAME = "users";
	public static String USERNAME_KEY = "username";
	public static String PASSWORD_KEY = "password";
	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;

	public UserDA() {
		super();
		this.client = new MongoClient(URI);
		this.db = client.getDatabase(URI.getDatabase());
		this.collection = db.getCollection(COLLECTION_NAME);
	}

	public MongoClient getClient() {
		return client;
	}

	public MongoDatabase getDb() {
		return db;
	}

	public User fetch(String username) {
		Document doc = collection.find(eq(USERNAME_KEY, username)).first();
		User user = null;
		if (doc != null) {
			user = new User(doc.getString(USERNAME_KEY), doc.getString(PASSWORD_KEY));
		}
		return user;
	}

	public void store(User user) {
		Document doc = new Document(USERNAME_KEY, user.getUsername()).append(PASSWORD_KEY, user.getPassword());
		collection.insertOne(doc);
	}

	public void update(User user) {
		collection.updateOne(eq(USERNAME_KEY, user.getUsername()), set(PASSWORD_KEY, user.getPassword()));
	}

	public void delete(User user) {
		collection.deleteOne(eq(USERNAME_KEY, user.getUsername()));
	}

	public void delete(String username) {
		collection.deleteOne(eq(USERNAME_KEY, username));
	}

	public void close() {
		client.close();
	}

	public static void main(String[] args) {
		User user = new User("aayushi", "pass");
		UserDA userDA = new UserDA();
		userDA.store(user);
		System.out.println(userDA.fetch("aayushi"));
		userDA.update(new User("aayushi", "new password"));
		System.out.println(userDA.fetch("aayushi"));
		//userDA.delete("aayushi");
		System.out.println(userDA.fetch("aayushi"));
		userDA.close();
	}

}
