package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;
import org.bson.Document;

import bean.Links;
import storage.LinksDA;

public class SearchEngineWorker implements Runnable
{
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	HashSet<ArrayList<String>> mySeenNodes = new HashSet<ArrayList<String>>();
	HashSet<ArrayList<String>> seenNodesOther = new HashSet<ArrayList<String>>();
	String word;
	
	public SearchEngineWorker(HashSet<ArrayList<String>> mySeenNodes, HashSet<ArrayList<String>> seenNodesOther, String word)
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
		
		while(SearchEngine.flag)
		{
			WeightedPath weightedPath = frontier.remove();
			String node = weightedPath.getNode();
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
				}
			}
			
			List<JSONObject> relations = new ArrayList<JSONObject>();
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
		}
		
	}
	
}