package indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import com.google.common.collect.Multimap;

import bean.InvertedIndex;
import bean.Occurance;
import storage.InvertedIndexDA;
import utils.Stemmer;

/**
 * Inverted Index. 
 * Takes an arraylist of key value pairs as input, and builds and inverted index.
 * @author Deepti
 *
 */

public class InvertedIndexDLMS {
	
	public static void buildInvertedIndex(Multimap<String, String> extracted_pairs_leaf)
	{
		System.out.println("BUILDING INVERTED INDEX");
		List<String> words = new ArrayList<String>();
		List<Occurance> occurs = new ArrayList<Occurance>();
		//InvertedIndexDA iIndexDA = new InvertedIndexDA();
		HashMap<String, List<Occurance>> occurances = new HashMap<String, List<Occurance>>();
		
		Set<String> keys = extracted_pairs_leaf.keySet();
		for (String key : keys)
		{
			for(String value : extracted_pairs_leaf.get(key)){
//				String path = pair.split(" : ", 2)[0].trim();
				String[] nodePath = key.split("/");
				String attribute = nodePath[nodePath.length - 1];
				words = Arrays.asList(value.trim().split("\\s+"));
				for (String word: words)
				{
					word = word.toLowerCase().replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
					if (stopwords.contains(word))
						continue;
					word = stem(word);
					Occurance current_occurance = new Occurance(key, attribute);
					//InvertedIndex idx = iIndexDA.fetch(word);
					if (occurances.containsKey(word))
					{
						//System.out.println("word " + word + " in store");
						occurs = occurances.get(word);
						occurs.add(current_occurance);
						//InvertedIndex new_idx = new InvertedIndex(word, occurs);
						//iIndexDA.update(new_idx);
						occurances.put(word, occurs);
					}
					else
					{
						//System.out.println("word " + word + " not in store");
						occurs = new ArrayList<Occurance>();
						occurs.add(current_occurance);
						//InvertedIndex new_idx = new InvertedIndex(word, occurs);
						//iIndexDA.store(new_idx);
						occurances.put(word, occurs);
					}
				}
			}
		}
		
		System.out.println("number of words: " + occurances.size());
		//printIndex(occurances);
		writeToDB(occurances);
	}
	
	private static void writeToDB(HashMap<String, List<Occurance>> index) {
		System.out.println("Writing to DB");
		InvertedIndexDA iIndexDA= new InvertedIndexDA();
		for (Entry<String, List<Occurance>> entry: index.entrySet())
		{
			//System.out.println("Storing word: " + entry.getKey());
			InvertedIndex idx = iIndexDA.fetch(entry.getKey());
			if (idx != null)
			{
				idx.getOccurs().addAll(entry.getValue());
				iIndexDA.update(idx);
			}
			else
			{
				idx = new InvertedIndex(entry.getKey(), entry.getValue());
				iIndexDA.store(idx);
			}
		}
		
	}

	private static String stem(String word)
	{
		//System.out.println("received word: " + word);
		Stemmer stemmer = new Stemmer();
		char[] charArray = word.toCharArray();
		stemmer.add(charArray, word.length());
		stemmer.stem();
		String stemmedWord = stemmer.toString();
		//System.out.println("Stemmed Word: " + stemmedWord);
		return stemmedWord;
	}
	
	private static void printIndex(HashMap<String, List<Occurance>> index)
	{
		for (Entry<String, List<Occurance>> entry: index.entrySet())
		{
			System.out.print("word: " + entry.getKey());
			for (Occurance ob: entry.getValue())
			{
				System.out.print(" path: " + ob.getPath());
				System.out.print(", attribute: " + ob.getAttribute());
			}
			System.out.println();
		}
	}
	
	/** The stopwords. */
	private static ArrayList<String> stopwords = new ArrayList<String>(
			Arrays.asList(("a,about,above,"
					+ "after,again,against,all,am,an,and,any,are,"
					+ "aren't,as,at,be,because,been,before,being,"
					+ "below,between,both,but,by,could,"
					+ "couldn't,did,didn't,do,does,doesn't,doing,don't,"
					+ "down,during,each,few,for,from,further,had,hadn't,"
					+ "has,hasn't,have,haven't,having,he,he'd,he'll,he's,"
					+ "her,here,here's,hers,herself,him,himself,his,"
					+ "how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,"
					+ "it's,its,itself,let's,me,more,mustn't,my,myself,"
					+ "no,nor,of,off,on,once,only,or,other,ought,our,ours,"
					+ "ourselves,out,over,own,shan't,she,she'd,she'll,"
					+ "she's,should,shouldn't,so,some,such,than,that,that's,"
					+ "the,their,theirs,them,themselves,then,there,there's,"
					+ "these,they,they'd,they'll,they're,they've,this,those,"
					+ "through,to,too,under,until,up,very,was,wasn't,we,we'd,"
					+ "we'll,we're,we've,were,weren't,what's,when's,"
					+ "where's,while,who's,why's,with,"
					+ "won't,would,wouldn't,you,you'd,you'll,you're,you've,your,"
					+ "yours,yourself,yourselves,").split(",")));

}