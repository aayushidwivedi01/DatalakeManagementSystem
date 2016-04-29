package searchengine;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import utils.Stemmer;

public class SearchEngine
{
	public static boolean flag = true;
	int NUM_THREADS = 10;
	Thread[] workerThreads = new Thread[2];
	Map<String, WeightedPath> seenWorker1 = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenWorker2 = new HashMap<String, WeightedPath>();
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	
	public void search(String[] query)
	{
		String keyword1 = stem(query[0]);
		if (query.length > 1)
		{
			String keyword2 = stem(query[1]);
			WeightedPath p1 = new WeightedPath(keyword1, 0);
			WeightedPath p2 = new WeightedPath(keyword2, 0);
			seenWorker1.put(keyword1, p1);
			seenWorker2.put(keyword2, p2);
			SearchEngineWorker worker1 = new SearchEngineWorker(seenWorker1, seenWorker2, keyword1);
			SearchEngineWorker worker2 = new SearchEngineWorker(seenWorker2, seenWorker1, keyword2);
			workerThreads[0] = new Thread(worker1);
			workerThreads[1] = new Thread(worker2);
			workerThreads[0].start();
			workerThreads[1].start();
			
			//TESTING
			
//			WeightedPath node1 = new WeightedPath(keyword1, 0.4);
//			WeightedPath node2 = new WeightedPath(keyword2, 6);
//			frontier.add(node1);
//			frontier.add(node2);
//			System.out.println(frontier.remove().getNode());

		}
	}
	
	public static String stem(String word)
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