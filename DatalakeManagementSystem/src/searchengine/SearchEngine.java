package searchengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import utils.Stemmer;

public class SearchEngine
{
	int NUM_THREADS = 10;
	Thread[] workerThreads = new Thread[2];
	HashSet<ArrayList<String>> seenWorker1 = new HashSet<ArrayList<String>>();
	HashSet<ArrayList<String>> seenWorker2 = new HashSet<ArrayList<String>>();
	
	
	public void search(String[] query)
	{
		String keyword1 = stem(query[0]);
		if (query.length > 1)
		{
			String keyword2 = stem(query[1]);
			seenWorker1.add(new ArrayList<String>(Arrays.asList(keyword1)));
			seenWorker2.add(new ArrayList<String>(Arrays.asList(keyword2)));
			SearchEngineWorker worker1 = new SearchEngineWorker(seenWorker1, seenWorker2);
			SearchEngineWorker worker2 = new SearchEngineWorker(seenWorker2, seenWorker1);
			workerThreads[0] = new Thread(worker1);
			workerThreads[1] = new Thread(worker2);
			workerThreads[0].start();
			workerThreads[1].start();

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
	
	public static void main(String[] args)
	{
		String[] query = {"tom", "brady"};
		SearchEngine engine = new SearchEngine();
		engine.search(query);
	}
}