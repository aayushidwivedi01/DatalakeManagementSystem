package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bean.ForwardIndexPair;
import bean.Link;
import linker.LinkCreator;
import linker.LinkSaver;

public class ThreadPool {

	private List<Thread> pool;
	private int threadCount;
	private Queue<ForwardIndexPair> fIndexQueue;
	private Queue<Set<Link>> linksQueue;
	private LinkSaver linkSaver;
	

	public ThreadPool(int linkerThreadCount,
			Queue<ForwardIndexPair> fIndexQueue, Queue<Set<Link>> linksQueue) {
		super();
		this.pool = new ArrayList<Thread>();
		this.threadCount = linkerThreadCount;
		this.fIndexQueue = fIndexQueue;
		this.linksQueue = linksQueue;
		this.linkSaver = new LinkSaver(linksQueue);
		startThreads();
	}

	public void startThreads() {
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new LinkCreator(fIndexQueue, linksQueue);
			thread.start();
			pool.add(thread);
		}
		linkSaver.start();
	}

	public void joinThreads() throws InterruptedException {
		int i = 1;
		while (fIndexQueue.getSize() > 0) {
			try {
				i++;
				Thread.sleep(500 * i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		i = 1;
		while (linksQueue.getSize() > 0) {
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
		linkSaver.join();
	}

	public void notifyThreadsToEnd() throws InterruptedException {
		for (Thread thread : pool) {
			LinkCreator linkCreatorThread = (LinkCreator) thread;
			linkCreatorThread.setShouldContinue(false);
		}
		linkSaver.setShouldContinue(false);
		synchronized (fIndexQueue) {
			fIndexQueue.notifyAll();
		}
		synchronized (linksQueue) {
			linksQueue.notifyAll();
		}
	}
	
	public Queue<ForwardIndexPair> getQueue() {
		return fIndexQueue;
	}

}
