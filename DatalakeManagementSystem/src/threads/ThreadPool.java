package threads;

import java.util.ArrayList;
import java.util.List;

import bean.ForwardIndexPair;
import linker.LinkCreator;

public class ThreadPool {

	private List<Thread> pool;
	private int threadCount;
	private Queue<ForwardIndexPair> queue;

	public ThreadPool(int threadCount) {
		super();
		this.pool = new ArrayList<Thread>();
		this.threadCount = threadCount;
		this.queue = new Queue<ForwardIndexPair>();
		startThreads();
	}

	public void startThreads() {
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new LinkCreator(queue);
			thread.start();
			pool.add(thread);
		}
	}

	public void joinThreads() throws InterruptedException {
		int i = 1;
		while(queue.getSize() > 0 ) {
			try {
				i++;
				Thread.sleep(500 * i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notifyThreadsToEnd();
		for (Thread thread : pool) {
			thread.join();
		}
	}
	
	public void notifyThreadsToEnd() throws InterruptedException {
		for (Thread thread : pool) {
			LinkCreator linkCreatorThread = (LinkCreator) thread;
			linkCreatorThread.setShouldContinue(false);
		}
	}

	public Queue<ForwardIndexPair> getQueue() {
		return queue;
	}

}
