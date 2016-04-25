package indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import utils.Stemmer;

/**
 * Inverted Index. 
 * Takes an arraylist of key value pairs as input, and builds and inverted index.
 * @author Deepti
 *
 */

public class InvertedIndex {
	
	public static void buildInvertedIndex(ArrayList<String> extractedPairs)
	{
		HashMap<String, List<JSONObject>> index = new HashMap<String, List<JSONObject>>();
		List<String> words = new ArrayList<String>();
		List<JSONObject> newValues = new ArrayList<>();
		for (String pair : extractedPairs)
		{
			String path = pair.split(":", 2)[0].trim();
			String[] nodePath = path.split("/");
			String attribute = nodePath[nodePath.length - 1];
			words = Arrays.asList(pair.split(":", 2)[1].trim().split("\\s+"));
			for (String word: words)
			{
				word = word.toLowerCase().replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
				word = stem(word);
				JSONObject jsonValue = new JSONObject();
				jsonValue.put("path", path);
				jsonValue.put("attribute", attribute);
				if (index.containsKey(word))
				{
					newValues = index.get(word);
					newValues.add(jsonValue);
					index.put(word, newValues);
				}
				else
				{
					newValues = new ArrayList<>();
					newValues.add(jsonValue);
					index.put(word, newValues);
				}
			}
		}
		
		printIndex(index);
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
	
	private static void printIndex(HashMap<String, List<JSONObject>> index)
	{
		for (Entry<String, List<JSONObject>> entry: index.entrySet())
		{
			System.out.print("word: " + entry.getKey());
			for (JSONObject ob: entry.getValue())
			{
				System.out.print(" path: " + ob.get("path"));
				System.out.print(", attribute: " + ob.get("attribute"));
			}
			System.out.println();
		}
	}
}