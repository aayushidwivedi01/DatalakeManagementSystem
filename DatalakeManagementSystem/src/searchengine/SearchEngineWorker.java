package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
<<<<<<< HEAD
import java.util.Map;

import org.json.JSONObject;
=======

import org.json.JSONObject;
import org.bson.Document;
>>>>>>> 975cf6dc87ae2c39a58d98ec0eb53d9fcf38d756

import bean.Links;
import storage.LinksDA;

public class SearchEngineWorker implements Runnable
{
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
<<<<<<< HEAD
	Map<String, WeightedPath> mySeenNodes = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenNodesOther = new HashMap<String, WeightedPath>();
	HashSet<ArrayList<String>> seenPaths = new HashSet<ArrayList<String>>();

	String word;
	
	public SearchEngineWorker(Map<String, WeightedPath> mySeenNodes, Map<String, WeightedPath> seenNodesOther, String word)
=======
	HashSet<ArrayList<String>> mySeenNodes = new HashSet<ArrayList<String>>();
	HashSet<ArrayList<String>> seenNodesOther = new HashSet<ArrayList<String>>();
	String word;
	
	public SearchEngineWorker(HashSet<ArrayList<String>> mySeenNodes, HashSet<ArrayList<String>> seenNodesOther, String word)
>>>>>>> 975cf6dc87ae2c39a58d98ec0eb53d9fcf38d756
	{
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
		this.word = word;
	}
	
	@Override
	public void run() {
//		System.out.println("Hi in worker");
//		System.out.println("my nodes are " + mySeenNodes.toString());
//		System.out.println("other nodes are " + seenNodesOther.toString());
		WeightedPath currentNode = new WeightedPath(word, 1);
		frontier.add(currentNode);
		LinksDA lDa = new LinksDA();
		
<<<<<<< HEAD
		//int tester = 0;
		//while(tester < 4)
=======
>>>>>>> 975cf6dc87ae2c39a58d98ec0eb53d9fcf38d756
		while(SearchEngine.flag)
		{
			WeightedPath weightedPath = frontier.remove();
			String node = weightedPath.getNode();
<<<<<<< HEAD
			//System.out.println("found node: " + node);
			synchronized(seenNodesOther)
			{
				if (seenNodesOther.containsKey(node))
				{
					ArrayList<String> path2 = new ArrayList<>(seenNodesOther.get(node).getPath());
					Collections.reverse(path2);
					System.out.println("Found a path!! Path:" + weightedPath.getPath() + " + " + path2);
					
					SearchEngine.flag = false;
=======
			ArrayList<String> path = weightedPath.getPath();
			synchronized(seenNodesOther)
			{
				if (seenNodesOther.contains(path))
				{
					System.out.println("Found a path!!");
					SearchEngine.flag = false;
				}
				if (frontier.isEmpty())
				{
					SearchEngine.flag = false;
					System.out.println("No path found");
>>>>>>> 975cf6dc87ae2c39a58d98ec0eb53d9fcf38d756
				}
			}
			
			List<JSONObject> relations = new ArrayList<JSONObject>();
<<<<<<< HEAD
			
			Links links = lDa.fetch(node);
//			System.out.println("found links: " + links);
			relations = links.getRelations();
			//System.out.println("relations: " + relations);
			
			ArrayList<String> path = weightedPath.getPath();
			for (JSONObject relation : relations)
			{
				String dest = relation.getString("dest");
				ArrayList<String> newPath = new ArrayList<String>(path);
				if (path.contains(dest))
					continue;
				newPath.add(dest);
				synchronized(mySeenNodes)
				{
					if (!mySeenNodes.containsKey(dest))
					{
						//System.out.println("Adding to frontier: " + newPath);
						WeightedPath newWeightedPath = new WeightedPath(newPath, 1);
						frontier.add(newWeightedPath);
						mySeenNodes.put(node, weightedPath);
						seenPaths.add(newPath);
					}
					
					else
					{
						
					}
				}
			}
			
			if (frontier.isEmpty())
			{
				SearchEngine.flag = false;
				System.out.println("No path found");
			}
			
			//tester++;
=======
			synchronized(lDa)
			{
				Links links = lDa.fetch(node);
				relations = links.getRelations();
				System.out.println("relations: " + relations);
			}
			
			for (JSONObject relation : relations)
			{
				String dest = relation.getString("dest");
				ArrayList<String> newPath = new ArrayList<String>(currentNode.getPath());
				newPath.add(dest);
				synchronized(mySeenNodes)
				{
					frontier.add(new WeightedPath(newPath, 1));
					mySeenNodes.add(newPath);
				}
			}
>>>>>>> 975cf6dc87ae2c39a58d98ec0eb53d9fcf38d756
		}
		
	}
	
}