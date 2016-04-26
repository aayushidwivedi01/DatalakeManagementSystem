package extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import storage.ForwardIndexDA;
import bean.ForwardIndex;


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
//			System.out.println(mediaType);
			
			//PARSE JSON
			if(mediaType.equals("application/json")){
				
				InputStream is = new FileInputStream(filename);
		        String jsonTxt = IOUtils.toString(is);
		        String out = JsonExtract.extractJson(jsonTxt,filename);
		        ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);
//				extracted_pairs = read_out.getLeafNodes();
				extracted_pairs = read_out.getAllNodes();
		        
//		        for(String pair : extracted_pairs){
//		        	System.out.println(pair);
//		        }
			}
			//PARSE CSV
			else if(mediaType.equals("text/csv")){
				String out = CSVExtract.extractCSV(filename);
				ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);
				
//				extracted_pairs = read_out.getLeafNodes();
				extracted_pairs = read_out.getAllNodes();
		        
//		        for(String pair : extracted_pairs){
//		        	System.out.println(pair);
//		        }
			} 
			//PARSE XML
			else if(mediaType.equals("application/xml")){
				XMLExtract saxparser = new XMLExtract();
				saxparser.extractXML(filename);
//				extracted_pairs = saxparser.getLeafNodes();
				extracted_pairs = saxparser.getAllNodes();
				
//				for(String pair : extracted_pairs){
//		        	System.out.println(pair);
//		        }
				
			}
		}
		
		//Store in forward index
		ForwardIndexDA fIndexDA = new ForwardIndexDA();
		for(String pair : extracted_pairs){
			ForwardIndex fIndex = new ForwardIndex(pair.split(" : ")[0], pair.split(" : ")[1]);
			fIndexDA.store(fIndex);
		}
		
		//CALL INVERTED INDEX METHOD

	}

}
