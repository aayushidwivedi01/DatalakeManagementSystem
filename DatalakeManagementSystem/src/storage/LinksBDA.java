package storage;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

import bean.LinksBDB;

public class LinksBDA {
	private PrimaryIndex<String, LinksBDB> pIdx;

	public static LinksBDB fetch(String LinksId)
	{
		LinksBDB Links = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, LinksBDB> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					LinksBDB.class);
			if (userPrimaryIndex != null) {
				Links = userPrimaryIndex.get(LinksId);
			}
		}
		return Links;
	}

	public static LinksBDB store(LinksBDB Links)
	{
		LinksBDB insertedLinks = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, LinksBDB> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					LinksBDB.class);
			if (userPrimaryIndex != null) {
				insertedLinks = userPrimaryIndex.put(Links);
			}
		}
		return insertedLinks;
	}

	public static boolean delete(String LinksId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, LinksBDB> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					LinksBDB.class);
			if (userPrimaryIndex != null) {
				return userPrimaryIndex.delete(LinksId);
			}
		}
		return false;
	}

	public static boolean update(LinksBDB links){
		LinksBDB oldLinks = fetch(links.getSource());
		if(oldLinks != null){
			oldLinks.setRelations(links.getRelations());
			if (store(oldLinks)!=null){
				return true;
			} else return false;
			
		}else return false;
	}
	public static long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, LinksBDB> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					LinksBDB.class);
			if (userPrimaryIndex != null) {
				result = userPrimaryIndex.count();
			}
		}
		return result;
	}

}
