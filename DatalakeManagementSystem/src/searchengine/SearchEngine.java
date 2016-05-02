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

	public void search(String query)
	{
		if (query.split(" ").length > 1)
		{
			String orig_keyword1 = query.split(" ")[0];
			String orig_keyword2 = query.split(" ")[1];
			String keyword1 = stem(orig_keyword1);
			String keyword2 = stem(orig_keyword2);
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
				keyword1 = "/".concat(keyword1);
				keyword2 = "/".concat(keyword2);
				orig_keyword1 = "/".concat(orig_keyword1);
				orig_keyword2 = "/".concat(orig_keyword2);
				getOriginalKeywords(keyword1, keyword2, orig_keyword1, orig_keyword2);
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
	
	private void getOriginalKeywords(String stemmedWord1, String stemmedWord2, String word1, String word2) {
		
		for (int i = 0; i < SearchEngine.kShortestPaths.size(); i++)
		{
			ArrayList<String> pathList = SearchEngine.kShortestPaths.get(i);
			int len = pathList.size();
			String startNode = pathList.get(0);
			String lastNode = pathList.get(len - 1);
			if (startNode.contains(stemmedWord1))
			{
				startNode = startNode.replace(stemmedWord1, word1);
				pathList.set(0, startNode);
			}
			
			if (startNode.contains(stemmedWord2))
			{
				startNode = startNode.replace(stemmedWord2, word2);
				pathList.set(0, startNode);
			}
			
			if (lastNode.contains(stemmedWord1))
			{
				lastNode = lastNode.replace(stemmedWord1, word1);
				pathList.set(len - 1, lastNode);
			}
			
			if (lastNode.contains(stemmedWord2))
			{
				lastNode = lastNode.replace(stemmedWord2, word2);
				pathList.set(len - 1, lastNode);
			}
			//System.out.println("path list: " + pathList);
			SearchEngine.kShortestPaths.set(i, pathList);
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
		SearchEngine engine = new SearchEngine();
		long startTime = System.currentTimeMillis();
		engine.search("tom hardy");
		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime));
		DBWrapper.close();
	}
}