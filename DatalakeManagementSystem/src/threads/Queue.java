package threads;

import java.util.ArrayList;

public class Queue<T>
{

	private ArrayList<T> queue;

	public Queue()
	{
		queue = new ArrayList<T>();
	}

	public ArrayList<T> getQueue()
	{
		return queue;
	}

	public synchronized int getSize()
	{
		return queue.size();
	}

	public synchronized void enqueue(T t)
	{
		queue.add(queue.size(), t); // add element to the end of the array list
		this.notify();
	}

	public synchronized T dequeue()
	{
		if(getSize() > 0) {
			return queue.remove(0); // remove element from the beginning of
		}
		else {
			return null;
		}
	}

	public synchronized void enqueueAll(ArrayList<T> list)
	{
		for (T t : list)
		{
			enqueue(t); // add all elements from the list
		}
	}

	@Override
	public String toString()
	{
		return "Queue [queue=" + queue + "]";
	}

}