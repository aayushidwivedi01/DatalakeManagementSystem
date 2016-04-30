package linker;

import bean.Links;
import storage.LinksDA;

public class LinkStoreThread extends Thread {

	private LinksDA linksDA;
	private Links links;
	private int threadID;

	public LinkStoreThread(int id) {
		linksDA = new LinksDA();
		this.threadID = id;
	}

	public void run(Links linksToStore) {
		// System.out.println("Initialted store thread");
		links = linksToStore;
		run();
	}

	public void run() {
		// System.out.println("Running store thread");
		Links storedLinks = linksDA.fetch(links.getSource());
		if (storedLinks == null) {
			linksDA.store(links);
		} else {
			//Set<JSONObject> linkSet = new HashSet<JSONObject>();
			//linkSet.addAll(storedLinks.getRelations());
			storedLinks.getRelations().addAll(links.getRelations());
			linksDA.update(storedLinks);
		}
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	public int getthreadID() {
		return threadID;
	}

	public void setthreadID(int id) {
		this.threadID = id;
	}

}
