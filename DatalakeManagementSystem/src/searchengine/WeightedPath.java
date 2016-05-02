package searchengine;

import java.util.ArrayList;

public class WeightedPath implements Comparable<WeightedPath> {
	private ArrayList<String> path;
	private double cost;
	private String node;

	public WeightedPath(ArrayList<String> path, double cost) {
		this.path = path;
		this.node = path.get(path.size() - 1);
		this.cost = cost;
	}

	public WeightedPath(String node, double cost) {
		this.path = new ArrayList<String>();
		path.add(node);
		this.node = node;
		this.cost = cost;
	}

	public ArrayList<String> getPath() {
		return path;
	}

	// public void setPath(ArrayList<String> path) {
	// this.path = path;
	// }

	public double getCost() {
		return cost;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public void updateCost(double new_cost) {
		cost += new_cost;
	}

	public void add(String node) {
		path.add(node);
		this.node = node;
	}

	public int compareTo(WeightedPath p) {
		return (int) (this.getCost() - p.getCost());
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return (this.path.equals(((WeightedPath) obj).getPath()));
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}