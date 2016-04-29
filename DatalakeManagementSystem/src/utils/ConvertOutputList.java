package utils;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConvertOutputList {
	
	JSONArray nodes = null;
	JSONArray links = null;
	
	public ConvertOutputList(){
		nodes = new JSONArray();
		links = new JSONArray();
	}
	
	public void createJson(ArrayList<String> list){
		
		JSONArray jarray = new JSONArray();
		ArrayList<Integer> end_points = new ArrayList<Integer>();
		
		int j = 0;
		
		for(String entry : list){
			String [] components = entry.split("/");
			end_points.add(j+components.length-1);
			
			int i;
			
			for(i = j; i < j+components.length; i++){
				JSONObject name = new JSONObject();
				name.put("name", components[i-j]);
				nodes.add(name);
				
				if(i+1 != j+components.length){
					JSONObject link = new JSONObject();
					link.put("source", i);
					link.put("target", i+1);
					links.add(link);
				}
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
		
		JSONObject final_json = new JSONObject();
		
//		String [] nodesArr = new String[nodes.size()];
//		nodesArr = nodes.toArray(nodesArr);
		final_json.put("nodes", nodes);
		
//		String [] linksArr = new String[links.size()];
//		linksArr = links.toArray(linksArr);
		final_json.put("links", links);
		
		System.out.println(final_json.toString());
	}

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("DOC1/b/tom");
		list.add("DOC2/d/tom");
		list.add("DOC3/tom");
		list.add("DOC4/x/brady");
		
		ConvertOutputList col = new ConvertOutputList();
		col.createJson(list);
		
		
	}

}
