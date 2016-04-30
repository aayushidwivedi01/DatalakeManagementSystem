package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONObject;

import bean.Links;
import storage.LinksDA;

public class SearchEngineWorker implements Runnable
{
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	Map<String, WeightedPath> mySeenNodes = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenNodesOther = new HashMap<String, WeightedPath>();
	int k = 5;
	
	public SearchEngineWorker(Queue<WeightedPath> frontier, Map<String, WeightedPath> mySeenNodes, Map<String, WeightedPath> seenNodesOther)
	{
		this.frontier = frontier;
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
	}
	
	@Override
	public void run() {

		LinksDA lDa = new LinksDA();
		
		try
		{
			WeightedPath weightedPath = null;
			while(SearchEngine.flag)
			{
				synchronized(frontier)
				{
					if (frontier.isEmpty())
					{
						frontier.wait();
					}
					if (!frontier.isEmpty())
					{
						weightedPath = frontier.remove();
					}
					else
						continue;
				}
				
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
				
				Set<JSONObject> relations = new HashSet<JSONObject>();
				
				Links links = lDa.fetch(node);
	//			System.out.println("found links: " + links);
				relations = links.getRelations();
				//System.out.println("relations: " + relations);
				
				ArrayList<String> path = weightedPath.getPath();
				for (JSONObject relation : relations)
				{
					String dest = relation.getString("dest");
					ArrayList<String> newPath = new ArrayList<String>(path);
					
					//Ignore node if it creates a loop in path
					if (path.contains(dest))
						continue;
					
					newPath.add(dest);
					double newCost = weightedPath.getCost() + relation.getDouble("weight");
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
									synchronized(frontier)
									{
										frontier.add(newWeightedPath);
										frontier.notify();
									}
									mySeenNodes.put(dest, newWeightedPath);
								}
							}
							//System.out.println("Adding to frontier: " + newPath);
						}
						
						else
						{
							synchronized(frontier)
							{
								frontier.add(newWeightedPath);
								frontier.notify();
							}
							mySeenNodes.put(dest, weightedPath);
						}
					}
				}
				
//				if (frontier.isEmpty())
//				{
//					SearchEngine.flag = false;
//					System.out.println("No path found");
//				}
				
			}	//tester++;
		}
		
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}
	
}