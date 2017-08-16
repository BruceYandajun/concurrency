package com.letv.shop.base.concurrency.threadpool;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletionServiceTest {
	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(10); // 创建含10.条线程的线程池
		CompletionService<String> completionService = new ExecutorCompletionService<String>(
				executor);

		// 采用闭锁让线程同时跑
		final CountDownLatch cdl = new CountDownLatch(1);
		for (int i = 1; i <= 10; i++) {
			completionService.submit(new Callable<String>() {
				public String call() throws Exception {
					cdl.await();
					int a = new Random().nextInt(5000);
					Thread.sleep(a); // 让当前线程随机休眠一段时间
					System.out.println(Thread.currentThread().getName() + ": "
							+ a);
					return a + "";
				}
			});
		}
		cdl.countDown();
		System.out.println(completionService.take().get()); // 获取第一个线程执行的结果
		executor.shutdown();
	}
}
