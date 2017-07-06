
package com.letv.shop.base.concurrency.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * 闭锁
 * 
 * @author Bruce
 *
 */
public class TestHarness {
	public static void timeTasks(int nThreads) {
		// 启动门
		final CountDownLatch startGate = new CountDownLatch(1);
		// 结束门
		final CountDownLatch endGate = new CountDownLatch(nThreads);
		for (int i = 0; i < nThreads; i++) {
			Thread t = new Thread() {
				public void run() {
					try {
						// 启动门来阻塞所有线程，等都就绪了再打开
						startGate.await();
						System.out.println(Thread.currentThread().getName());
					} catch (InterruptedException e) {

					} finally {
						// 每个线程执行完毕，到达结束门，计数器减一
						endGate.countDown();
					}

				}
			};
			t.start();
		}

		long start = 0;
		long end = 0;
		try {
			start = System.nanoTime();
			// 启动门打开，所有开始并行run
			startGate.countDown();
			// 结束门阻塞主线程，等待最后一个线程到达，再打开门
			endGate.await();
			end = System.nanoTime();
			System.out.println("All threads runned using " + (end - start) + " nanoseconds");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			startGate.countDown();
		}
	}

	public static void main(String[] args) {
		timeTasks(1000);
	}
}
