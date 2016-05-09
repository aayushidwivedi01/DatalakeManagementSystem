package searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bean.Document;
import bean.Link;
import storage.DocumentDA;
import storage.LinksDA;

public class SingleWordWorker implements Runnable
{

	ArrayList<Link> frontier = new ArrayList<Link>();
	Map<String, Boolean> userPermissions = new HashMap<String, Boolean>();
	int k = 5;
	LinksDA lDa;
	String username;
	DocumentDA docDa;
	
	public SingleWordWorker(ArrayList<Link> singleWordRelations, String username, LinksDA lDa, DocumentDA docDa)
	{
		this.frontier = singleWordRelations;
		this.username = username;
		this.lDa = lDa;
		this.docDa = docDa;
	}
	
	@Override
	public void run() {
		
		boolean flag = true;
		while (flag)
		{
			Link link = null;
			synchronized(frontier)
			{
				if (frontier.isEmpty())
				{
					flag = false;
					continue;
				}
				if (!frontier.isEmpty())
				{
					link = frontier.remove(0);
				}
				else
				{
					flag = false;
					continue;
				}
			}
			
			double cost = link.getWeight();
			//String path = weightedPath.getNode();
			String path = link.getDest().concat("/").concat(link.getSource());
			if (isAccessible(path))
			{
				ArrayList<String> fullPath = new ArrayList<String>();
				while (path.length() != 0)
				{
					fullPath.add(path);
					int endIndex = path.lastIndexOf('/');
					if (endIndex == -1)
					{
						path = "";
					}
					else
					{
						path = path.substring(0, endIndex);
					}
				}
				double newCost = cost + fullPath.size();
				WeightedPath newPath = new WeightedPath(fullPath, newCost);
				
				synchronized(SearchEngine.singleWordResults)
				{
					SearchEngine.singleWordResults.add(newPath);
				}
			}
		}
	}
	
	public boolean isAccessible(String docPath) 
	{
		System.out.println(docPath);
		String doc;
		if (!docPath.contains("/"))
			doc = docPath;
		else
			doc = docPath.substring(0, docPath.indexOf("/"));
		String permission;
		if (userPermissions.containsKey(doc))
			return userPermissions.get(doc);
		
		System.out.println("doc - "+doc);
		Document docInfo = docDa.fetch(doc);
		System.out.println("doc info - "+docInfo);
		permission = docInfo.getPermission();
		String owner = docInfo.getOwner();
		if (permission.equals("Public") || owner.equals(username))
		{
			userPermissions.put(doc, true);
			return true;
		}
		
		userPermissions.put(doc, false);
		return false;
	}
}