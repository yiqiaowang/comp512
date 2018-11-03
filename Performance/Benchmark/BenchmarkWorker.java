package Benchmark;

import java.lang.Runnable;
import java.util.Arrays;
import java.util.Random;

public class BenchmarkWorker implements Runnable {

    // Need to store the delay buffering time to acheive desired load 
    private long bufferTimeMillis;
    private int warmup = 20;
    private int iterations;
    // Need to store an executable transaction here
    // Need to store a client
    private BenchmarkTimer timer = new BenchmarkTimer();

    private volatile BenchmarkResult results = new BenchmarkResult();

    public BenchmarkWorker(int iterations, long delay) {
        this.bufferTimeMillis = delay;
        this.iterations = iterations;
        // Create an executable transaction here 
        // Create a client here
        //
        // Pass the client to the transaction as an argument
    };

    // Execute the transaction every `target' milliseconds.
    // Implements the buffering delay time.
    private void execute(long target, boolean log) throws InterruptedException {
        this.timer.init(); 

        // Execute client transaction here
        Thread.sleep(100);

        long elapsed = this.timer.getElapsedMillis();
        if (log) {
            this.results.addResult(elapsed);
        }
        Thread.sleep(this.getBufferMillis((int) (target - elapsed)));
    }

    private long getBufferMillis(int bufferMillis) throws InterruptedException {
        if (bufferMillis < 0) return 0;
        Random rand = new Random();
        int noise = Math.min(rand.nextInt(bufferMillis/10), 500);
        int sign = rand.nextBoolean() ? -1 : 1;
        return bufferMillis + sign * noise;
    }

    public BenchmarkResult getResult() {
        return this.results;
    }

    public void run() {
        try {
            int counter = 0;

            // Warmup the JVM
            while (counter < this.warmup) {
                this.execute(this.bufferTimeMillis, false);
                counter++;
            }

            // Perform benchmark
            counter = 0;
            while (counter < this.iterations) {
                this.execute(this.bufferTimeMillis, true);
                counter++;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
