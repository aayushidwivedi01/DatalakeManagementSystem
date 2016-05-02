package searchengine;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import storage.DocumentDA;
import storage.LinksDA;

public class BidirectionalSearch implements Runnable
{

	int NUM_THREADS = 1;
	Queue<WeightedPath> frontier = new PriorityQueue<WeightedPath>();
	Map<String, WeightedPath> mySeenNodes = new HashMap<String, WeightedPath>();
	Map<String, WeightedPath> seenNodesOther = new HashMap<String, WeightedPath>();
	Thread[] threadPool = new Thread[NUM_THREADS];
	int k = 5;
	String word, username;
	
	public BidirectionalSearch(Map<String, WeightedPath> mySeenNodes, Map<String, WeightedPath> seenNodesOther, String word, String username)
	{
		this.mySeenNodes = mySeenNodes;
		this.seenNodesOther = seenNodesOther;
		this.word = word;
		this.username = username;
	}
	
	@Override
	public void run() {
		
		LinksDA lDa = new LinksDA();
		DocumentDA docDa = new DocumentDA();
		//Start all the worker threads
		for (int i = 0; i < NUM_THREADS; i++)
		{
			SearchEngineWorker worker_i = new SearchEngineWorker(frontier, mySeenNodes, seenNodesOther, username, lDa, docDa);
			threadPool[i] = new Thread(worker_i);
			threadPool[i].start();
		}
		
		//Initialize frontier with first word
		synchronized(frontier)
		{
			//System.out.println("initializing frontier with " + word);
			WeightedPath currentNode = new WeightedPath(word, 1);
			frontier.add(currentNode);
			frontier.notify();
		}
		
		//Wait for the threads to finish
		try {
			for (int i = 0; i < NUM_THREADS; i++)
			{
					threadPool[i].join();
					//System.out.println("Thread " + i + " finished");
			}
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}