package extractor;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class XMLExtract extends DefaultHandler {
	ArrayList<String> list = null;
	static String filename = null;
	String content = "content";
	Stack<Map> contents = new Stack<Map>();
	
	//Stores the leaf nodes for indexing
	ArrayList<String> leaf_nodes = null;
	
	//Stores the intermediate nodes as well
	ArrayList <String>all_nodes = null;
	
	public ArrayList<String> getLeafNodes(){
		return leaf_nodes;
	}
	
	public ArrayList<String> getAllNodes(){
		return all_nodes;
	}
   
	 public void startDocument( ) throws SAXException {
	      list = new ArrayList<String>();
	      
	      leaf_nodes = new ArrayList<String>();
	      all_nodes = new ArrayList<String>();
	      
	      list.add(filename);
	      list.add(content);
	      
	   }
	 
	   public void endDocument( ) throws SAXException {
//	      System.out.println( "SAX Event: END DOCUMENT" );
//	      String jobj = JSONObject.toJSONString(last_map);
//	      System.out.println(jobj);
//	      System.out.println("LEAF VALUES");
//	      System.out.println(leaf_nodes.size());
//	      for(String key : leaf_nodes){
//	    	  System.out.println(key);
//	      }
	      
//	      System.out.println("ALL VALUES");
//	      System.out.println(all_nodes.size());
//	      for(String key : all_nodes){
//	    	  System.out.println(key);
//	      }
	   }
	   
	   /**
	    * Adds the current element to the path.
	    * Creates a new LinkedHashMap and adds to the stack.
	    * Adds any attribute value pairs to the hash map.
	    */
	   public void startElement( String namespaceURI, String localName, String qName, Attributes attr ) throws SAXException {
//		   System.out.println( "SAX Event: Start ELEMENT[ " +
//	                  localName + " ]" );
		   	list.add(localName);
//		   	Map current_element = new LinkedHashMap();
		   	String current_path = getList();
//		   	
		   	for ( int i = 0; i < attr.getLength(); i++ ){
		   		String key = current_path+"/"+attr.getLocalName(i);
		   		
//		   		current_element.put(key, attr.getValue(i));
//		   		System.out.println("Attribute - "+key+" : "+attr.getValue(i));
		   		
		   		leaf_nodes.add(key+" : "+attr.getValue(i));
		   		all_nodes.add(key+" : "+attr.getValue(i));
		   	}
//		   	
//		   	contents.add(current_element);
		   	
	   }
	   
	   public void endElement( String namespaceURI, String localName, String qName ) throws SAXException {
		   
//	      System.out.println( "SAX Event: END ELEMENT[ " +
//	                  localName + " ]" );
	      
	      String key = getList();
	      
	      if(!leaf_nodes.contains(key)){
	    	  all_nodes.add(key+" : DO_NOT_LINK_THIS_VALUE");
	      }
//	      
	      int idx = list.lastIndexOf(localName);
	      list.remove(idx);
//	      
//	      if (last_map != null){
//	    	  contents.peek().put(key, last_map);
//	      }
//	      
//	      last_map = contents.pop();
	   }
	   
	   /**
	    * Adds current_path/textvalue() to the current LinkedHashMap
	    */
	   public void characters( char[] ch, int start, int length ) throws SAXException {
		  StringBuilder s = new StringBuilder();
		  int i = start;
		  for(int j= 0; j<length;j++){
			  s.append(ch[i++]);
		  }
		 
		  
//		  Map current_element = contents.peek();
		  String current_path = getList();
//		  
		  String key = current_path;
		  if(s.toString().trim().length() > 0){
//			  System.out.println("TEXT -  "+key+" : "+s.toString());
			  leaf_nodes.add(key+" : "+s.toString());
			  all_nodes.add(key+" : "+s.toString());
		  }
//		  current_element.put(key, s.toString());
	   }
	   
	   public String getList(){
		   StringBuilder s = new StringBuilder();
		   for (String element : list){
			   s.append(element+"/");
		   }
		   int idx = s.toString().lastIndexOf("/");
		   s.setCharAt(idx, ' ');
		   return s.toString().trim();
	   }
	   
	   public void extractXML(String filename){
		   try {
		         // Create SAX 2 parser...
		         XMLReader xr = XMLReaderFactory.createXMLReader();
		         // Set the ContentHandler...
		         xr.setContentHandler( new XMLExtract() );
		            // Parse the file...
		         xr.parse( new InputSource(
		               new FileReader( filename )) );
		      }catch ( Exception e )  {
		         e.printStackTrace();
		      }
	   }
	   
//	   public static void main( String[] argv ){
//	      System.out.println( "Example1 SAX Events:" );
//	      filename = "example.xml";
//	      try {
//	         // Create SAX 2 parser...
//	         XMLReader xr = XMLReaderFactory.createXMLReader();
//	         // Set the ContentHandler...
//	         xr.setContentHandler( new SAXExample() );
//	            // Parse the file...
//	         xr.parse( new InputSource(
//	               new FileReader( "/home/cis455/Downloads/web_worker2.xml" )) );
//	      }catch ( Exception e )  {
//	         e.printStackTrace();
//	      }
//	   }

	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException{
//		SAXParserFactory spf = SAXParserFactory.newInstance();
//		SAXParser saxParser = spf.newSAXParser();
//		XMLReader xmlReader = saxParser.getXMLReader();
//		xmlReader.setContentHandler(new SAXExample());
//		
//		xmlReader.parse(new InputSource(
//               new FileReader( "/home/cis455/Downloads/web_worker1.xml" )));
//		
//	   
//	}
}