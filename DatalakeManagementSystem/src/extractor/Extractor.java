package extractor;

import indexer.InvertedIndexDLMS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import storage.ForwardIndexDA;
import bean.ForwardIndex;


/**
 * Test extractor. 
 * Take filename as input, detects mime type and calls the parser.
 * @author Anwesha
 *
 */

public class Extractor {

	public static void main(String[] args) throws IOException {
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
		Multimap<String,String> extracted_pairs_leaf = ArrayListMultimap.create();
		Multimap<String,String> extracted_pairs_all = ArrayListMultimap.create();
		Multimap<String,String> metadata = ArrayListMultimap.create();
		
		for(String filename: files){
			String mediaType = tika.detect(filename);
			TikaExtractor tikaextract = new TikaExtractor();
			//PARSE JSON
			if(mediaType.equals("application/json")){
				
				InputStream is = new FileInputStream(filename);
		        String jsonTxt = IOUtils.toString(is);
		        String out = JsonExtract.extractJson(jsonTxt,"generated.json");
		        ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);
				
				
				extracted_pairs_leaf = read_out.getLeafNodes();
				extracted_pairs_all = read_out.getAllNodes();
				
				metadata = tikaextract.getMetadata(filename);
		        
//				Set<String> keys = extracted_pairs_all.keySet();
//		        for(String key : keys){
//		        	for(String value : extracted_pairs_all.get(key)){
//		        		System.out.println(key+" : "+value);
//		        	}
//		        }
			}
			//PARSE CSV
			else if(mediaType.equals("text/csv")){
				String out = CSVExtract.extractCSV(filename);
				ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);
				
				extracted_pairs_leaf = read_out.getLeafNodes();
				extracted_pairs_all = read_out.getAllNodes();
				
				metadata = tikaextract.getMetadata(filename);
		        
			} 
			//PARSE XML
			else if(mediaType.equals("application/xml")){
				XMLExtract saxparser = new XMLExtract();
				saxparser.extractXML(filename);
				extracted_pairs_leaf = saxparser.getLeafNodes();
				extracted_pairs_all = saxparser.getAllNodes();
				
				metadata = tikaextract.getMetadata(filename);
				
			}else{
				//Call apache tika
				metadata = tikaextract.getMetadata(filename);
				extracted_pairs_all = tikaextract.extract(filename);
				extracted_pairs_leaf = extracted_pairs_all;
			}
		}
		
		//Store in forward index
		ForwardIndexDA fIndexDA = new ForwardIndexDA();
		
		//ALL CONTENT
		Set<String> keys = extracted_pairs_all.keySet();
		for(String key : keys){
        	for(String value : extracted_pairs_all.get(key)){
        		ForwardIndex fIndex = new ForwardIndex(key,value);
    			fIndexDA.store(fIndex);
        	}
        }
		
		//METADATA
		Set<String> meta_keys = metadata.keySet();
		for(String key : meta_keys){
        	for(String value : metadata.get(key)){
        		ForwardIndex fIndex = new ForwardIndex(key,value);
    			fIndexDA.store(fIndex);
        	}
        }
		
		//CALL INVERTED INDEX METHOD
		InvertedIndexDLMS.buildInvertedIndex(extracted_pairs_leaf);
		InvertedIndexDLMS.buildInvertedIndex(metadata);

	}

}
