package linker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import bean.Link;
import bean.Links;
import storage.LinksDA;
import threads.Queue;

public class LinkSaver extends Thread {

	private LinksDA linksDA;
	private boolean shouldContinue;
	private Queue<Set<Link>> linksQueue;

	public LinkSaver(Queue<Set<Link>> linksQueue) {
		this.linksDA = new LinksDA();
		this.shouldContinue = true;
		this.linksQueue = linksQueue;
	}

	public LinksDA getLinksDA() {
		return linksDA;
	}

	public void setLinksDA(LinksDA linksDA) {
		this.linksDA = linksDA;
	}

	public boolean isShouldContinue() {
		return shouldContinue;
	}

	public void setShouldContinue(boolean shouldContinue) {
		this.shouldContinue = shouldContinue;
	}

	public Queue<Set<Link>> getLinksQueue() {
		return linksQueue;
	}

	public void setLinksQueue(Queue<Set<Link>> linksQueue) {
		this.linksQueue = linksQueue;
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

	private void addAllLink(Set<Link> links, Map<String, Links> mapOfLinks) {
		for (Link link : links) {
			addLink(link, mapOfLinks);
		}
	}

	public Map<String, Links> mergeLinks(Set<Link> links) {
		Map<String, Links> mapOfLinks = new HashMap<String, Links>();
		addAllLink(links, mapOfLinks);
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

	public void run() {
		System.out.println("Link SaverThread - " + Thread.currentThread().getName() + " - started!");
		while (shouldContinue) {
			synchronized (linksQueue) {
				if (linksQueue.getSize() == 0) {
					try {
						System.out.println("Link Saver going to wait");
						linksQueue.wait();
						System.out.println("Link Saver waking up - " + linksQueue.getSize());
						while (linksQueue.getSize() > 0) {
							Set<Link> linkSet = linksQueue.dequeue();
							if (linkSet != null) {
								System.out.println("Link Saver going to save links");
								saveLinks(linkSet);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					while (linksQueue.getSize() > 0) {
						Set<Link> linkSet = linksQueue.dequeue();
						if (linkSet != null) {
							System.out.println("Link Saver going to save links");
							saveLinks(linkSet);
						}
					}
				}
			}
		}
		while (linksQueue.getSize() > 0) {
			Set<Link> linkSet = linksQueue.dequeue();
			if (linkSet != null) {
				System.out.println("Link Saver going to save links");
				saveLinks(linkSet);
			}
		}
		System.out.println("Link SaverThread - " + Thread.currentThread().getName()
				+ " - ended! with links table size - " + linksDA.getSize()+ " " + linksQueue.getSize());
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