package com.letv.shop.base.concurrency.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;

/**
 * 模拟CAS操作
 * 
 * @author Bruce
 *
 */
@ThreadSafe
public class SimulatedCAS {
	@GuardedBy("this")
	private int value;

	public synchronized int get() {
		return value;
	}

	public synchronized int compareAndSwap(int expectedValue, int newValue) {
		int oldValue = value;
		if (oldValue == expectedValue) {
			value = newValue;
		}
		return oldValue;
	}

	public boolean compareAndSet(int expectedValue, int newValue) {
		return (expectedValue == compareAndSwap(expectedValue, newValue));
	}

	public static void main(String[] args) {
		final CasCounter cs = new CasCounter();
		ExecutorService threadPool = Executors.newFixedThreadPool(1000);
		final CountDownLatch startGate = new CountDownLatch(1);
		for (int i = 0; i < 1000; i++) {
			threadPool.execute(new Runnable() {

				@Override
				public void run() {
					try {
						startGate.await();
						cs.increment();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});

		}
		System.out.println("All thread starting");
		startGate.countDown();
		threadPool.shutdown();
	}
}

@ThreadSafe
class CasCounter {
	private SimulatedCAS value = new SimulatedCAS();

	public int getValue() {
		return value.get();
	}

	public int increment() {
		int v;
		do {
			v = value.get();
		} while (v != value.compareAndSwap(v, v + 1));
		System.out.println(v + 1);
		return v + 1;
	}
}
