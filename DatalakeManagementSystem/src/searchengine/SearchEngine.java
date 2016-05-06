package searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import bean.Document;
import bean.Link;
import bean.Links;
import storage.DBWrapper;
import storage.DocumentDA;
import storage.LinksDA;
import utils.Stemmer;

public class SearchEngine
{
	public static boolean flag = true;
	public static List<ArrayList<String>> kShortestPaths = new ArrayList<ArrayList<String>>();
	public static Queue<WeightedPath> singleWordResults = new PriorityQueue<WeightedPath>();
	Thread[] workerThreads = new Thread[2];
	Map<String, WeightedPath> seenWorker1 = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenWorker2 = new HashMap<String, WeightedPath>();
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	ArrayList<Link> singleWordRelations = new ArrayList<Link>();
	String query, username;
	int NUM_THREADS_SINGLE_WORD = 1;
	int k = 5;
	public SearchEngine(String query, String username)
	{
		this.query = query;
		this.username = username;
	}

	public List<ArrayList<String>> search()
	{
		if (query.split(" ").length == 1)
		{
			try
			{
				Thread[] workers = new Thread[NUM_THREADS_SINGLE_WORD];
				LinksDA lDa = new LinksDA();
				DocumentDA docDa = new DocumentDA();
				Links links = lDa.fetch(stem(query));
				Set<Link> relations = links.getRelations();
				System.out.println("size : " + relations);
				singleWordRelations = new ArrayList<Link>(relations);
				System.out.println("number of links: " + singleWordRelations.size());
				for (int i = 0; i < NUM_THREADS_SINGLE_WORD; i++)
				{
					SingleWordWorker worker_i = new SingleWordWorker(singleWordRelations, username, lDa, docDa);
					workers[i] = new Thread(worker_i);
					workers[i].start();
				}
				
				for (int i = 0; i < NUM_THREADS_SINGLE_WORD; i++)
				{
					workers[i].join();
				}
				
				if (singleWordResults.size() < k)
				{
					k = singleWordResults.size();
				}
				for (int i = 0; i < k; i++)
				{
					kShortestPaths.add(singleWordResults.remove().getPath());
				}
				getOriginalKeywords(stem(query), query);
				printResults();
				
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		else
		{
			String orig_keyword1 = query.split(" ")[0];
			String orig_keyword2 = query.split(" ")[1];
			String keyword1 = stem(orig_keyword1);
			String keyword2 = stem(orig_keyword2);
			WeightedPath p1 = new WeightedPath(keyword1, 0);
			WeightedPath p2 = new WeightedPath(keyword2, 0);
			seenWorker1.put(keyword1, p1);
			seenWorker2.put(keyword2, p2);
			BidirectionalSearch worker1 = new BidirectionalSearch(seenWorker1, seenWorker2, keyword1, username, k);
			BidirectionalSearch worker2 = new BidirectionalSearch(seenWorker2, seenWorker1, keyword2, username, k);
			workerThreads[0] = new Thread(worker1);
			workerThreads[1] = new Thread(worker2);
			workerThreads[0].start();
			workerThreads[1].start();
			
			try {
				workerThreads[0].join();
				workerThreads[1].join();
				keyword1 = "/".concat(keyword1);
				keyword2 = "/".concat(keyword2);
				orig_keyword1 = "/".concat(orig_keyword1);
				orig_keyword2 = "/".concat(orig_keyword2);
				getOriginalKeywords(keyword1, keyword2, orig_keyword1, orig_keyword2);
				printResults();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return kShortestPaths;
	}
	
	public void printResults()
	{
		for (ArrayList<String> path : kShortestPaths)
		{
			System.out.println("path: " + path);
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
			
			else if (startNode.contains(stemmedWord2))
			{
				startNode = startNode.replace(stemmedWord2, word2);
				pathList.set(0, startNode);
			}
			
			if (lastNode.contains(stemmedWord1))
			{
				lastNode = lastNode.replace(stemmedWord1, word1);
				pathList.set(len - 1, lastNode);
			}
			
			else if (lastNode.contains(stemmedWord2))
			{
				lastNode = lastNode.replace(stemmedWord2, word2);
				pathList.set(len - 1, lastNode);
			}
			//System.out.println("path list: " + pathList);
			SearchEngine.kShortestPaths.set(i, pathList);
		}
		
	}
	
	private void getOriginalKeywords(String stemmedWord1, String word1) {
		
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
			
			if (lastNode.contains(stemmedWord1))
			{
				lastNode = lastNode.replace(stemmedWord1, word1);
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
		Document document1 = new Document("yelp_academic_dataset_business_1.json", "deepti", "test_path", "Public");
		//Document document2 = new Document("generated3.json", "aayushi", "test_path", "Public");
		DocumentDA docDA = new DocumentDA();
		docDA.store(document1);
		//docDA.store(document2);
		String query = "hours friday";
		String username = "deepti";
		SearchEngine engine = new SearchEngine(query, username);
		long startTime = System.currentTimeMillis();
		engine.search();
		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime));
		DBWrapper.close();
	}
}