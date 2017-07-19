package com.letv.shop.base.concurrency.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 非线程安全的计数器
 * 
 * @author Bruce
 *
 */
public class NotSafeCounter {
	private int value;

	public int get() {
		return value;
	}

	public void incrementAndGet() {
		// 这是一个 读-改-写 复合操作，非线程安全，只不过是执行时间极其短暂，多线程情况下也不好重现
		value++;
	}

	public static void main(String[] args) {
		final NotSafeCounter sc = new NotSafeCounter();
		ExecutorService threadPool = Executors.newFixedThreadPool(10000);
		final CountDownLatch startGate = new CountDownLatch(1);
		for (int i = 0; i < 10000; i++) {
			threadPool.execute(new Runnable() {

				@Override
				public void run() {
					try {
						startGate.await();
						sc.incrementAndGet();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});

		}
		System.out.println("All thread starting");
		// 用闭锁让线程同时跑，提高竞争性
		startGate.countDown();
		threadPool.shutdown();
		// 等待所有累加线程都结束
		while (Thread.activeCount() > 1)
			Thread.yield();
		System.out.println(sc.get());// 最终得到的结果不是10000
	}
}
