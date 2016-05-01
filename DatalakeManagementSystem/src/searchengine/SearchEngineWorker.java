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
	LinksDA lDa;
	
	public SearchEngineWorker(Queue<WeightedPath> frontier, Map<String, WeightedPath> mySeenNodes, Map<String, WeightedPath> seenNodesOther, LinksDA lDa)
	{
		this.frontier = frontier;
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
		this.lDa = lDa;
	}
	
	@Override
	public void run() {

		//LinksDA lDa = new LinksDA();
		
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
						System.out.println("Matched node: " + node);
						ArrayList<String> path1 = new ArrayList<>(weightedPath.getPath());
						//System.out.println("Second path: " + seenNodesOther.get(node).getPath());
						ArrayList<String> path2 = new ArrayList<>(seenNodesOther.get(node).getPath());
						//Collections.reverse(path2);
						//path2.remove(0);
						System.out.println("Found a path!"); //+ weightedPath.getPath() + " + " + path2);
						System.out.println("Path 1: " + path1 + " Path 2: " + path2);
						//path1.addAll(path2);
						synchronized(SearchEngine.kShortestPaths)
						{
							if (SearchEngine.kShortestPaths.size() < k)
							{
								ArrayList<String> mergedPath = mergePaths(path1, path2);
								
								if (!SearchEngine.kShortestPaths.contains(mergedPath))
								{
									Collections.reverse(mergedPath);
									if (!SearchEngine.kShortestPaths.contains(mergedPath))
									{	
										//Add shortest path
										//System.out.println("Adding path: " + path1);
										SearchEngine.kShortestPaths.add(path1);
										
										if (SearchEngine.kShortestPaths.size() >= k || frontier.isEmpty())
										{
											SearchEngine.flag = false;
										}
									}
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
					System.out.println(node + " linked to: " + dest);
					ArrayList<String> newPath = new ArrayList<String>(path);
					
					//Ignore node if it creates a loop in path
					if (path.contains(dest))
						continue;
					
					//System.out.println("Adding to path " + newPath + " : " + dest);

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
							mySeenNodes.put(dest, newWeightedPath);
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
	
}