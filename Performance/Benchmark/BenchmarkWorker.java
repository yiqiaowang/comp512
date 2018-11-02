package Benchmark;

import java.lang.Runnable;

public class BenchmarkWorker implements Runnable {

    // Need to store an executable transaction here
    // Need to store a client
    // Need to store a timer
    private BenchmarkTimer timer = new BenchmarkTimer();
    
    public BenchmarkWorker() {
        // Create an executable transaction here 
        // Create a client here
        // Create a timer
        //
        // Pass the client to the transaction as an argument
    };

    // Execute the transaction every `target' milliseconds.
    // Implements the buffering delay time.
    private long execute(long target) {
        this.timer.init(); 
        
        // Execute client transaction here
        
        long elpased = this.timer.getElapsedMillis();
        BenchmarkTimer.wait(target - elapsed);
        return this.timer.getElapsedMillis();
    }
    
    public void run() {
        
    };
}
