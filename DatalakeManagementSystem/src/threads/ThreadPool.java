package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bean.ForwardIndexPair;
import bean.Link;
import linker.LinkCreator;
import linker.LinkSaver;

public class ThreadPool {

	private static final int MAX_WAIT_COUNT = 5;
	private static final int SLEEP_DURATION = 1000;
	private List<Thread> pool;
	private int threadCount;
	private Queue<ForwardIndexPair> fIndexQueue;
	private Queue<Set<Link>> linksQueue;
	private LinkSaver linkSaver;

	public ThreadPool(int linkerThreadCount, Queue<ForwardIndexPair> fIndexQueue, Queue<Set<Link>> linksQueue) {
		super();
		this.pool = new ArrayList<Thread>();
		this.threadCount = linkerThreadCount;
		this.fIndexQueue = fIndexQueue;
		this.linksQueue = linksQueue;
		startThreads();
	}

	public void startThreads() {
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new LinkCreator(fIndexQueue, linksQueue);
			thread.start();
			pool.add(thread);
		}
		this.linkSaver = new LinkSaver(linksQueue);
		linkSaver.start();
	}

	public void waitForFIndexQueue() {
		int i = 1;
		while (fIndexQueue.getSize() > 0) {
			try {
				i++;
				if (i >= MAX_WAIT_COUNT) {
					i = 1;
				}
				Thread.sleep(SLEEP_DURATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void waitForLinksQueue() {
		int i = 1;
		while (linksQueue.getSize() > 0) {
			try {
				i++;
				if (i >= MAX_WAIT_COUNT) {
					i = 1;
				}
				Thread.sleep(SLEEP_DURATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void joinThreads() throws InterruptedException {
		System.out.println("Linker Thread pool starting the end!");
		waitForFIndexQueue();
		System.out.println("Linker Thread pool detected end of fIndexQueue");
		notifyThreadsToEnd();
		System.out.println("Linker Thread pool notified all link creator threads");
		for (Thread thread : pool) {
			thread.join();
		}
		System.out.println("Linker Thread pool joined link creator threads");
		System.out.println("Linker Thread pool waiting for the end of links Queue");
		waitForLinksQueue();
		System.out.println("Linker Thread pool detected end of linksQueue");
		waitForLinkSaverThreadToSleep();
		linkSaver.setShouldContinue(false);
		synchronized (linksQueue) {
			linksQueue.notifyAll();
		}
		System.out.println("Linker Thread pool notified link saver thread to end");
		linkSaver.join();
		System.out.println("Linker Thread pool joined link saver thread");
	}

	private void waitForLinkCreatorThreadsToSleep() {
		boolean done = false;
		int i = 0;
		while (!done) {
			done = true;
			for (Thread thread : pool) {
				if (!(thread.getState().equals(Thread.State.TERMINATED)
						|| thread.getState().equals(Thread.State.WAITING))) {
					done = false;
					try {
						i++;
						if (i >= MAX_WAIT_COUNT) {
							i = 1;
						}
						System.out.println(thread.getState());
						System.out.println("Thread pool sleeping while waiting for link creator threads to finish");
						Thread.sleep(SLEEP_DURATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	private void waitForLinkSaverThreadToSleep() {
		boolean done = false;
		int i = 0;
		while (!done) {
			done = true;
			if (!(linkSaver.getState().equals(Thread.State.TERMINATED)
					|| linkSaver.getState().equals(Thread.State.WAITING))) {
				done = false;
				try {
					i++;
					if (i >= MAX_WAIT_COUNT) {
						i = 1;
					}
					System.out.println(linkSaver.getState());
					System.out.println("Thread pool sleeping while waiting for link saver thread to finish");
					Thread.sleep(SLEEP_DURATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyThreadsToEnd() throws InterruptedException {
		waitForLinkCreatorThreadsToSleep();
		LinkCreator.setShouldContinue(false);
		synchronized (fIndexQueue) {
			fIndexQueue.notifyAll();
		}

	}

	public Queue<ForwardIndexPair> getQueue() {
		return fIndexQueue;
	}

}
