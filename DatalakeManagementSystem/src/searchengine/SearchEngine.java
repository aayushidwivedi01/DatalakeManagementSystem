package searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import storage.DBWrapper;
import utils.Stemmer;

public class SearchEngine
{
	public static boolean flag = true;
	public static List<ArrayList<String>> kShortestPaths = new ArrayList<ArrayList<String>>();
	Thread[] workerThreads = new Thread[2];
	Map<String, WeightedPath> seenWorker1 = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenWorker2 = new HashMap<String, WeightedPath>();
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();

	public void search(String[] query)
	{
		//String keyword1 = stem(query[0]);
		String keyword1 = query[0];
		if (query.length > 1)
		{
			String keyword2 = stem(query[1]);
			WeightedPath p1 = new WeightedPath(keyword1, 0);
			WeightedPath p2 = new WeightedPath(keyword2, 0);
			seenWorker1.put(keyword1, p1);
			seenWorker2.put(keyword2, p2);
			BidirectionalSearch worker1 = new BidirectionalSearch(seenWorker1, seenWorker2, keyword1);
			BidirectionalSearch worker2 = new BidirectionalSearch(seenWorker2, seenWorker1, keyword2);
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
			try {
				workerThreads[0].join();
				workerThreads[1].join();
				for (ArrayList<String> path : kShortestPaths)
				{
					System.out.println("path: " + path);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
		DBWrapper.setup("/home/cis550/db");
		String[] query = {"dravosburg", "latenight"};
		SearchEngine engine = new SearchEngine();
		long startTime = System.currentTimeMillis();
		engine.search(query);
		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime));
		DBWrapper.close();
	}
}