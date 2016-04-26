package extractor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMParser {

  public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {

    File file = new File(args[0]);
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbf.newDocumentBuilder();
	
	dBuilder.setEntityResolver(new EntityResolver() {

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            System.out.println("Ignoring " + publicId + ", " + systemId);
            return new InputSource(new StringReader(""));
        }
    });
	
	Document doc = dBuilder.parse(file);
	doc.normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
//	Map contents = new LinkedHashMap();
	JSONObject contents = new JSONObject();
	String prefix = "example.xml/content/";
	JSONObject returned = null;
	
//	NodeList nodeList = doc.getChildNodes();
//	for (int count = 0; count < nodeList.getLength(); count++) {
//		System.out.println(nodeList.item(count).getNodeName());
//	}
	if (doc.hasChildNodes()) {
//		NodeList children = doc.getChildNodes();
//		for(int i = 0; i<children.getLength();i++){
			returned = constructNode(doc.getChildNodes(), contents, prefix);
			System.out.println("RETURNED IN MAIN: "+returned);
//		}
	}
	
	contents.put(prefix+doc.getDocumentElement().getNodeName(), returned);
//	System.out.println(contents.toString());
  }

  private static JSONObject constructNode(NodeList nodeList, JSONObject contents, String prefix) {

    for (int count = 0; count < nodeList.getLength(); count++) {
    	JSONObject temp = new JSONObject();
    	Node tempNode = nodeList.item(count);
	  	
    	String current_node = prefix+tempNode.getNodeName();
		System.out.println("current node : "+current_node+" Node type : "+tempNode.getNodeType());

    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
			NodeList children = tempNode.getChildNodes();
			
			/**
			 * Add text value to map
			 */
			for (int i = 0; i < children.getLength(); i++){
				Node child = children.item(i);
				if(child.getNodeType() == Node.TEXT_NODE){
					if(child.getTextContent().trim().length() > 0){
						System.out.println("Node Value = "+child.getTextContent());
						contents.put(current_node, child.getTextContent());
					}
				}
			}
			
			/**
			 * Add attributes to map
			 */
			if (tempNode.hasAttributes()) {
	
				// get attributes names and values
				NamedNodeMap nodeMap = tempNode.getAttributes();
	
				for (int i = 0; i < nodeMap.getLength(); i++) {
	
					Node node = nodeMap.item(i);
//					System.out.println("attr name : " + node.getNodeName());
//					System.out.println("attr value : " + node.getNodeValue());
					temp.put(current_node+"/"+node.getNodeName(), node.getNodeValue());
	
				}
	
			}
			
			/**
			 * Add child maps
			 */
			if (tempNode.hasChildNodes()) {
				
//				for(int j = 0; j<children.getLength();j++){
//					System.out.println("calling children");
					JSONObject returned = constructNode(children, temp, prefix);
					temp.put(current_node, returned);
//				}
	
			}
			contents.put(current_node, temp);
    	}
    }
//    System.out.println("RETURNING "+contents.toJSONString());
    return contents;
    
  }

}