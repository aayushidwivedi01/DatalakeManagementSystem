package extractor;

import java.io.IOException;

import java.net.MalformedURLException;
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
//        		System.out.println("Value node for leaf : "+child.isValueNode());
        		json.put(names.get(++idx), child);
//        		System.out.println(names.get(++idx)+" : "+child);
        		
        	}else{
        		//container node
//        		System.out.println(child.getNodeType());
//        		System.out.println("Value node for container : "+child.isValueNode());
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
//        		System.out.println(current_name+" : "+child);
        		Map returned = printJson(child, field_names, -1, json_child);
        		json.put(current_name, returned);
        	}
        }
//        System.out.println("returning from leaf");
//		System.out.println(json.toJSONString());
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
        	names.add(file_name+"/content/"+fieldNames.next());
        }
        
        Map json = new LinkedHashMap();
        Map returned = printJson(node, names, -1, json);
        
        JSONObject json_obj = new JSONObject(json);
//        System.out.println(json_obj.toJSONString());
        
        String jobj = JSONObject.toJSONString(returned);
//        System.out.println(jobj);
        return jobj;
	}
	
    public static void main(String[] args) throws MalformedURLException, IOException {
    	String genreJson = "{\"title\":\"Free Music Archive - Albums\",\"message\":\"\",\"errors\":[1,2],\"total\":\"14002\",\"total_pages\":7001,\"page\":1,\"limit\":\"2\",\"dataset\":[{\"album_id\":\"7596\",\"album_title\":\"!!! - Live @ KEXP 7\\/24\\/2010\",\"album_handle\":\"_-_Live__KEXP_7242010\",\"album_url\":\"http:\\/\\/freemusicarchive.org\\/music\\/___\\/_-_Live__KEXP_7242010\\/\",\"album_type\":\"Live Performance\",\"artist_name\":\"!!!\",\"artist_url\":\"http:\\/\\/freemusicarchive.org\\/music\\/___\\/\",\"album_producer\":null,\"album_engineer\":null,\"album_information\":\"<p>Funk-punk party starters !!! performs live at KEXP\\u2019s \\u201cBean Room\\u201d stage during our broadcast at the Capitol Hill Block Party 2010.\\u00a0\\u00a0\\u00a0 <br \\/><\\/p>\",\"album_date_released\":null,\"album_comments\":\"0\",\"album_favorites\":\"0\",\"album_tracks\":\"2\",\"album_listens\":\"4519\",\"album_date_created\":\"10\\/22\\/2010 04:34:11 PM\",\"album_image_file\":\"https:\\/\\/freemusicarchive.org\\/file\\/images\\/albums\\/_-_Live__KEXP_7242010_-_20101022181252833.jpg\",\"album_images\":[{\"image_id\":\"10574\",\"user_id\":null,\"artist_id\":null,\"album_id\":\"7596\",\"curator_id\":null,\"image_file\":\"https:\\/\\/freemusicarchive.org\\/file\\/images\\/albums\\/_-_Live__KEXP_7242010_-_20101022181252833.jpg\",\"image_title\":\"albums Image\",\"image_caption\":null,\"image_copyright\":null,\"image_source\":null,\"image_order\":\"0\"}],\"tags\":[]},{\"album_id\":\"10620\",\"album_title\":\"\\\"...Through The Cracks\\\" Mix Vol. 1\",\"album_handle\":\"Through_The_Cracks_Mix_Vol_1\",\"album_url\":\"http:\\/\\/freemusicarchive.org\\/music\\/The_Yes_Sirs\\/Through_The_Cracks_Mix_Vol_1\\/\",\"album_type\":\"Album\",\"artist_name\":\"The Yes Sirs\",\"artist_url\":\"http:\\/\\/freemusicarchive.org\\/music\\/The_Yes_Sirs\\/\",\"album_producer\":null,\"album_engineer\":null,\"album_information\":\"<p>Tracks from us and our friends that never really got a proper release, so we gave away this internet-only collection of demos, lost tracks, etc.<\\/p>\",\"album_date_released\":null,\"album_comments\":\"0\",\"album_favorites\":\"1\",\"album_tracks\":\"4\",\"album_listens\":\"1619\",\"album_date_created\":\"12\\/18\\/2011 07:29:57 PM\",\"album_image_file\":\"https:\\/\\/freemusicarchive.org\\/file\\/images\\/albums\\/The_Yes_Sirs_-_Album_Title_-_20111218183001853.png\",\"album_images\":[{\"image_id\":\"16848\",\"user_id\":null,\"artist_id\":null,\"album_id\":\"10620\",\"curator_id\":null,\"image_file\":\"https:\\/\\/freemusicarchive.org\\/file\\/images\\/albums\\/The_Yes_Sirs_-_Album_Title_-_20111218183001853.png\",\"image_title\":\"Album Image: Album Title\",\"image_caption\":\"\\\"...Through The Cracks\\\" Mix Vol. 1\",\"image_copyright\":\"2011\",\"image_source\":\"http:\\/\\/cnproachmotel.blogspot.com\",\"image_order\":\"0\"}],\"tags\":[]}]}";
    	extractJson(genreJson, "example.json");
    }

}