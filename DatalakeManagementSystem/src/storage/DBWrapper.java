package storage;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import bean.Link;
import bean.LinksBDB;

public class DBWrapper {

	private static String envDirectory = null;

	private static Environment myEnv = null;
	private static EntityStore store;

	public static void setup(String dirName) throws DatabaseException {
		if (dirName == null)
			return;
		File dbDir = new File(dirName);

		if (!(dbDir.isDirectory())) {
			if (dbDir.mkdir()) {
				System.out.println("Directory creation successful");
			} else {
				System.out.println("Failed to create new directory");
			}
		}

		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();

		envConfig.setAllowCreate(true);
		storeConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		storeConfig.setTransactional(true);

		myEnv = new Environment(dbDir, envConfig);
		store = new EntityStore(myEnv, "Links Store", storeConfig);

	}

	public static EntityStore getStore() {
		return store;
	}

	public static void shutdown() throws DatabaseException {
		if (store != null)
			store.close();
		
		if (myEnv != null)
			myEnv.close();

		
	}

}