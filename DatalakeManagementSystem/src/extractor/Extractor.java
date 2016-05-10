package extractor;

import linker.Linker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	private String path;

	public Extractor(String path) {
		this.path = path;
	}

	public int extract() {
		try {
			DBWrapper.setup("/home/cis550/db");
			// Check if directory or file
			System.out.println("Extractor starting...");
			// long start = System.currentTimeMillis();
			File file = new File(path);
			List<String> files = new ArrayList<String>();

			if (file.isDirectory()) {
				files = Arrays.asList(file.list());
			} else {
				files.add(path);
			}

			Tika tika = new Tika();

			try {
				for (String filename : files) {
					if (file.isDirectory())
						filename = path + "/" + filename;

					System.out.println("Extracting - " + filename);
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

						System.out.println("Finished actual json parsing");
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
						XMLExtract handler = saxparser.extractXML(filename);
						extracted_pairs_leaf = handler.getLeafNodes();
						extracted_pairs_all = handler.getAllNodes();
						metadata = tikaextract.getMetadata(filename);

					} else {
						// Call apache tika
						metadata = tikaextract.getMetadata(filename);
						extracted_pairs_all = tikaextract.extract(filename);
						extracted_pairs_leaf = extracted_pairs_all;
					}

					// Store in forward index
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
					System.out.println("Size : " + all_doc_keys.size());

					// Add flat document DA
					FlatDocumentDA fda = new FlatDocumentDA();
					FlatDocument flatDocument = new FlatDocument(new File(filename).getName(), all_doc_keys);
					fda.store(flatDocument);
				}

				// linker call
				System.out.println("Extractor done. Starting Linker...");
				// System.out.println(System.currentTimeMillis() - start);
				long start = System.currentTimeMillis();
				Linker linker = new Linker();
				linker.linkNewDocuments();
				System.out.println("Linking Finished in - " + (System.currentTimeMillis() - start) + " mSec");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return -1;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		} finally {
			DBWrapper.close();
		}
		return 1;
	}

	public static void main(String[] args) throws IOException {
		// Extractor extractor = new
		// Extractor("/home/cis455/Desktop/cis550project/bid_data/casts124.xml");
		Extractor extractor0 = new Extractor("/home/cis550/demo_files_new");
		extractor0.extract();
//		Extractor extractor = new Extractor("/home/cis550/cast.xml");
//		extractor.extract();
//		Extractor extractor2 = new Extractor("/home/cis550/ebay.xml");
//		extractor2.extract();
//		Extractor extractor3 = new Extractor("/home/cis550/movie.xml");
//		extractor3.extract();
//		Extractor extractor4 = new Extractor("/home/cis550/silicon_valley.json");
//		extractor4.extract();
		
	}

}