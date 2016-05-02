package searchengine;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public ArrayList<String> mergePaths(ArrayList<String> path1, ArrayList<String> path2) {
		ArrayList<String> mergedPath = new ArrayList<String>();
		mergedPath.addAll(path1);
		Collections.reverse(path2);
		path2.remove(0);
		mergedPath.addAll(path2);
		//System.out.println("in function merge: " + mergedPath);
		if (mergedPath.size() > 0)
		{
			String firstNode = mergedPath.remove(0);
			String pathToFirstNode = mergedPath.get(0);
			pathToFirstNode = pathToFirstNode.concat("/").concat(firstNode);
			mergedPath.add(0, pathToFirstNode);
		}
		//System.out.println("after first check: " + mergedPath);
		if (mergedPath.size() > 2)
		{
			int l = mergedPath.size() - 1;
			String lastNode = mergedPath.remove(l);
			String pathToLastNode = mergedPath.get(l - 1);
			pathToLastNode = pathToLastNode.concat("/").concat(lastNode);
			mergedPath.add(l, pathToLastNode);
		}
		System.out.println("merged path: " + mergedPath);
		return mergedPath;
	}
	public static void main(String[] args)
	{
		DBWrapper.setup("/Users/Deepti/MyClasses/DB/Project/db");
		String[] query = {"tom", "hardy"};
		SearchEngine engine = new SearchEngine();
		long startTime = System.currentTimeMillis();
		engine.search(query);
		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime));
//		ArrayList<String> path1 = new ArrayList<String>(Arrays.asList("tom", "x/y/name", "hardy"));
//		ArrayList<String> path2 = new ArrayList<String>(Arrays.asList("hardy"));
//		System.out.println("path1: " + path1 + " path2: " + path2);
//		engine.mergePaths(path1, path2);
//		
//		path1 = new ArrayList<String>(Arrays.asList("tom", "x/y/name"));
//		path2 = new ArrayList<String>(Arrays.asList("hardy", "x/y/name"));
//		System.out.println("path1: " + path1 + " path2: " + path2);
//		engine.mergePaths(path1, path2);
//		
//		path1 = new ArrayList<String>(Arrays.asList("name", "x/y/name"));
//		path2 = new ArrayList<String>(Arrays.asList("x/y/name"));
//		System.out.println("path1: " + path1 + " path2: " + path2);
//		engine.mergePaths(path1, path2);
//		
//		path1 = new ArrayList<String>(Arrays.asList("name", "x/y/z/from/name", "x/y/z/from", "x/y/z", "x/y"));
//		path2 = new ArrayList<String>(Arrays.asList("tom", "x/y/a/from", "x/y/a", "x/y"));
//		System.out.println("path1: " + path1 + " path2: " + path2);
//		engine.mergePaths(path1, path2);
		
		DBWrapper.close();
	}
}