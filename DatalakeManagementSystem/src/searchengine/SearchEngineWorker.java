package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import bean.Link;
import bean.Links;
import storage.LinksDA;

public class SearchEngineWorker implements Runnable
{
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	Map<String, WeightedPath> mySeenNodes = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenNodesOther = new HashMap<String, WeightedPath>();
	int k = 5;
	String word;
	
	public SearchEngineWorker(Map<String, WeightedPath> mySeenNodes, Map<String, WeightedPath> seenNodesOther, String word)
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
		
		//int tester = 0;
		//while(tester < 4)
		while(SearchEngine.flag)
		{
			WeightedPath weightedPath = frontier.remove();
			String node = weightedPath.getNode();
			//System.out.println("found node: " + node);
			synchronized(seenNodesOther)
			{
				if (seenNodesOther.containsKey(node))
				{
					ArrayList<String> path1 = new ArrayList<>(weightedPath.getPath());
					ArrayList<String> path2 = new ArrayList<>(seenNodesOther.get(node).getPath());
					Collections.reverse(path2);
					System.out.println("Found a path!!"); //+ weightedPath.getPath() + " + " + path2);
					path1.addAll(path2);
					synchronized(SearchEngine.kShortestPaths)
					{
						if (!SearchEngine.kShortestPaths.contains(path1))
						{
							Collections.reverse(path1);
							SearchEngine.kShortestPaths.add(path1);
							if (SearchEngine.kShortestPaths.size() == k || frontier.isEmpty())
							{
								SearchEngine.flag = false;
							}
						}
					}
				}
			}
			
			Set<Link> relations = new HashSet<Link>();
			
			Links links = lDa.fetch(node);
//			System.out.println("found links: " + links);
			relations = links.getRelations();
			//System.out.println("relations: " + relations);
			
			ArrayList<String> path = weightedPath.getPath();
			for (Link relation : relations)
			{
				String dest = relation.getDest();
				ArrayList<String> newPath = new ArrayList<String>(path);
				if (path.contains(dest))
					continue;
				newPath.add(dest);
				double newCost = weightedPath.getCost() + relation.getWeight();
				WeightedPath newWeightedPath = new WeightedPath(newPath, newCost);
				synchronized(mySeenNodes)
				{
					//Update path if this one is shorter
					if (mySeenNodes.containsKey(dest))
					{
						WeightedPath oldPath = mySeenNodes.get(dest);
						if (!oldPath.equals(weightedPath))
						{
							if (newCost < oldPath.getCost())
							{
								frontier.add(newWeightedPath);
								mySeenNodes.put(dest, newWeightedPath);
							}
						}
						//System.out.println("Adding to frontier: " + newPath);
					}
					
					else
					{
						frontier.add(newWeightedPath);
						mySeenNodes.put(dest, weightedPath);
					}
				}
			}
			
			if (frontier.isEmpty())
			{
				SearchEngine.flag = false;
				System.out.println("No path found");
			}
			
			//tester++;
		}
		
	}
	
}