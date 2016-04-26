package extractor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONObject;


public class CSVExtract {
	
	public static String extractCSV(String filename) throws IOException{
		
		CSVParser parser = new CSVParser(
			      new FileReader(filename), 
			      CSVFormat.DEFAULT.withHeader());
		
		Map<String,String> contents = new LinkedHashMap();
		
		Map<String, Integer> headers = parser.getHeaderMap();
		String path = filename+"/"+filename+"_row";
		for(CSVRecord record : parser){
			//Skipping non consistent records
			if(!record.isConsistent())
				continue;
			
			for(String header : headers.keySet()){
				String current = path+record.getRecordNumber()+"/"+header;
				contents.put(current, record.get(header));
			}
			
		}
		
		parser.close();
//		int i = 1;
//		for (String con : contents.keySet()){
//			if(i > 36)
//				break;
//			i++;
//			System.out.println(con+" : "+contents.get(con));
//		}
		
//		JSONObject json_obj = new JSONObject(contents);
		return JSONObject.toJSONString(contents);
	}

//	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		Reader in = new FileReader("/home/cis455/Downloads/sample.csv");
//		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
//		extractCSV("/home/cis455/Downloads/sample.csv");
//		
//		
//		for(Object header : headers.keySet()){
//			System.out.println(header+" : "+headers.get(header));
//			
//		}
//		for(CSVRecord record : parser){
////			System.out.println(record.toString());
//			if(!record.isConsistent()){
//				System.out.println("not consistent");
//			}
//		}
		
//		Map contents = null;
//		for (CSVRecord record : records) {
////		    System.out.println(record.toString());
//		    contents = record.toMap();
//		}
		
		

//	}

}
