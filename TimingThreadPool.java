package com.letv.shop.base.concurrency.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * 给线程池添加统计信息
 * 
 * @author Bruce
 *
 */
public class TimingThreadPool extends ThreadPoolExecutor {

	public TimingThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private final Logger log = Logger.getLogger("TimingThreadPool");
	private final AtomicLong numTasks = new AtomicLong();
	private final AtomicLong totalTime = new AtomicLong();

	/**
	 * 记录执行开始时间
	 */
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		log.fine(String.format("Thread %s: start %s", t, r));
		startTime.set(System.nanoTime());
	}

	/**
	 * 统计结束是执行时间
	 */
	protected void afterExecute(Runnable r, Throwable t) {
		try {
			long endTime = System.nanoTime();
			long taskTime = endTime - startTime.get();
			numTasks.incrementAndGet();
			totalTime.addAndGet(taskTime);
			log.fine(String.format("Thread %s: end %s, time=%dns", t, r,
					taskTime));
		} finally {
			super.afterExecute(r, t);
		}
	}
	/**
	 * stop thread
	 */

	protected void terminated() {
		try {
			log.info(String.format("Terminated: avg time=%dns", totalTime.get()
					/ numTasks.get()));
		} finally {
			super.terminated();
		}
	}
}
