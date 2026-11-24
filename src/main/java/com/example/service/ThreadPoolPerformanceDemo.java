package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 使用线程池演示多线程对运行效率的提升。
 * 
 * 场景：把一组计算密集型任务（每个任务循环计算平方和）分别用顺序方式与固定大小线程池方式执行，
 * 打印两种方式的耗时并计算加速比。
 * 
 * 可运行：在 IDE 中直接运行 main，或使用 mvn package 后运行类。
 * 注意：默认参数（TASKS、ITERATIONS）可根据机器性能调整，过大可能导致运行时间较长。
 */
public class ThreadPoolPerformanceDemo {

    // 任务数量和每个任务的工作量；可根据机器性能调整
    private static final int TASKS = 320;
    private static final int ITERATIONS = 8_000_000; // 每个任务的循环次数（计算量）

    public static void main(String[] args) throws Exception {
        int cpus = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + cpus);
        System.out.println("Tasks: " + TASKS + ", iterations per task: " + ITERATIONS);

        long seqMs = runSequential(TASKS, ITERATIONS);
        long poolMs = runWithFixedThreadPool(TASKS, ITERATIONS, cpus);

        System.out.printf("\nResult summary:\n");
        System.out.printf("Sequential time: %d ms\n", seqMs);
        System.out.printf("FixedThreadPool(%d) time: %d ms\n", cpus, poolMs);
        System.out.printf("Speedup: %.2fx\n", (double) seqMs / (double) poolMs);
    }

    // 顺序执行所有任务
    private static long runSequential(int tasks, int iterations) {
        long start = System.nanoTime();
        long total = 0L;
        for (int t = 0; t < tasks; t++) {
            total += compute(iterations);
        }
        long end = System.nanoTime();
        System.out.println("Sequential total (checksum): " + total);
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    // 使用固定大小线程池并行执行任务
    private static long runWithFixedThreadPool(int tasks, int iterations, int poolSize) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        try {
            List<Callable<Long>> callables = new ArrayList<>();
            for (int t = 0; t < tasks; t++) {
                callables.add(() -> compute(iterations));
            }

            long start = System.nanoTime();
            List<Future<Long>> futures = executor.invokeAll(callables);

            long total = 0L;
            for (Future<Long> f : futures) {
                total += f.get();
            }
            long end = System.nanoTime();
            System.out.println("ThreadPool total (checksum): " + total);
            return TimeUnit.NANOSECONDS.toMillis(end - start);
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    // 模拟计算任务：累加平方和
    private static long compute(int iterations) {
        long sum = 0L;
        for (int i = 1; i <= iterations; i++) {
            sum += (long) i * (long) i;
        }
        return sum;
    }
}
