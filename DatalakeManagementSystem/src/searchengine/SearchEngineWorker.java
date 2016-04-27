package searchengine;

import java.util.PriorityQueue;
import java.util.Queue;

import javax.print.attribute.standard.MediaSize.Other;

import java.util.ArrayList;
import java.util.HashSet;

public class SearchEngineWorker implements Runnable
{
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	HashSet<ArrayList<String>> mySeenNodes = new HashSet<ArrayList<String>>();
	HashSet<ArrayList<String>> seenNodesOther = new HashSet<ArrayList<String>>();
	
	public SearchEngineWorker(HashSet<ArrayList<String>> mySeenNodes, HashSet<ArrayList<String>> seenNodesOther)
	{
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
	}
	
	@Override
	public void run() {
//		System.out.println("Hi in worker");
//		System.out.println("my nodes are " + mySeenNodes.toString());
//		System.out.println("other nodes are " + seenNodesOther.toString());
		
		
	}
	
}