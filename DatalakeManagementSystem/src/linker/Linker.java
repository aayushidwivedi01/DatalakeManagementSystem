package linker;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import bean.ForwardIndex;
import bean.Link;
import bean.PathAttribute;

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

public class Linker {

	private static enum LinkType {
		CONTAINS, IS_CONTAINED_IN, IS_SAME, IS_PARTENT, IS_CHILD, MATCHES_ATTRIBUTE, MATCHES_CONTENT, MATCHES_FILENAME, MATCHES_PATH
	}

	private static Map<LinkType, String> linkType = new EnumMap<LinkType, String>(LinkType.class);
	private static Map<LinkType, Double> linkWeight = new EnumMap<LinkType, Double>(LinkType.class);
	private static final double DEFAULT_WEIGHT = 1.0;

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

	private List<Link> getParentChildLinks(ForwardIndex f, PathAttribute pathAttribute) {
		List<Link> links = new ArrayList<Link>();
		LinkType type;
		String source;
		String dest;
		if (pathAttribute.getHasAttribute()) {
			source = pathAttribute.getPath();
			type = LinkType.IS_PARTENT;
			dest = pathAttribute.getAttribute();
			links.add(new Link(source, linkType.get(type), dest, linkWeight.get(type)));
			source = pathAttribute.getPath();
			type = LinkType.IS_CHILD;
			dest = f.getPath();
			links.add(new Link(dest, linkType.get(type), source, linkWeight.get(type)));
		}
		return links;
	}

	private List<Link> getValueAttributeLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		List<Link> links = new ArrayList<Link>();
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

	private List<Link> getValuePathLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		List<Link> links = new ArrayList<Link>();
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

	private List<Link> getValueFileLink(ForwardIndex f1, ForwardIndex f2, PathAttribute pathAttributeF1,
			PathAttribute pathAttributeF2) {
		List<Link> links = new ArrayList<Link>();
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

	private List<Link> getValueContainsLinks(ForwardIndex f1) {
		List<Link> links = new ArrayList<Link>();
		LinkType type;
		String source;
		String dest;
		String[] tokens = f1.getValue().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		for (String token : tokens) {
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

	private List<Link> createLinks(ForwardIndex f1, ForwardIndex f2) {
		List<Link> links = new ArrayList<Link>();
		// split index paths into filename, path and attribute
		PathAttribute pathAttributeF1 = new PathAttribute(f1.getPath());
		PathAttribute pathAttributeF2 = new PathAttribute(f2.getPath());
		// generate parent child links for f1 and f2
		links.addAll(getParentChildLinks(f1, pathAttributeF1));
		links.addAll(getParentChildLinks(f2, pathAttributeF2));
		// generate links from f1 to f2
		links.addAll(getValueAttributeLink(f1, f2, pathAttributeF1, pathAttributeF2));
		links.addAll(getValuePathLink(f1, f2, pathAttributeF1, pathAttributeF2));
		links.addAll(getValueFileLink(f1, f2, pathAttributeF1, pathAttributeF2));

		// generate links from f2 to f1
		links.addAll(getValueAttributeLink(f2, f1, pathAttributeF2, pathAttributeF1));
		links.addAll(getValuePathLink(f2, f1, pathAttributeF2, pathAttributeF1));
		links.addAll(getValueFileLink(f2, f1, pathAttributeF2, pathAttributeF1));

		return links;
	}

	private static void printLinks(List<Link> links) {

		for (Link link : links) {
			System.out.println(link);
		}
		System.out.println();
	}

	public static void main(String[] args) {

		// test path, attribute and filename extraction
		System.out.println("Path Atrribute Test - ");
		PathAttribute pa = new PathAttribute("user1_doc.xml/root/content");
		System.out.println("Source - user1_doc.xml/root/content");
		System.out.println(pa);
		pa = new PathAttribute("user1_doc.xml/root");
		System.out.println("Source - user1_doc.xml/root");
		System.out.println(pa);
		pa = new PathAttribute("user1_doc.xml");
		System.out.println("Source - user1_doc.xml");
		System.out.println(pa);
		System.out.println();

		Linker linker = new Linker();
		// ForwardIndexDA fIndexDA = new ForwardIndexDA();
		ForwardIndex f1 = null, f2 = null;
		PathAttribute pathAttributeF1 = null, pathAttributeF2 = null;

		// test parent child links
		f1 = new ForwardIndex("user1_doc.xml/root/content", "Helo There, my fiends?");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "miami");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Parent to Child Links Test - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.getParentChildLinks(f1, pathAttributeF1));
		printLinks(linker.getParentChildLinks(f2, pathAttributeF2));

		// test value to attribute links
		f1 = new ForwardIndex("user1_doc.xml/root/content", "Helo There, my fiends? column1");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "content");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Value to Attribute Links Test - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.getValueAttributeLink(f1, f2, pathAttributeF1, pathAttributeF2));
		printLinks(linker.getValueAttributeLink(f2, f1, pathAttributeF2, pathAttributeF1));

		// test value to path links
		f1 = new ForwardIndex("user1_doc.xml/root/content",
				"Helo There, my fiends? user2_expenses.csv/root/table/row1/column1");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "content");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Value to Path Links Test - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.getValuePathLink(f1, f2, pathAttributeF1, pathAttributeF2));
		printLinks(linker.getValuePathLink(f2, f1, pathAttributeF2, pathAttributeF1));

		// test value to filename links
		f1 = new ForwardIndex("user1_doc.xml/root/content", "Helo There, my fiends? column1");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "user1_doc.xml");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Value to Filename Links Test - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.getValueFileLink(f1, f2, pathAttributeF1, pathAttributeF2));
		printLinks(linker.getValueFileLink(f2, f1, pathAttributeF2, pathAttributeF1));

		// test value contains links
		f1 = new ForwardIndex("user1_doc.xml/root/content", "Helo There, my fiends?");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "miami");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Value contains tokens Link Test - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.getValueContainsLinks(f1));
		printLinks(linker.getValueContainsLinks(f2));

		// test createLinks method
		f1 = new ForwardIndex("user1_doc.xml/root/content", "Helo There, my fiends?");
		f2 = new ForwardIndex("user2_expenses.csv/root/table/row1/column1", "miami");
		pathAttributeF1 = new PathAttribute(f1.getPath());
		pathAttributeF2 = new PathAttribute(f2.getPath());
		System.out.println("Creating Links - ");
		System.out.println("Indices - ");
		System.out.println(f1);
		System.out.println(f2);
		printLinks(linker.createLinks(f1, f2));
	}
}
