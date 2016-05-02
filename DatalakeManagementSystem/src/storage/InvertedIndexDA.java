package storage;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.PrimaryIndex;
import bean.InvertedIndex;
import bean.Occurance;

public class InvertedIndexDA {

	public InvertedIndex fetch(String InvertedIndexId) {
		InvertedIndex InvertedIndex = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, InvertedIndex> iIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					InvertedIndex.class);
			if (iIndexPrimaryIndex != null) {
				InvertedIndex = iIndexPrimaryIndex.get(InvertedIndexId);
			}
		}
		return InvertedIndex;
	}

	public InvertedIndex store(InvertedIndex InvertedIndex) {
		InvertedIndex insertedInvertedIndex = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, InvertedIndex> iIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					InvertedIndex.class);
			if (iIndexPrimaryIndex != null) {
				insertedInvertedIndex = iIndexPrimaryIndex.put(InvertedIndex);
			}
		}
		return insertedInvertedIndex;
	}

	public boolean delete(String InvertedIndexId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, InvertedIndex> iIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					InvertedIndex.class);
			if (iIndexPrimaryIndex != null) {
				return iIndexPrimaryIndex.delete(InvertedIndexId);
			}
		}
		return false;
	}

	public boolean delete(InvertedIndex invertedIndex) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, InvertedIndex> iIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					InvertedIndex.class);
			if (iIndexPrimaryIndex != null) {
				return iIndexPrimaryIndex.delete(invertedIndex.getWord());
			}
		}
		return false;
	}

	public boolean update(InvertedIndex InvertedIndex) {
		InvertedIndex oldInvertedIndex = fetch(InvertedIndex.getWord());
		if (oldInvertedIndex != null) {
			oldInvertedIndex.setOccurs(InvertedIndex.getOccurs());
			if (store(oldInvertedIndex) != null) {
				return true;
			} else
				return false;

		} else
			return false;
	}

	public long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, InvertedIndex> iIndexPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					InvertedIndex.class);
			if (iIndexPrimaryIndex != null) {
				result = iIndexPrimaryIndex.count();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		DBWrapper.setup("/home/cis550/db");

		List<Occurance> occurances = new ArrayList<Occurance>();
		occurances.add(new Occurance("test_file/test_path/test_att", "test_att"));
		occurances.add(new Occurance("test_file/test_path/test_att2", "test_att2"));
		InvertedIndex invertedIndex = new InvertedIndex("test_word", occurances);

		InvertedIndexDA iIndexDA = new InvertedIndexDA();
		iIndexDA.store(invertedIndex);
		System.out.println(iIndexDA.fetch("test_word"));
		System.out.println(iIndexDA.getSize());

		invertedIndex.getOccurs().add(new Occurance("test_file/test_path/test_att3", "test_att3"));

		iIndexDA.update(invertedIndex);
		System.out.println(iIndexDA.fetch("test_word"));
		System.out.println(iIndexDA.getSize());

		iIndexDA.delete(invertedIndex);
		System.out.println(iIndexDA.fetch("test_word"));
		DBWrapper.close();
	}

}
