package extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test extractor. 
 * Take filename as input, detects mime type and calls the parser.
 * @author Anwesha
 *
 */

public class TestExtractor {

	public static void main(String[] args) throws IOException {
//		String file_arg = "/home/cis455/git/dlms_little_bobby_tables/DatalakeManagementSystem/src/extractor/generated.json";
		String file_arg = args[0];
		
		//Check if directory or file
		File file = new File(file_arg);
		List<String> files = new ArrayList<String>();
		
		if(file.isDirectory()){
			files = Arrays.asList(file.list());
		}else{
			files.add(file_arg);
		}
		
		Tika tika = new Tika();
		ArrayList<String> extracted_pairs = new ArrayList<String>();
		
		for(String filename: files){
			String mediaType = tika.detect(filename);
			System.out.println(mediaType);
			
			//PARSE JSON
			if(mediaType.equals("application/json")){
				
//				final InputStream in = new FileInputStream(filename);
//				try {
//				  for (Iterator it = new ObjectMapper().readValues(
//				      new JsonFactory().createJsonParser(in), Map.class); it.hasNext();)
//				    System.out.println(IOUtils.toString((InputStream) it.next()));
//				  	
//				}
//				finally { in.close();} 
				
				InputStream is = new FileInputStream(filename);
		        String jsonTxt = IOUtils.toString(is);
		        String out = JsonExtract.extractJson(jsonTxt,filename);
		        extracted_pairs = readJsonOutput.getExtractedPairs(out);
		        
		        for(String pair : extracted_pairs){
		        	System.out.println(pair);
		        }
			}
			//PARSE CSV
			else if(mediaType.equals("text/csv")){
				String out = CSVExtract.extractCSV(filename);
				extracted_pairs = readJsonOutput.getExtractedPairs(out);
		        
		        for(String pair : extracted_pairs){
		        	System.out.println(pair);
		        }
			} 
			//PARSE XML
			else if(mediaType.equals("application/xml")){
				XMLExtract saxparser = new XMLExtract();
				saxparser.extractXML(filename);
				extracted_pairs = saxparser.getLeafNodes();
				
				for(String pair : extracted_pairs){
		        	System.out.println(pair);
		        }
				
			}
		}
		
		//CALL INVERTED INDEX METHOD

	}

}
