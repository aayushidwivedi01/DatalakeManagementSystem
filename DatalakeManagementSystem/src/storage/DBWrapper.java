package storage;

import java.io.File;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DBWrapper {

	private static Environment myEnv = null;
	private static EntityStore store = null;
	private static EntityStore newDocStore = null;

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
//		envConfig.setTransactional(true);
//		storeConfig.setTransactional(true);
		envConfig.setConfigParam(EnvironmentConfig.ENV_RUN_CLEANER,  "false");
		envConfig.setConfigParam(EnvironmentConfig.ENV_RUN_CHECKPOINTER, "false");
		envConfig.setConfigParam(EnvironmentConfig.ENV_RUN_IN_COMPRESSOR, "false");
		envConfig.setAllowCreate(true);
		storeConfig.setAllowCreate(true);
		myEnv = new Environment(dbDir, envConfig);
		store = new EntityStore(myEnv, "Links Store", storeConfig);
		newDocStore = new EntityStore(myEnv, "new document Store", storeConfig);

	}

	public static EntityStore getStore() {
		return store;
	}

	public static EntityStore getNewDocStore() {
		return newDocStore;
	}

	public static void close() throws DatabaseException {
		if (store != null) {
			System.out.println("Closing BDB Store");
			store.close();
		}
		if (newDocStore != null) {
			System.out.println("Closing flatDocumentStore BDB Store");
			newDocStore.close();
		}
		if (myEnv != null) {
			System.out.println("Closing BDB Environment");
			myEnv.close();
		}

	}

}