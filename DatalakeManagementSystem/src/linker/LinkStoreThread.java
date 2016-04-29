package linker;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

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
			Set<JSONObject> linkSet = new HashSet<JSONObject>();
			if(storedLinks.getSource().equals("sample3.json/root3/content/text")) {
				for(JSONObject json : storedLinks.getRelations()) {
					System.out.println(json);
				}
			}
			linkSet.addAll(storedLinks.getRelations());
			storedLinks.getRelations().addAll(linkSet);
			
			if(storedLinks.getSource().equals("sample3.json/root3/content/text")) {
				System.out.println();
				for(JSONObject json : storedLinks.getRelations()) {
					System.out.println(json);
				}
			}
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
