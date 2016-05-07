package linker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.Link;
import bean.Links;
import storage.LinksDA;

public class LinkSaver extends Thread {

	private LinksDA linksDA;

	public LinkSaver() {
		this.linksDA = new LinksDA();
	}

	public void saveLinks(Set<Link> links) {
		// merge links and store them
		Map<String, Links> mapOfLinks = mergeLinks(links);
		System.out.println("Unique sources - " + mapOfLinks.size());
		long startTime = System.nanoTime();
		storeLinks(mapOfLinks);
		long endTime = System.nanoTime();
		System.out.println("Time to store - " + (endTime - startTime) / 1000000 + " mSec");
	}

	private void addLink(Link link, Map<String, Links> mapOfLinks) {
		if (mapOfLinks.containsKey(link.getSource())) {
			mapOfLinks.get(link.getSource()).getRelations().add(link);

		} else {
			Links links = new Links(link.getSource(), new HashSet<Link>());
			links.getRelations().add(link);
			mapOfLinks.put(link.getSource(), links);
		}
	}

	private void addAllLink(List<Link> links, Map<String, Links> mapOfLinks) {
		for (Link link : links) {
			addLink(link, mapOfLinks);
		}
	}

	public Map<String, Links> mergeLinks(Set<Link> links) {
		ArrayList<Link> mergedLinks = new ArrayList<Link>(links);

		Map<String, Links> mapOfLinks = new HashMap<String, Links>();
		addAllLink(mergedLinks, mapOfLinks);
		return mapOfLinks;
	}

	public void storeLinks(Map<String, Links> mapOfLinks) {
		for (Map.Entry<String, Links> links : mapOfLinks.entrySet()) {
			Links storedLinks = linksDA.fetch(links.getValue().getSource());
			if (storedLinks == null) {
				linksDA.store(links.getValue());
			} else {
				storedLinks.getRelations().addAll(links.getValue().getRelations());
				linksDA.update(storedLinks);
			}
		}
	}

	// public void storeLinksThreaded(Map<String, Links> mapOfLinks) {
	// boolean done = false;
	// while (!done) {
	// done = true;
	// for (LinkStoreThread thread : linkStoreThreads) {
	// if (thread.getState().equals(Thread.State.RUNNABLE)) {
	// done = false;
	// }
	// }
	// try {
	// Thread.sleep(200);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// System.out.println("All storage threads free");
	// for (Map.Entry<String, Links> links : mapOfLinks.entrySet()) {
	// done = false;
	// while (!done) {
	// for (LinkStoreThread thread : linkStoreThreads) {
	// if (!thread.getState().equals(Thread.State.RUNNABLE)) {
	// done = true;
	// thread.run(links.getValue());
	// break;
	// }
	// }
	// try {
	// Thread.sleep(200);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// System.out.println("done writing");
	// }
}