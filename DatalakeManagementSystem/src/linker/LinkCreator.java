package linker;

import java.util.ArrayList;
import java.util.Arrays;
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

	/** The stopwords. */
	private static ArrayList<String> stopwords = new ArrayList<String>(Arrays.asList(("a,about,above,"
			+ "after,again,against,all,am,an,and,any,are," + "aren't,as,at,be,because,been,before,being,"
			+ "below,between,both,but,by,could," + "couldn't,did,didn't,do,does,doesn't,doing,don't,"
			+ "down,during,each,few,for,from,further,had,hadn't," + "has,hasn't,have,haven't,having,he,he'd,he'll,he's,"
			+ "her,here,here's,hers,herself,him,himself,his," + "how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,"
			+ "it's,its,itself,let's,me,more,mustn't,my,myself," + "no,nor,of,off,on,once,only,or,other,ought,our,ours,"
			+ "ourselves,out,over,own,shan't,she,she'd,she'll,"
			+ "she's,should,shouldn't,so,some,such,than,that,that's,"
			+ "the,their,theirs,them,themselves,then,there,there's,"
			+ "these,they,they'd,they'll,they're,they've,this,those,"
			+ "through,to,too,under,until,up,very,was,wasn't,we,we'd," + "we'll,we're,we've,were,weren't,what's,when's,"
			+ "where's,while,who's,why's,with," + "won't,would,wouldn't,you,you'd,you'll,you're,you've,your,"
			+ "yours,yourself,yourselves,").split(",")));
	private static Map<LinkType, String> linkType = new EnumMap<LinkType, String>(LinkType.class);
	private static Map<LinkType, Double> linkWeight = new EnumMap<LinkType, Double>(LinkType.class);
	private static final double DEFAULT_WEIGHT = 2.0;
	private static final double IMPROVED_WEIGHT = 1.0;
	private static final String DNL = "donotlink";
	private static final int MAX_LINK_SET_SIZE = 1000;
	private static boolean shouldContinue;
	private Stemmer stemmer;
	private Queue<ForwardIndexPair> fIndexQueue;
	private Queue<Set<Link>> linksQueue;
	private LinkSaver linkSaver;
	private Set<Link> links;

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

	public LinkCreator(Queue<ForwardIndexPair> fIndexQueue, Queue<Set<Link>> linksQueue) {
		this.stemmer = new Stemmer();
		this.fIndexQueue = fIndexQueue;
		this.linksQueue = linksQueue;
		this.links = new HashSet<Link>();
		shouldContinue = true;
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

	public static void setShouldContinue(boolean shouldContinueArg) {
		shouldContinue = shouldContinueArg;
	}

	private String stem(String word) {
		stemmer.add(word.toCharArray(), word.length());
		stemmer.stem();
		String stemmedWord = stemmer.toString();
		return stemmedWord;
	}

	private Set<Link> getParentChildLinks(ForwardIndex f, PathAttribute pathAttribute) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (pathAttribute.getHasAttribute()) {
			source = pathAttribute.getPath();
			type = LinkType.IS_PARTENT;
			dest = f.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = f.getPath();
			type = LinkType.IS_CHILD;
			dest = pathAttribute.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
		}
		return links;
	}

	private Set<Link> getAttributePathLinks(ForwardIndex f, PathAttribute pathAttribute) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (pathAttribute.getHasAttribute()) {
			source = pathAttribute.getAttribute();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = f.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = pathAttribute.getAttribute();
			links.add(new Link(dest, linkType.get(type), source, linkWeight.get(type)));
		}
		return links;
	}

	private Set<Link> getValueAttributeLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(pathAttributeF2.getAttribute())) {
			source = f1.getValue();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
		} else if (f1.getValue().contains(pathAttributeF2.getAttribute())) {
			source = f1.getValue();
			type = LinkType.MATCHES_ATTRIBUTE;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
		}

		return links;
	}

	private Set<Link> getValuePathLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(f2.getPath())) {
			source = f1.getValue();
			type = LinkType.MATCHES_PATH;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
		} else if (f1.getValue().contains(f2.getPath())) {
			source = f1.getValue();
			type = LinkType.MATCHES_PATH;
			dest = f2.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
			source = f2.getPath();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
		}
		return links;
	}

	private Set<Link> getValueFileLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		if (f1.getValue().equalsIgnoreCase(pathAttributeF2.getFile())) {
			source = f1.getValue();
			type = LinkType.MATCHES_FILENAME;
			dest = pathAttributeF2.getFile();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = pathAttributeF2.getFile();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
		} else if (f1.getValue().contains(pathAttributeF2.getFile())) {
			source = f1.getValue();
			type = LinkType.MATCHES_FILENAME;
			dest = pathAttributeF2.getFile();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
			source = pathAttributeF2.getFile();
			type = LinkType.MATCHES_CONTENT;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
		}

		return links;
	}

	private Set<Link> getValueContainslinks(ForwardIndex f1) {
		Set<Link> links = new HashSet<Link>();
		LinkType type;
		String source;
		String dest;
		String[] tokens = f1.getValue().trim().split("\\s+");
		for (String token : tokens) {
			token = token.toLowerCase().replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
			if (stopwords.contains(token) || token.equals(" ")) {
				continue;
			}
			token = stem(token);
			// System.out.println(token);
			source = f1.getPath();
			type = LinkType.CONTAINS;
			dest = token;
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
			source = token;
			type = LinkType.IS_CONTAINED_IN;
			dest = f1.getPath();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type) + DEFAULT_WEIGHT));
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
		// System.out.println("CREATED LINKS - "+ links.size());
		return links;
	}

	public Set<Link> createLinks(ForwardIndex f1, ForwardIndex f2) {
		Set<Link> links = new HashSet<Link>();
		// split index paths into filename, path and attribute
		PathAttribute pathAttributeF1 = new PathAttribute(f1.getPath());
		PathAttribute pathAttributeF2 = new PathAttribute(f2.getPath());
		if (f1.getValue() != null && !f1.getValue().equalsIgnoreCase(DNL)) {
			// generate links from f1 to f2
			if (pathAttributeF1.getHasAttribute() && pathAttributeF2.getHasAttribute()) {
				links.addAll(getValueAttributeLink(f1, f2, pathAttributeF1, pathAttributeF2));
			}
			links.addAll(getValuePathLink(f1, f2, pathAttributeF1, pathAttributeF2));
			links.addAll(getValueFileLink(f1, f2, pathAttributeF1, pathAttributeF2));
		}
		if (f2.getValue() != null && !f2.getValue().equalsIgnoreCase(DNL)) {
			// generate links from f2 to f1
			if (pathAttributeF1.getHasAttribute() && pathAttributeF2.getHasAttribute()) {
				links.addAll(getValueAttributeLink(f2, f1, pathAttributeF2, pathAttributeF1));
			}
			links.addAll(getValuePathLink(f2, f1, pathAttributeF2, pathAttributeF1));
			links.addAll(getValueFileLink(f2, f1, pathAttributeF2, pathAttributeF1));
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
		// System.out.println("Thread - " + Thread.currentThread().getName() + "
		// - started!");
		while (shouldContinue) {
			//System.out.println("Thread - " + Thread.currentThread().getName() + " - in while");
			synchronized (fIndexQueue) {
				//System.out.println("Thread - " + Thread.currentThread().getName() + " - in fIndexQueue");
				if (fIndexQueue.getSize() > 0) {
					ForwardIndexPair fIndexPair = fIndexQueue.dequeue();

					if (fIndexPair != null) {
						System.out.println(fIndexPair.getF1() + " " + fIndexPair.getF2());
						links.addAll(createLinks(fIndexPair));
						System.out.println(links.size());
						System.out.println(fIndexQueue.getSize());
						if (links.size() > MAX_LINK_SET_SIZE) {
							linksQueue.enqueue(links);
							links = new HashSet<Link>();
						}
					}
				} else {
					try {
						//System.out.println("Thread - " + Thread.currentThread().getName() + " - waiting for fIndexQueue");
						fIndexQueue.wait();
						//System.out.println("Thread - " + Thread.currentThread().getName() + " - done waiting");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (links.size() > 0) {
			System.out.println("Thread - " + Thread.currentThread().getName() + " - enquing before ending!");
			linksQueue.enqueue(links);
		}
		System.out.println("Thread - " + Thread.currentThread().getName() + " - ended!");
	}
}