package com.example.service;

import java.util.concurrent.*;

/**
 * 演示 ThreadPoolExecutor 的四种拒绝策略：
 *  - AbortPolicy: 抛出 RejectedExecutionException
 *  - CallerRunsPolicy: 由调用者线程执行被拒绝的任务
 *  - DiscardPolicy: 直接丢弃被拒绝的任务
 *  - DiscardOldestPolicy: 丢弃队列中最老的任务，然后尝试提交当前任务
 *
 *  运行方式：在项目编译后运行此类的 main 方法（IDE 直接运行或 mvn package 后用 java -cp target/classes ...）
 */
public class ThreadPoolRejectionDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("---- AbortPolicy (默认) 演示 ----");
        runScenario("AbortPolicy", new ThreadPoolExecutor.AbortPolicy());

        System.out.println("\n---- CallerRunsPolicy 演示 ----");
        runScenario("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy());

        System.out.println("\n---- DiscardPolicy 演示 ----");
        runScenario("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy());

        System.out.println("\n---- DiscardOldestPolicy 演示 ----");
        runScenario("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 统一场景：创建一个容量很小的线程池，提交多个任务以触发拒绝策略。
     * corePoolSize = 1, maximumPoolSize = 1, queue capacity = 1。
     * 总请求数 = 5，超过可处理（1运行 + 1队列）将触发拒绝处理。
     */
    private static void runScenario(String name, RejectedExecutionHandler handler) throws InterruptedException {
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        long keepAliveTime = 0L;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                queue,
                Executors.defaultThreadFactory(),
                handler
        );

        // 使用自定义任务以便观察线程/提交者行为
        for (int i = 1; i <= 5; i++) {
            final int id = i;
            Runnable task = () -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Task " + id + " running on thread: " + threadName);
                try {
                    // 模拟任务耗时，确保足以让后续提交导致排队或拒绝
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + id + " finished on thread: " + threadName);
            };

            System.out.println("Submitting task " + id + " to " + name + " handler");
            try {
                executor.execute(task);
            } catch (RejectedExecutionException ex) {
                System.out.println("Task " + id + " was rejected: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }

            // 提交间隔短一些，让任务保持运行以制造压力
            Thread.sleep(100);
        }

        // 优雅关闭
        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        if (!terminated) {
            System.out.println("Forcing shutdown of " + name);
            executor.shutdownNow();
        }

        // 打印队列状态
        System.out.println(name + " finished. Remaining queued tasks: " + queue.size());
    }
}
