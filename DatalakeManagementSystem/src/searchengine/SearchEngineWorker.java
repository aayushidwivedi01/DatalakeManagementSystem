package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import bean.Document;
import bean.Link;
import bean.Links;
import storage.LinksDA;
import storage.DocumentDA;

public class SearchEngineWorker implements Runnable {
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	Map<String, WeightedPath> mySeenNodes = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenNodesOther = new HashMap<String, WeightedPath>();
	Map<String, Boolean> userPermissions = new HashMap<String, Boolean>();
	int k;
	LinksDA lDa;
	String username;
	DocumentDA docDa;

	public SearchEngineWorker(Queue<WeightedPath> frontier, Map<String, WeightedPath> mySeenNodes,
			Map<String, WeightedPath> seenNodesOther, String username, LinksDA lDa, DocumentDA docDa, int k) {
		this.frontier = frontier;
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
		this.username = username;
		this.lDa = lDa;
		this.docDa = docDa;
		this.k = k;
	}

	@Override
	public void run() {

		// LinksDA lDa = new LinksDA();

		try {
			WeightedPath weightedPath = null;
			while (SearchEngine.flag) {
				synchronized (frontier) {
					if (frontier.isEmpty()) {
						// System.out.println("Waiting " +
						// Thread.currentThread().getName());
						frontier.wait(10000);
						// System.out.println("Out of wait: " +
						// Thread.currentThread().getName());
					}
					if (!frontier.isEmpty()) {
						// System.out.println("Frontier not empty: " +
						// Thread.currentThread().getName());
						weightedPath = frontier.remove();
					} else {
						SearchEngine.flag = false;
						continue;
					}
				}

				String node = weightedPath.getNode();
				// System.out.println("found node: " + node);
				synchronized (seenNodesOther) {
					if (seenNodesOther.containsKey(node)) {
						// System.out.println("Matched node: " + node);
						ArrayList<String> path1 = new ArrayList<>(weightedPath.getPath());
						// System.out.println("Second path: " +
						// seenNodesOther.get(node).getPath());
						ArrayList<String> path2 = new ArrayList<>(seenNodesOther.get(node).getPath());
						// Collections.reverse(path2);
						// path2.remove(0);
						// System.out.println("Found a path!"); //+
						// weightedPath.getPath() + " + " + path2);
						// System.out.println("Path 1: " + path1 + " Path 2: " +
						// path2);
						// path1.addAll(path2);
						synchronized (SearchEngine.kShortestPaths) {
							if (SearchEngine.kShortestPaths.size() < k) {
								ArrayList<String> mergedPath = mergePaths(path1, path2);

								if (!SearchEngine.kShortestPaths.contains(mergedPath)) {
									Collections.reverse(mergedPath);
									if (!SearchEngine.kShortestPaths.contains(mergedPath)) {
										// Add shortest path
										// System.out.println("Adding path: " +
										// path1);
										SearchEngine.kShortestPaths.add(mergedPath);

										if (SearchEngine.kShortestPaths.size() >= k) {
											SearchEngine.flag = false;
										}
									}
								}
							}
						}
						// System.out.println(Thread.currentThread().getName() +
						// " path1: " + path1 + "path2: " + path2);
						if (path1.size() == 1 || path2.size() == 0) {
							// System.out.println("Don't add links" +
							// Thread.currentThread().getName());
							continue;
						}
						// else
						// {
						// System.out.println("adding links" +
						// Thread.currentThread().getName());
						// }
					}
				}

				Set<Link> relations = new HashSet<Link>();
				// System.out.println("node: " + node);
				Links links = lDa.fetch(node);
				// System.out.println("found links: " + links);
				relations = links.getRelations();
				// System.out.println("relations: " + relations);
				if (relations != null) {
					ArrayList<String> path = weightedPath.getPath();
					for (Link relation : relations) {
						String dest = relation.getDest();
						// System.out.println(Thread.currentThread().getName() +
						// node + " linked to: " + dest);

						if (!isAccessible(dest)) {
							continue;
						}
						ArrayList<String> newPath = new ArrayList<String>(path);

						// Ignore node if it creates a loop in path
						if (path.contains(dest))
							continue;

						// System.out.println("Adding to path " + newPath + " :
						// " + dest);

						newPath.add(dest);
						double newCost = weightedPath.getCost() + relation.getWeight();
						WeightedPath newWeightedPath = new WeightedPath(newPath, newCost);

						synchronized (mySeenNodes) {
							synchronized (frontier) {
								// System.out.println(Thread.currentThread().getName()
								// + "Adding to frontier new path: " + newPath);
								frontier.add(newWeightedPath);
								frontier.notify();
							}
							mySeenNodes.put(dest, newWeightedPath);
						}
					}
				} else {
					SearchEngine.flag = false;
				}
			}
		}

		catch (InterruptedException e) {
			e.printStackTrace();
			SearchEngine.flag = false;
		}

		catch (NullPointerException e) {
			e.printStackTrace();
			SearchEngine.flag = false;
		}

		catch (Exception e) {
			e.printStackTrace();
			SearchEngine.flag = false;
		}

	}

	public boolean isAccessible(String docPath) {
		String doc;
		if (!docPath.contains("/"))
			doc = docPath;
		else
			doc = docPath.substring(0, docPath.indexOf("/"));
		String permission;
		if (userPermissions.containsKey(doc))
			return userPermissions.get(doc);

		Document docInfo = docDa.fetch(doc);
		if (docInfo == null)
			return true;
		permission = docInfo.getPermission();
		String owner = docInfo.getOwner();
		if (permission.equals("Public") || owner.equals(username)) {
			userPermissions.put(doc, true);
			return true;
		}

		userPermissions.put(doc, false);
		return false;
	}

	public ArrayList<String> mergePaths(ArrayList<String> path1, ArrayList<String> path2) {
		ArrayList<String> mergedPath = new ArrayList<String>();
		mergedPath.addAll(path1);
		Collections.reverse(path2);
		path2.remove(0);
		mergedPath.addAll(path2);
		// System.out.println("in function merge: " + mergedPath);
		if (mergedPath.size() > 0) {
			String firstNode = mergedPath.remove(0);
			String pathToFirstNode = mergedPath.get(0);
			pathToFirstNode = pathToFirstNode.concat("/").concat(firstNode);
			mergedPath.add(0, pathToFirstNode);
		}
		// System.out.println("after first check: " + mergedPath);
		if (mergedPath.size() > 2) {
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