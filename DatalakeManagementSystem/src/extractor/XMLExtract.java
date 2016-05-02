package extractor;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

/**
 * SAX Based XML Parser
 * 
 * @author Anwesha
 *
 */
public class XMLExtract extends DefaultHandler {
	ArrayList<String> list = null;
	static String filename = null;
	String content = "content";
	Stack<Map> contents = new Stack<Map>();

	// Stores the leaf nodes for indexing
	Multimap<String, String> leaf_nodes = null;

	public Multimap<String, String> getLeafNodes() {
		return leaf_nodes;
	}

	// Stores the intermediate nodes as well
	Multimap<String, String> all_nodes = null;

	public Multimap<String, String> getAllNodes() {
		return all_nodes;
	}

	public void startDocument() throws SAXException {
		list = new ArrayList<String>();

		leaf_nodes = ArrayListMultimap.create();
		all_nodes = ArrayListMultimap.create();

		list.add(filename);

	}

	public void endDocument() throws SAXException {

	}

	/**
	 * Adds the current element to the path. Adds any attribute value pairs to
	 * the hash map.
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		list.add(localName);
		String current_path = getList();

		for (int i = 0; i < attr.getLength(); i++) {
			String key = current_path + "/" + attr.getLocalName(i);

			leaf_nodes.put(key, attr.getValue(i));
			all_nodes.put(key, attr.getValue(i));
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		String key = getList();

		if (!leaf_nodes.containsKey(key)) {
			all_nodes.put(key, "DONOTLINK");
		}
		int idx = list.lastIndexOf(localName);
		list.remove(idx);
	}

	/**
	 * Emits current_path's text value (if any)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		StringBuilder s = new StringBuilder();
		int i = start;
		for (int j = 0; j < length; j++) {
			s.append(ch[i++]);
		}

		String current_path = getList();

		String key = current_path;
		if (s.toString().trim().length() > 0) {
			leaf_nodes.put(key, s.toString());
			all_nodes.put(key, s.toString());
		}
	}

	public String getList() {
		StringBuilder s = new StringBuilder();
		for (String element : list) {
			s.append(element + "/");
		}
		int idx = s.toString().lastIndexOf("/");
		s.setCharAt(idx, ' ');
		return s.toString().trim();
	}

	public void extractXML(String filename) {
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(new XMLExtract());
			xr.parse(new InputSource(new FileReader(filename)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static void main( String[] argv ){
	// System.out.println( "Example1 SAX Events:" );
	// filename = "example.xml";
	// try {
	// // Create SAX 2 parser...
	// XMLReader xr = XMLReaderFactory.createXMLReader();
	// // Set the ContentHandler...
	// xr.setContentHandler( new SAXExample() );
	// // Parse the file...
	// xr.parse( new InputSource(
	// new FileReader( "/home/cis455/Downloads/web_worker2.xml" )) );
	// }catch ( Exception e ) {
	// e.printStackTrace();
	// }
	// }

	// public static void main(String[] args) throws
	// ParserConfigurationException, SAXException, FileNotFoundException,
	// IOException{
	// SAXParserFactory spf = SAXParserFactory.newInstance();
	// SAXParser saxParser = spf.newSAXParser();
	// XMLReader xmlReader = saxParser.getXMLReader();
	// xmlReader.setContentHandler(new SAXExample());
	//
	// xmlReader.parse(new InputSource(
	// new FileReader( "/home/cis455/Downloads/web_worker1.xml" )));
	//
	//
	// }
}