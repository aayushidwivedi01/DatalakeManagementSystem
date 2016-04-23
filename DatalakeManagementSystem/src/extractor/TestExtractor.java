package extractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

/**
 * Test extractor. 
 * Take filename as input, detects mime type and calls the parser.
 * @author Anwesha
 *
 */

public class TestExtractor {

	public static void main(String[] args) throws IOException {
		String filename = args[0];
		Tika tika = new Tika();
		String mediaType = tika.detect(filename);
//		System.out.println(mediaType);
		
		if(mediaType.equals("application/json")){
			InputStream is = new FileInputStream(filename);
	        String jsonTxt = IOUtils.toString(is);
	        String out = JsonExtract.extractJson(jsonTxt,"generated.json");
	        ArrayList<String> extracted_pairs = readJsonOutput.getExtractedPairs(out);
	        
	        for(String pair : extracted_pairs){
	        	System.out.println(pair);
	        }
		}


	}

}
