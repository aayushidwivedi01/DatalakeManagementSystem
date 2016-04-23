package extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * For reading the extracted JSON Output
 * @author Anwesha
 *
 */
public class readJsonOutput {
	
	/**
	 * Reads the Json Tree from the root node passed.
	 * In terms of indexing, can index the leaf nodes output.
	 * @param node
	 * @param names
	 * @param idx
	 */
	private static void printJson(JsonNode node, ArrayList<String> names, int idx) {
		Iterator <JsonNode> children = node.elements();
        while (children.hasNext()){
        	JsonNode child = children.next();
        	if(!child.isContainerNode()){
        		//Leaf Node
        		System.out.println(names.get(++idx)+" : "+child);
        		
        	}else{
        		//container node
        		String current_name = names.get(++idx);
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
        			for (int i = 1; i<= size; i++){
        				field_names.add(current_name+"/element"+i);
        			}
        			
        		}
        		System.out.println(current_name+" : "+child);
        		printJson(child, field_names, -1);
        	}
        }
	}

	public static void main(String[] args) throws JsonProcessingException, IOException {
		// TODO Auto-generated method stub
		String s="{\"example.json\\/content\\/title\":\"Free Music Archive - Albums\",\"example.json\\/content\\/message\":\"\",\"example.json\\/content\\/errors\":{\"example.json\\/content\\/errors\\/element1\":1,\"example.json\\/content\\/errors\\/element2\":2},\"example.json\\/content\\/total\":\"14002\",\"example.json\\/content\\/total_pages\":7001,\"example.json\\/content\\/page\":1,\"example.json\\/content\\/limit\":\"2\",\"example.json\\/content\\/dataset\":{\"example.json\\/content\\/dataset\\/element1\":{\"example.json\\/content\\/dataset\\/element1\\/album_id\":\"7596\",\"example.json\\/content\\/dataset\\/element1\\/album_title\":\"!!! - Live @ KEXP 7/24/2010\",\"example.json\\/content\\/dataset\\/element1\\/album_handle\":\"_-_Live__KEXP_7242010\",\"example.json\\/content\\/dataset\\/element1\\/album_url\":\"http://freemusicarchive.org/music/___/_-_Live__KEXP_7242010/\",\"example.json\\/content\\/dataset\\/element1\\/album_type\":\"Live Performance\",\"example.json\\/content\\/dataset\\/element1\\/artist_name\":\"!!!\",\"example.json\\/content\\/dataset\\/element1\\/artist_url\":\"http://freemusicarchive.org/music/___/\",\"example.json\\/content\\/dataset\\/element1\\/album_producer\":null,\"example.json\\/content\\/dataset\\/element1\\/album_engineer\":null,\"example.json\\/content\\/dataset\\/element1\\/album_information\":\"<p>Funk-punk party starters !!! performs live at KEXP\u2019s \u201cBean Room\u201d stage during our broadcast at the Capitol Hill Block Party 2010.\u00a0\u00a0\u00a0 <br /></p>\",\"example.json\\/content\\/dataset\\/element1\\/album_date_released\":null,\"example.json\\/content\\/dataset\\/element1\\/album_comments\":\"0\",\"example.json\\/content\\/dataset\\/element1\\/album_favorites\":\"0\",\"example.json\\/content\\/dataset\\/element1\\/album_tracks\":\"2\",\"example.json\\/content\\/dataset\\/element1\\/album_listens\":\"4519\",\"example.json\\/content\\/dataset\\/element1\\/album_date_created\":\"10/22/2010 04:34:11 PM\",\"example.json\\/content\\/dataset\\/element1\\/album_image_file\":\"https://freemusicarchive.org/file/images/albums/_-_Live__KEXP_7242010_-_20101022181252833.jpg\",\"example.json\\/content\\/dataset\\/element1\\/album_images\":{\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\":{\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_id\":\"10574\",\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/user_id\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/artist_id\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/album_id\":\"7596\",\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/curator_id\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_file\":\"https://freemusicarchive.org/file/images/albums/_-_Live__KEXP_7242010_-_20101022181252833.jpg\",\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_title\":\"albums Image\",\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_caption\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_copyright\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_source\":null,\"example.json\\/content\\/dataset\\/element1\\/album_images\\/element1\\/image_order\":\"0\"}},\"example.json\\/content\\/dataset\\/element1\\/tags\":{}},\"example.json\\/content\\/dataset\\/element2\":{\"example.json\\/content\\/dataset\\/element2\\/album_id\":\"10620\",\"example.json\\/content\\/dataset\\/element2\\/album_title\":\"\\\"...Through The Cracks\\\" Mix Vol. 1\",\"example.json\\/content\\/dataset\\/element2\\/album_handle\":\"Through_The_Cracks_Mix_Vol_1\",\"example.json\\/content\\/dataset\\/element2\\/album_url\":\"http://freemusicarchive.org/music/The_Yes_Sirs/Through_The_Cracks_Mix_Vol_1/\",\"example.json\\/content\\/dataset\\/element2\\/album_type\":\"Album\",\"example.json\\/content\\/dataset\\/element2\\/artist_name\":\"The Yes Sirs\",\"example.json\\/content\\/dataset\\/element2\\/artist_url\":\"http://freemusicarchive.org/music/The_Yes_Sirs/\",\"example.json\\/content\\/dataset\\/element2\\/album_producer\":null,\"example.json\\/content\\/dataset\\/element2\\/album_engineer\":null,\"example.json\\/content\\/dataset\\/element2\\/album_information\":\"<p>Tracks from us and our friends that never really got a proper release, so we gave away this internet-only collection of demos, lost tracks, etc.</p>\",\"example.json\\/content\\/dataset\\/element2\\/album_date_released\":null,\"example.json\\/content\\/dataset\\/element2\\/album_comments\":\"0\",\"example.json\\/content\\/dataset\\/element2\\/album_favorites\":\"1\",\"example.json\\/content\\/dataset\\/element2\\/album_tracks\":\"4\",\"example.json\\/content\\/dataset\\/element2\\/album_listens\":\"1619\",\"example.json\\/content\\/dataset\\/element2\\/album_date_created\":\"12/18/2011 07:29:57 PM\",\"example.json\\/content\\/dataset\\/element2\\/album_image_file\":\"https://freemusicarchive.org/file/images/albums/The_Yes_Sirs_-_Album_Title_-_20111218183001853.png\",\"example.json\\/content\\/dataset\\/element2\\/album_images\":{\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\":{\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_id\":\"16848\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/user_id\":null,\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/artist_id\":null,\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/album_id\":\"10620\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/curator_id\":null,\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_file\":\"https://freemusicarchive.org/file/images/albums/The_Yes_Sirs_-_Album_Title_-_20111218183001853.png\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_title\":\"Album Image: Album Title\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_caption\":\"\\\"...Through The Cracks\\\" Mix Vol. 1\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_copyright\":\"2011\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_source\":\"http://cnproachmotel.blogspot.com\",\"example.json\\/content\\/dataset\\/element2\\/album_images\\/element1\\/image_order\":\"0\"}},\"example.json\\/content\\/dataset\\/element2\\/tags\":{}}}}\n";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(s);
		Iterator<String> fieldNames = node.fieldNames();
		ArrayList<String> names = new ArrayList<String>();
		while(fieldNames.hasNext()){
			names.add(fieldNames.next());
		}
		
		printJson(node, names, -1);
		
		
		
	}

}
