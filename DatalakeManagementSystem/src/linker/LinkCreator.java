package linker;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import bean.ForwardIndex;
import bean.ForwardIndexPair;
import bean.Link;
import bean.Links;
import bean.PathAttribute;
import threads.Queue;
import utils.Stemmer;

/*
 * Linker Steps - 
 * 1. Get a document from flat_document_new collection : doc1
 * 2. Get a document from flat_document collection : doc2
 * 3. For each ForwardIndex : f1 in doc1 - 
 * 		For each ForwardIndex : f2 in doc2 - 
 * 			Link f1 and f2
 * 4. Store doc1 in flat_document collection
 * 5. Delete doc1 from flat_document_new collection
 * 6. Go Back to step 1
 */

/*
 * You should at least link two items if:
 * 
 * You have a parent data item and a nested data item, e.g., the parent is a
 * JSON map and the nested data item is one of its properties.
 * 
 * One attribute’s value is the same as the key attribute of another data
 * item
 * 
 * One attribute’s value is the same as the file or path name of another
 * data item
 * 
 * Both attributes have the same value, which is the ID of a known entity
 * 
 * You may also want to look at measures of approximate matches among
 * strings, including string edit distance and n-grams.
 * 
 */

public class LinkCreator extends Thread {

	private static enum LinkType {
		CONTAINS, IS_CONTAINED_IN, IS_SAME, IS_PARTENT, IS_CHILD, MATCHES_ATTRIBUTE, MATCHES_CONTENT, MATCHES_FILENAME, MATCHES_PATH
	}

	private static Map<LinkType, String> linkType = new EnumMap<LinkType, String>(
			LinkType.class);
	private static Map<LinkType, Double> linkWeight = new EnumMap<LinkType, Double>(
			LinkType.class);
	private static final double DEFAULT_WEIGHT = 1.0;
	private static final String DNL = "donotlink";
	private static final int MAX_LINK_SET_SIZE = 10000;
	private Stemmer stemmer;
	private Queue<ForwardIndexPair> fIndexQueue;
	private Queue<Set<Link>> linksQueues;
	private LinkSaver linkSaver;
	private Set<Link> links;
	private boolean shouldContinue;

	static {
		linkType.put(LinkType.CONTAINS, "CONTAINS");
		linkType.put(LinkType.IS_SAME, "IS_SAME");
		linkType.put(LinkType.IS_PARTENT, "IS_PARTENT");
		linkType.put(LinkType.IS_CHILD, "IS_CHILD");
		linkType.put(LinkType.MATCHES_ATTRIBUTE, "MATCHES_ATTRIBUTE");
		linkType.put(LinkType.MATCHES_CONTENT, "MATCHES_CONTENT");
		linkType.put(LinkType.MATCHES_FILENAME, "MATCHES_FILENAME");
		linkType.put(LinkType.MATCHES_PATH, "MATCHES_PATH");
		linkType.put(LinkType.IS_CONTAINED_IN, "IS_CONTAINED_IN");

		linkWeight.put(LinkType.CONTAINS, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.IS_SAME, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.IS_PARTENT, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.IS_CHILD, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.MATCHES_ATTRIBUTE, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.MATCHES_CONTENT, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.MATCHES_FILENAME, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.MATCHES_PATH, DEFAULT_WEIGHT);
		linkWeight.put(LinkType.IS_CONTAINED_IN, DEFAULT_WEIGHT);
	}

	public LinkCreator(Queue<ForwardIndexPair> fIndexQueue,
			Queue<Set<Link>> linksQueues) {
		this.stemmer = new Stemmer();
		this.fIndexQueue = fIndexQueue;
		this.linksQueues = linksQueues;
		this.links = new HashSet<Link>();
		this.shouldContinue = true;
	}

	public static Map<LinkType, String> getLinkType() {
		return linkType;
	}

	public static Map<LinkType, Double> getLinkWeight() {
		return linkWeight;
	}

	public static double getDefaultWeight() {
		return DEFAULT_WEIGHT;
	}

	public static String getDnl() {
		return DNL;
	}

	public static int getMaxLinkSetSize() {
		return MAX_LINK_SET_SIZE;
	}

	public Stemmer getStemmer() {
		return stemmer;
	}

	public Queue<ForwardIndexPair> getfIndexQueue() {
		return fIndexQueue;
	}

	public LinkSaver getLinkSaver() {
		return linkSaver;
	}

	public Set<Link> getLinks() {
		return links;
	}

	public boolean isShouldContinue() {
		return shouldContinue;
	}

	public void setShouldContinue(boolean shouldContinue) {
		this.shouldContinue = shouldContinue;
	}

	private String stem(String word) {
		stemmer.add(word.toCharArray(), word.length());
		stemmer.stem();
		String stemmedWord = stemmer.toString();
		return stemmedWord;
	}

	private Set<Link> getParentChildLinks(ForwardIndex f,
			PathAttribute pathAttribute) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (pathAttribute.getHasAttribute()) {
			source = pathAttribute.getPath();
			type = LinkType.IS_PARTENT;
			dest = f.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
			source = f.getPath();
			type = LinkType.IS_CHILD;
			dest = pathAttribute.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
		}
		return links;
	}

	private Set<Link> getAttributePathLinks(ForwardIndex f,
			PathAttribute pathAttribute) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (pathAttribute.getHasAttribute()) {
			source = pathAttribute.getAttribute();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
			source = f.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = pathAttribute.getAttribute();
			links.add(new Link(dest, linkType.get(type), source, linkWeight
					.get(type)));
		}
		return links;
	}

	private Set<Link> getValueAttributeLink(ForwardIndex f1, ForwardIndex f2,
			PathAttribute pathAttributeF1, PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(pathAttributeF2.getAttribute())) {
			source = f1.getValue();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
		} else if (f1.getValue().contains(pathAttributeF2.getAttribute())) {
			source = f1.getValue();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
		}

		return links;
	}

	private Set<Link> getValuePathLink(ForwardIndex f1, ForwardIndex f2,
			PathAttribute pathAttributeF1, PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(f2.getPath())) {
			source = f1.getValue();
			type = LinkType.MATCHES_PATH;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
		} else if (f1.getValue().contains(f2.getPath())) {
			source = f1.getValue();
			type = LinkType.MATCHES_PATH;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
		}
		return links;
	}

	private Set<Link> getValueFileLink(ForwardIndex f1, ForwardIndex f2,
			PathAttribute pathAttributeF1, PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(pathAttributeF2.getFile())) {
			source = f1.getValue();
			type = LinkType.MATCHES_FILENAME;
			dest = pathAttributeF2.getFile();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
			source = pathAttributeF2.getFile();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type)));
		} else if (f1.getValue().contains(pathAttributeF2.getFile())) {
			source = f1.getValue();
			type = LinkType.MATCHES_FILENAME;
			dest = pathAttributeF2.getFile();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
			source = pathAttributeF2.getFile();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
		}

		return links;
	}

	private Set<Link> getValueContainslinks(ForwardIndex f1) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		String[] tokens = f1.getValue().replaceAll("[^a-zA-Z0-9 ]", "")
				.toLowerCase().split("\\s+");
		for (String token : tokens) {
			token = stem(token);
			// System.out.println(token);
			source = f1.getPath();
			type = LinkType.CONTAINS;
			dest = token;
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
			source = token;
			type = LinkType.IS_CONTAINED_IN;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight
					.get(type) + DEFAULT_WEIGHT));
		}
		return links;
	}

	public Set<Link> createSelfLinks(ForwardIndex f) {

		Set<Link> links = new HashSet<Link>();
		// split index paths into filename, path and attribute
		PathAttribute pathAttributeF1 = new PathAttribute(f.getPath());
		// generate parent child links for f1 and f2
		links.addAll(getParentChildLinks(f, pathAttributeF1));
		// generate attribute path links for f1 and f2
		links.addAll(getAttributePathLinks(f, pathAttributeF1));
		if (f.getValue() == null) {
			System.out.println(f);
		}
		if (!f.getValue().equalsIgnoreCase(DNL)) {
			// generate all contains links
			links.addAll(getValueContainslinks(f));
		}

		return links;
	}

	public Set<Link> createLinks(ForwardIndexPair fIndexPair) {
		Set<Link> links;
		if (fIndexPair.getF2() == null) {
			links = createSelfLinks(fIndexPair.getF1());
		} else {
			links = createLinks(fIndexPair.getF1(), fIndexPair.getF2());
		}
		return links;
	}

	public Set<Link> createLinks(ForwardIndex f1, ForwardIndex f2) {
		Set<Link> links = new HashSet<Link>();
		// split index paths into filename, path and attribute
		PathAttribute pathAttributeF1 = new PathAttribute(f1.getPath());
		PathAttribute pathAttributeF2 = new PathAttribute(f2.getPath());
		if (f1.getValue() != null && !f1.getValue().equalsIgnoreCase(DNL)) {
			// generate links from f1 to f2
			links.addAll(getValueAttributeLink(f1, f2, pathAttributeF1,
					pathAttributeF2));
			links.addAll(getValuePathLink(f1, f2, pathAttributeF1,
					pathAttributeF2));
			links.addAll(getValueFileLink(f1, f2, pathAttributeF1,
					pathAttributeF2));
		}
		if (f2.getValue() != null && !f2.getValue().equalsIgnoreCase(DNL)) {
			// generate links from f2 to f1
			links.addAll(getValueAttributeLink(f2, f1, pathAttributeF2,
					pathAttributeF1));
			links.addAll(getValuePathLink(f2, f1, pathAttributeF2,
					pathAttributeF1));
			links.addAll(getValueFileLink(f2, f1, pathAttributeF2,
					pathAttributeF1));
		}

		return links;
	}

	public void printLinks(Map<String, Links> mapOfLinks) {

		for (Map.Entry<String, Links> link : mapOfLinks.entrySet()) {
			System.out.println(link);
		}
		System.out.println();
	}

	public void printLinks(Set<Link> links) {

		for (Link link : links) {
			System.out.println(link);
		}
		System.out.println();
	}

	public void run() {
		System.out.println("Thread - " + Thread.currentThread().getName()
				+ " - started!");
		while (shouldContinue) {
			synchronized (fIndexQueue) {
				if (fIndexQueue.getSize() == 0) {
					try {
						fIndexQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					ForwardIndexPair fIndexPair = fIndexQueue.dequeue();
					if (fIndexPair != null) {
						links.addAll(createLinks(fIndexPair));
						if (links.size() > MAX_LINK_SET_SIZE) {
							// linkSaver.saveLinks(links);
							linksQueues.enqueue(links);
							links = new HashSet<Link>();
						}
					}
				}
			}
		}
		System.out.println("Thread - " + Thread.currentThread().getName()
				+ " - ended!");
		linksQueues.enqueue(links);
	}
}