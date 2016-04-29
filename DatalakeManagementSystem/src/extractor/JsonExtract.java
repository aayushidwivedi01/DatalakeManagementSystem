package extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * Extracting content from JSON files.
 * @author Anwesha
 *
 */
 
public class JsonExtract {
	/**
	 * Stores the extracted json from the tree in a LinkedHashMap.
	 * Keys are the full path in the document from the root (file name).
	 * In case of arrays or missing keys, derives a key from the parent node name and assigns it to the value.
	 * @param node
	 * @param names
	 * @param idx
	 * @param json
	 * @return
	 */
	
	private static Map printJson(JsonNode node, ArrayList<String> names, int idx, Map json) {
		Iterator <JsonNode> children = node.elements();
        while (children.hasNext()){
        	JsonNode child = children.next();
        	if(!child.isContainerNode()){
        		//Leaf Node
        		String key = names.get(++idx);
        		json.put(key, child);
        		
        	}else{
        		//container node
        		String current_name = names.get(++idx);
        		Map json_child = new LinkedHashMap();
        		ArrayList<String> field_names = null;
        		
        		if (child.getNodeType() == JsonNodeType.OBJECT){
        			Iterator<String> fieldNames = child.fieldNames();
        	        field_names = new ArrayList<String>();
        	        
        	        while(fieldNames.hasNext()){
        	        	String name = fieldNames.next();
        	        	field_names.add(current_name+"/"+name);
        	        }
        		}
        		else if (child.getNodeType() == JsonNodeType.ARRAY){
        			field_names = new ArrayList<String>();
        			int size = child.size();
        			String [] path_names = current_name.split("/");
        			for (int i = 1; i<= size; i++){
        				field_names.add(current_name+"/"+ path_names[path_names.length-1]+"_element"+i);
        			}
        			
        		}
        		Map returned = printJson(child, field_names, -1, json_child);
        		json.put(current_name, returned);
        	}
        }
        return json;
	}
	
	/**
	 * Calls the extractor with the root node of JSON tree.
	 * Converts into JSON string which can be stored and parsed later.
	 * Alternatively, can also be stored as a JSONObject - But this does not preserve any order.
	 * @param json_string
	 * @param file_name
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String extractJson(String json_string, String file_name) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
        // use the ObjectMapper to read the json string and create a tree
        JsonNode node = mapper.readTree(json_string);
        
        Iterator<String> fieldNames = node.fieldNames();
        ArrayList<String> names = new ArrayList<String>();
        
        while(fieldNames.hasNext()){
        	String name = new File(file_name).getName()+"/"+fieldNames.next();
        	names.add(name);
        	System.out.println(name);
        }
        
        if(names.size() == 0){
        	for (int k = 0; k < node.size(); k++){
        		names.add(new File(file_name).getName()+"/DONOTLINK_"+k);
        	}
        }
        
        Map json = new LinkedHashMap();
        Map returned = printJson(node, names, -1, json);
        
        JSONObject json_obj = new JSONObject(json);
        String jobj = JSONObject.toJSONString(returned);
        return jobj;
	}
}