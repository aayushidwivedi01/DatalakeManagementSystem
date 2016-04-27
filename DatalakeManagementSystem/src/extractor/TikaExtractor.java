/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package extractor;

import java.io.File;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


/**
 * Uses the auto detect parser and gets the metadata and content of a file
 */
public class TikaExtractor {
	
	static TikaConfig tikaConfig = null;
	static Metadata meta = null;
	String text = null;
	
	Multimap<String,String> metadata = null;
	Multimap<String,String> content = null;
	
	//Contructor
	public TikaExtractor(){
		tikaConfig = TikaConfig.getDefaultConfig();
		meta = new Metadata();
		metadata = ArrayListMultimap.create();
		content = ArrayListMultimap.create();
	}
	
	//Extracts all the metadata
	public Multimap<String,String> getMetadata(String filename){
		try {
			text = parseUsingAutoDetect(filename, tikaConfig, meta);
			for (String name: meta.names()){
				if(name.equals("X-Parsed-By")){
					continue;
				}
	        	metadata.put(filename+"/"+name, meta.get(name));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;
	}
	
	
	public Multimap<String,String> extract(String filename){
		content.put("contents_of_file", text);
		return content;
		
	}
	
	public static String parseUsingAutoDetect(String filename, TikaConfig tikaConfig,
            Metadata metadata) throws Exception {
		AutoDetectParser parser = new AutoDetectParser(tikaConfig);
		ContentHandler handler = new BodyContentHandler();
		TikaInputStream stream = TikaInputStream.get(new File(filename), metadata);
		parser.parse(stream, handler, metadata, new ParseContext());
		return handler.toString();
	}
	
//    public static void main(String[] args) throws Exception {
//        String filename = "/home/cis455/git/dlms_little_bobby_tables/DatalakeManagementSystem/src/extractor/generated.json";
//        TikaExtractor e = new TikaExtractor();
//         
//        String text = parseUsingAutoDetect(filename, tikaConfig, meta);
////        System.out.println(meta.names().length);
////        for (String name: meta.names()){
////        	System.out.println(name+" "+meta.get(name));
////        }
////        StringWriter sw = new StringWriter();
////        JsonMetadata.toJson(meta, sw);
////        System.out.println(sw.toString());
//        System.out.println("Parsed Metadata: ");
//        System.out.println(meta);
////        System.out.println("Parsed Text: ");
////        System.out.println(text);
//        
//        
//    }
}
