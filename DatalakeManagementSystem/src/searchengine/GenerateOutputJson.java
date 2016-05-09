package searchengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GenerateOutputJson {

	JSONArray nodes = null;
	JSONArray links = null;

	public GenerateOutputJson() {
		nodes = new JSONArray();
		links = new JSONArray();
	}
	
	public String getData(String input){
		List<String> list = Arrays.asList(input.split(","));
		return createJson(list);
		
	}
	
	@SuppressWarnings("unchecked")
	public String createJson(List<String> list){
		
		ArrayList<Integer> end_points = new ArrayList<Integer>();

		int j = 0;
		int common_idx = 0;
		int last_common = 0;

		for (String entry : list) {
			String[] components = entry.split("/");
			int i;
			StringBuilder origin = new StringBuilder();

			// Check if leaf node of this path already has been seen
			common_idx = 0;
			JSONObject common = new JSONObject();
			common.put("name", components[common_idx]);
			common.put("group", 1);
			common.put("url", "https://s3.amazonaws.com/bobby.tables.dlms/"+components[common_idx]);
			common.put("origin", origin.append("/"+components[common_idx]).toString());
			
			while(nodes.contains(common)){
				last_common = nodes.indexOf(common);
				common.clear();

				if (++common_idx < components.length) {
					String nodename = components[common_idx];
					if (nodename.startsWith("DONOTLINK")) {
						nodename = nodename.replace("DONOTLINK", "LIST");
					}
					common.put("name", nodename);
					common.put("origin", origin.append("/"+nodename).toString());
				} else
					break;
			}
			
			boolean flag = false;
			
//			System.out.println("Common index: "+common_idx);
			if(common_idx == 0){
				origin = new StringBuilder();
			}else{
				origin = origin.replace(origin.lastIndexOf("/"), origin.length(), "");
//				System.out.println(origin.toString());
			}
			
			for(i = j; i-j+common_idx < components.length; i++){
				flag = true;
//				System.out.println("entered loop for - "+common_idx+" : "+components[i-j+common_idx]);
				
				JSONObject name = new JSONObject();
				String nodename = components[i - j + common_idx];
				if (nodename.startsWith("DONOTLINK")) {
					nodename = nodename.replace("DONOTLINK", "LIST");
				}

				name.put("name", nodename);
				name.put("origin", origin.append("/"+nodename).toString());
				if(i==j && common_idx == 0){
					name.put("group", 1);
					name.put("url","https://s3.amazonaws.com/bobby.tables.dlms/"+nodename);
				}
//				System.out.println("ADDING : "+name.toJSONString());
				
				nodes.add(name);
//				System.out.println("i = "+i+" ; j = "+j+" ; common_idx = "+common_idx+" ; components length = "+components.length);
				
				if (i != j + components.length - common_idx -1) {
					JSONObject link = new JSONObject();
//					System.out.println("i = "+i+" ; j = "+j+" ; common_idx = "+common_idx);
					if (i == j && common_idx > 0) {
						JSONObject common_link = new JSONObject();
//						System.out.println("last common: "+last_common);
//						System.out.println("i: "+i);
						common_link.put("source", last_common);
						common_link.put("target", i);
						links.add(common_link);
						link.put("source", i);
						link.put("target", i + 1);

					} else {
						link.put("source", i);
						link.put("target", i + 1);
					}
					links.add(link);
				}
			}
			
			if(!flag){
//				System.out.println("skipping over");
				end_points.add(last_common);
				
			}
			
			if (common_idx != components.length)
				end_points.add(j + components.length - 1 - common_idx);
			j = i;
		}

		for (int i = 0; i < end_points.size(); i++) {
			if (i + 1 != end_points.size()) {
				JSONObject link = new JSONObject();
//				System.out.println("Adding width for "+end_points.get(i)+" to "+end_points.get(i+1));
				link.put("source", end_points.get(i));
				link.put("target", end_points.get(i+1));
				link.put("width", 10);
				links.add(link);
			}
		}
		
//		System.out.println(nodes.toJSONString().replace("\\", ""));
//		System.out.println(links.toJSONString());
		JSONObject final_json = new JSONObject();

		final_json.put("nodes", nodes);
		final_json.put("links", links);
		
		System.out.println(final_json.toString().replace("\\", ""));
		return final_json.toString().replace("\\", "");
	}

	public static void main(String[] args) {
//		String [] links = {"yelp_academic_dataset_business_1.json/DONOTLINK_0/city/dravosburg", "yelp_academic_dataset_business_1.json/DONOTLINK_0", "yelp_academic_dataset_business_1.json/DONOTLINK_635/attributes", "yelp_academic_dataset_business_1.json/DONOTLINK_635/attributes/Good For", "yelp_academic_dataset_business_1.json/DONOTLINK_635/attributes/Good For/latenight"};
		String [] links = {"yelp_academic_dataset_business_1.json/DONOTLINK_9816/name/hours", "yelp_academic_dataset_business_1.json/DONOTLINK_9816/name", "yelp_academic_dataset_business_1.json/DONOTLINK_9816", "yelp_academic_dataset_business_1.json", "yelp_academic_dataset_business_1.json/DONOTLINK_250", "yelp_academic_dataset_business_1.json/DONOTLINK_250/name", "yelp_academic_dataset_business_1.json/DONOTLINK_250/name/friday"};
//		String [] links = {"yelp_academic_dataset_business_1.json/DONOTLINK_10114/attributes/Good For/dessert/dessert", "yelp_academic_dataset_business_1.json/DONOTLINK_10114/attributes/Good For/dessert", "yelp_academic_dataset_business_1.json/DONOTLINK_10114/attributes/Good For", "yelp_academic_dataset_business_1.json/DONOTLINK_10114/attributes/Good For/latenight", "yelp_academic_dataset_business_1.json/DONOTLINK_10114/attributes/Good For/latenight/latenight"};
		List<String> list = Arrays.asList(links);
//		List<String> list = new ArrayList<String>();
//		list.add("DOC1/b/tom");
//		list.add("DOC1/b/e/tom");
//		list.add("DOC2/d/tom");
//		list.add("DOC3/tom");
//		list.add("DOC4/x/brady");
		
//		List<List<String>> f_list = new ArrayList<List<String>>();
//		f_list.add(list);
		GenerateOutputJson col = new GenerateOutputJson();
		col.createJson(list);

	}

}
