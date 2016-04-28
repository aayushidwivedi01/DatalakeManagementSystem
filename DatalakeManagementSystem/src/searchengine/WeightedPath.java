package searchengine;

import java.util.ArrayList;

class WeightedPath
{
	ArrayList<String> path;
	int cost;
	String node;

	public WeightedPath(ArrayList<String> path, int cost)
	{
		this.path = path;
		this.node = path.get(path.size());
	}
	
	public WeightedPath(String node, int cost)
	{
		this.path = new ArrayList<String>();
		path.add(node);
	}
	
	public ArrayList<String> getPath() {
		return path;
	}

//	public void setPath(ArrayList<String> path) {
//		this.path = path;
//	}

	public int getCost() {
		return cost;
	}

	public void updateCost(int new_cost) {
		cost += new_cost;
	}
	
	public void add(String node)
	{
		path.add(node);
		this.node = node;
	}
}