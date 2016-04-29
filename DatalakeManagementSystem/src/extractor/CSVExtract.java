package extractor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONObject;

/**
 * Class to extract CSV
 * @author Anwesha
 *
 */

public class CSVExtract {
	/**
	 * Generates a json string with each csv record as filename/filename_row_n/columnname : value
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	
	public static String extractCSV(String filename) throws IOException{
		
		CSVParser parser = new CSVParser(
			      new FileReader(filename), 
			      CSVFormat.DEFAULT.withHeader());
		
		Map<String,String> contents = new LinkedHashMap<String,String>();
		
		Map<String, Integer> headers = parser.getHeaderMap();
		String path = new File(filename).getName()+"/DONOTLINK_row";
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
