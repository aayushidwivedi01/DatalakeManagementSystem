package searchengine;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GenerateOutputJson {
	
	JSONArray nodes = null;
	JSONArray links = null;
	
	public GenerateOutputJson(){
		nodes = new JSONArray();
		links = new JSONArray();
	}
	
	public void createJson(List<String> list){
		
		ArrayList<Integer> end_points = new ArrayList<Integer>();
		
		int j = 0;
		int num_common_idx = 0;
		int last_common_idx = 0;
		
		for(String entry : list){
			String [] components = entry.split("/");
			int i;
			
			//Check if leaf node of this path already has been seen
			num_common_idx = 0;
			JSONObject common = new JSONObject();
			common.put("name", components[num_common_idx]);
			
			while(nodes.contains(common)){
				last_common_idx = nodes.indexOf(common);
				common.clear();
				
				if(++num_common_idx < components.length){
					String nodename = components[num_common_idx];
					if(nodename.startsWith("DONOTLINK")){
						nodename = nodename.replace("DONOTLINK", "LIST");
					}
					common.put("name", nodename);
				}
				else
					break;
			}
			for(i = j; i-j+num_common_idx < components.length; i++){
				JSONObject name = new JSONObject();
				String nodename = components[i-j+num_common_idx];
				if(nodename.startsWith("DONOTLINK")){
					nodename = nodename.replace("DONOTLINK", "LIST");
				}
				name.put("name", nodename);
				nodes.add(name);
				
				if(i != j+components.length-1-num_common_idx){
					JSONObject link = new JSONObject();
					
					if(i == j && num_common_idx > 0){
						JSONObject common_link = new JSONObject();
						common_link.put("source", last_common_idx);
						common_link.put("target", i);
						links.add(common_link);
						link.put("source", i);
						link.put("target", i+1);
						
					}else{
						link.put("source", i);
						link.put("target", i+1);
					}
					links.add(link);
				}
			}
			
			if(num_common_idx != components.length){
				end_points.add(j+components.length-1-num_common_idx);
			}
			j = i;
		}
		
		for(int i = 0; i < end_points.size(); i++){
			if(i+1 != end_points.size()){
				JSONObject link = new JSONObject();
				link.put("source", end_points.get(i));
				link.put("target", end_points.get(i+1));
				links.add(link);
			}
		}
//		System.out.println(links.toJSONString());
		JSONObject final_json = new JSONObject();
		
		final_json.put("nodes", nodes);
		
		final_json.put("links", links);
		
//		try {
//
//			FileWriter file = new FileWriter("/usr/share/jetty/webapps/root/graph.json");
//			file.write(final_json.toJSONString());
//			file.flush();
//			file.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println(final_json.toString());
	}

	public static void main(String[] args) {
		String [] arr = {"yelp_academic_dataset_business_1.json/DONOTLINK_2/city/dravosburg", "yelp_academic_dataset_business_1.json/DONOTLINK_2", "yelp_academic_dataset_business_1.json/DONOTLINK_204/attributes", "yelp_academic_dataset_business_1.json/DONOTLINK_204/attributes/Good For", "yelp_academic_dataset_business_1.json/DONOTLINK_204/attributes/Good For/latenight"};

		List<String> list = new ArrayList<String>();
		list = Arrays.asList(arr);
//		list.add("DOC1/b/tom");
//		list.add("DOC1/b/e/tom");
//		list.add("DOC2/d/tom");
//		list.add("DOC3/tom");
//		list.add("DOC4/x/brady");
		
		GenerateOutputJson col = new GenerateOutputJson();
		col.createJson(list);
		
		
	}

}
