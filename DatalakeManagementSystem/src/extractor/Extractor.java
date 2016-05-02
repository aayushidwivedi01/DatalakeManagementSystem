package extractor;

import indexer.InvertedIndexDLMS;
import linker.Linker;
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
import storage.DBWrapper;
import storage.FlatDocumentDA;
import storage.ForwardIndexDA;
import bean.FlatDocument;
import bean.ForwardIndex;

/**
 * Test extractor. Take filename as input, detects mime type and calls the
 * parser.
 * 
 * @author Anwesha
 *
 */

public class Extractor {

	public static void main(String[] args) throws IOException {
		DBWrapper.setup("/home/cis550/db");
		String file_arg = "/home/cis550/yelp_academic_dataset_business_1.json";

		// Check if directory or file
		File file = new File(file_arg);
		List<String> files = new ArrayList<String>();

		if (file.isDirectory()) {
			files = Arrays.asList(file.list());
		} else {
			files.add(file_arg);
		}

		Tika tika = new Tika();

		for (String filename : files) {

			Multimap<String, String> extracted_pairs_leaf = ArrayListMultimap.create();
			Multimap<String, String> extracted_pairs_all = ArrayListMultimap.create();
			Multimap<String, String> metadata = ArrayListMultimap.create();

			String mediaType = tika.detect(filename);
			System.out.println(mediaType);
			TikaExtractor tikaextract = new TikaExtractor();

			// PARSE JSON
			if (mediaType.equals("application/json")) {

				InputStream is = new FileInputStream(filename);
				String jsonTxt = IOUtils.toString(is);
				String out = JsonExtract.extractJson(jsonTxt, filename);
				ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);

				extracted_pairs_leaf = read_out.getLeafNodes();
				extracted_pairs_all = read_out.getAllNodes();

				metadata = tikaextract.getMetadata(filename);

			}
			// PARSE CSV
			else if (mediaType.equals("text/csv")) {
				String out = CSVExtract.extractCSV(filename);
				ReadJsonOutput read_out = new ReadJsonOutput();
				read_out.getExtractedPairs(out);

				extracted_pairs_leaf = read_out.getLeafNodes();
				extracted_pairs_all = read_out.getAllNodes();

				metadata = tikaextract.getMetadata(filename);

			}
			// PARSE XML
			else if (mediaType.equals("application/xml")) {
				XMLExtract saxparser = new XMLExtract();
				saxparser.extractXML(filename);
				extracted_pairs_leaf = saxparser.getLeafNodes();
				extracted_pairs_all = saxparser.getAllNodes();

				metadata = tikaextract.getMetadata(filename);

			} else {
				// Call apache tika
				metadata = tikaextract.getMetadata(filename);
				extracted_pairs_all = tikaextract.extract(filename);
				extracted_pairs_leaf = extracted_pairs_all;
			}

			/**
			 * Store in forward index
			 */
			ForwardIndexDA fIndexDA = new ForwardIndexDA();
			ArrayList<String> all_doc_keys = new ArrayList<String>();

			// ALL CONTENT
			Set<String> keys = extracted_pairs_all.keySet();
			for (String key : keys) {
				// System.out.println(key);
				for (String value : extracted_pairs_all.get(key)) {
					ForwardIndex fIndex = new ForwardIndex(key, value);
					fIndexDA.store(fIndex);
				}
			}

			all_doc_keys.addAll(keys);

			// METADATA
			Set<String> meta_keys = metadata.keySet();
			for (String key : meta_keys) {
				// System.out.println(key);
				for (String value : metadata.get(key)) {
					ForwardIndex fIndex = new ForwardIndex(key, value);
					fIndexDA.store(fIndex);
				}
			}

			all_doc_keys.addAll(meta_keys);

			/**
			 * Add flat document DA
			 */
			FlatDocumentDA fda = new FlatDocumentDA();
			FlatDocument flatDocument = new FlatDocument(new File(filename).getName(), all_doc_keys);

			fda.store(flatDocument);

			/**
			 * CALL INVERTED INDEX METHOD
			 */
			// InvertedIndexDLMS.buildInvertedIndex(extracted_pairs_leaf);
			// InvertedIndexDLMS.buildInvertedIndex(metadata);

			System.out.println("Extractor done. Starting Linker");
			Linker linker = new Linker();
			linker.linkNewDocuments();
			DBWrapper.close();
		}

	}

}
