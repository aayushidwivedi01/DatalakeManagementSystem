package storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import bean.Link;
import bean.Links;

public class LinksDA {

	public Links fetch(String LinksId) {
		Links Links = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Links> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Links.class);
			if (userPrimaryIndex != null) {
				Links = userPrimaryIndex.get(LinksId);
			}
		}
		return Links;
	}

	public List<Links> fetchAll() {
		PrimaryIndex<String, Links> userPrimaryIndex;
		List<Links> links = new ArrayList<Links>();
		if (DBWrapper.getStore() != null) {
			userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class, Links.class);
			EntityCursor<Links> linksCursor = userPrimaryIndex.entities();
			try {
				for (Links link : linksCursor) {
					links.add(link);
				}
			} finally {
				linksCursor.close();
			}
		}
		return links;
	}

	public Links store(Links Links) {
		Links insertedLinks = null;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Links> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Links.class);
			if (userPrimaryIndex != null) {
				insertedLinks = userPrimaryIndex.put(Links);
			}
		}
		return insertedLinks;
	}

	public boolean delete(String LinksId) {
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Links> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Links.class);
			if (userPrimaryIndex != null) {
				return userPrimaryIndex.delete(LinksId);
			}
		}
		return false;
	}

	public boolean update(Links links) {
		Links oldLinks = fetch(links.getSource());
		if (oldLinks != null) {
			oldLinks.setRelations(links.getRelations());
			if (store(oldLinks) != null) {
				return true;
			} else
				return false;

		} else
			return false;
	}

	public long getSize() {
		long result = -1;
		if (DBWrapper.getStore() != null) {
			PrimaryIndex<String, Links> userPrimaryIndex = DBWrapper.getStore().getPrimaryIndex(String.class,
					Links.class);
			if (userPrimaryIndex != null) {
				result = userPrimaryIndex.count();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		DBWrapper.setup("/home/cis550/db");

		Link link = new Link("source", "TYPE", "dest", 1.2);
		Set<Link> newLinks = new HashSet<Link>();
		newLinks.add(link);

		Links links = new Links("source", newLinks);

		LinksDA lDA = new LinksDA();

		//lDA.store(links);

		//System.out.println(lDA.fetch("work"));
		System.out.println(lDA.getSize());
		//lDA.delete(links.getSource());
		for(Links storedLink : lDA.fetchAll()) {
			if(storedLink.getSource().equals("generated3.json/new_data/from")) {
				System.out.println(storedLink);
				for(Link relation : storedLink.getRelations()) {
					System.out.println(relation);
				}
			}
		}
		DBWrapper.close();
	}

}
