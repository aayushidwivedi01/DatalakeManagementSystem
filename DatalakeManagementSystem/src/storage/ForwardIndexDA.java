package storage;

import com.sleepycat.persist.PrimaryIndex;

import bean.ForwardIndex;

public class ForwardIndexDA {

	public ForwardIndex fetch(String ForwardIndexId) {
		ForwardIndex ForwardIndex = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, ForwardIndex> fIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					ForwardIndex.class);
			if (fIndexPrimaryIndex != null) {
				ForwardIndex = fIndexPrimaryIndex.get(ForwardIndexId);
			}
		}
		return ForwardIndex;
	}

	public ForwardIndex store(ForwardIndex ForwardIndex) {
		ForwardIndex insertedForwardIndex = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, ForwardIndex> fIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					ForwardIndex.class);
			if (fIndexPrimaryIndex != null) {
				insertedForwardIndex = fIndexPrimaryIndex.put(ForwardIndex);
			}
		}
		return insertedForwardIndex;
	}

	public boolean delete(String ForwardIndexId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, ForwardIndex> fIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					ForwardIndex.class);
			if (fIndexPrimaryIndex != null) {
				return fIndexPrimaryIndex.delete(ForwardIndexId);
			}
		}
		return false;
	}

	public boolean delete(ForwardIndex forwardIndex) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, ForwardIndex> fIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					ForwardIndex.class);
			if (fIndexPrimaryIndex != null) {
				return fIndexPrimaryIndex.delete(forwardIndex.getPath());
			}
		}
		return false;
	}

	public boolean update(ForwardIndex ForwardIndex) {
		ForwardIndex oldForwardIndex = fetch(ForwardIndex.getPath());
		if (oldForwardIndex != null) {
			oldForwardIndex.setValue(ForwardIndex.getValue());
			if (store(oldForwardIndex) != null) {
				return true;
			} else
				return false;

		} else
			return false;
	}

	public long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, ForwardIndex> fIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					ForwardIndex.class);
			if (fIndexPrimaryIndex != null) {
				result = fIndexPrimaryIndex.count();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		DBWrapper.setup("/home/cis550/db");

		ForwardIndex forwardIndex = new ForwardIndex("test_path", "test_value");

		ForwardIndexDA fIndexDA = new ForwardIndexDA();

		// fIndexDA.store(forwardIndex);

		System.out.println(fIndexDA.fetch("test_path"));
		System.out.println(fIndexDA.getSize());
		fIndexDA.delete(forwardIndex);
		System.out.println(fIndexDA.fetch("test_path"));
		DBWrapper.close();
	}

}
