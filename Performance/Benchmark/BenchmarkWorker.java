package Benchmark;

import java.lang.Runnable;
import java.util.Arrays;
import java.util.Random;

public class BenchmarkWorker implements Runnable {

    // Need to store the delay buffering time to acheive desired load 
    private long bufferTimeMillis;
    // Need to store an executable transaction here
    // Need to store a client
    private BenchmarkTimer timer = new BenchmarkTimer();

    private volatile BenchmarkResult results = new BenchmarkResult();
    
    public BenchmarkWorker(long delay) {
        this.bufferTimeMillis = delay;
        // Create an executable transaction here 
        // Create a client here
        //
        // Pass the client to the transaction as an argument
    };

    // Execute the transaction every `target' milliseconds.
    // Implements the buffering delay time.
    private long execute(long target) throws InterruptedException {
        this.timer.init(); 
        
        // Execute client transaction here
        Thread.sleep(200);
        
        long elapsed = this.timer.getElapsedMillis();
        this.results.addResult(elapsed);
        Thread.sleep(this.getBufferMillis((int) (target - elapsed)));
        return this.timer.getElapsedMillis();
    }

    private long getBufferMillis(int delay) throws InterruptedException {
        if (delay < 0) return 0;
        Random rand = new Random();
        int noise = (System.currentTimeMillis() % 2 == 0) ? 
            rand.nextInt(delay/10) :
            -rand.nextInt(delay/10);
        return delay + noise;
    }
    
    public BenchmarkResult getResults() {
        return this.results;
    }

    public void run() {
        try {
            long time = this.execute(this.bufferTimeMillis);
            System.out.println("Worker finished executing in: " + time);
        } catch(Exception e){
            e.printStackTrace();
        }
    };
}
