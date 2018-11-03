package Benchmark;

import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.rmi.RemoteException;

import Client.RMIClient;
import Client.Command;

public class BenchmarkWorker implements Runnable {

    
    /**
     * Transaction Parameters
     */

    public ArrayList<Integer> flight_numbers = new ArrayList<>(Arrays.asList(
                1, 2, 3, 4, 5, 6
                ));

    public ArrayList<String> car_locations = new ArrayList<>(Arrays.asList(
                "montreal", "los angeles", "new york"
                ));

    public ArrayList<String> room_locations = new ArrayList<>(Arrays.asList(
                "montreal", "los angeles", "new york"
                ));

    public SingleResourceTransaction transaction;



    // ----------------------------------------------- // 
    // ----------------------------------------------- // 
    private long bufferTimeMillis;
    private int iterations;
    private RMIClient client;
    private BenchmarkTimer timer = new BenchmarkTimer();
    private Random rand;
    private int identifier;

    private volatile BenchmarkResult results = new BenchmarkResult();

    public BenchmarkWorker(int iterations, long delay) {
        this.bufferTimeMillis = delay;
        this.iterations = iterations;
        this.client = new RMIClient();
        this.rand = new Random();
        this.identifier = this.rand.nextInt(10000);
        
        // Configure this as well
        this.transaction = new SingleResourceTransaction(
                this.identifier,
                this.client,
                this.flight_numbers,
                this.room_locations,
                this.car_locations);

    };

    // Execute the transaction every `target' milliseconds.
    // Implements the buffering delay time.
    private void execute(long target, boolean log) throws InterruptedException, RemoteException {
        this.timer.init(); 

        // Execute client transaction here
        this.transaction.run();

        long elapsed = this.timer.getElapsedMillis();
        if (log) {
            this.results.addResult(elapsed);
            System.out.println("Transaction at " + this.identifier + " took " + elapsed + " milliseconds");
        }
        Thread.sleep(this.getBufferMillis((int) (target - elapsed)));
    }

    private long getBufferMillis(int bufferMillis) throws InterruptedException {
        if (bufferMillis < 0) return 0;
        int noise = Math.min(this.rand.nextInt(bufferMillis/10), 500);
        int sign = this.rand.nextBoolean() ? -1 : 1;
        return bufferMillis + sign * noise;
    }

    public BenchmarkResult getResult() {
        return this.results;
    }

    public void run() {
        try {
            // Setup the customer
            this.transaction.setupCustomer();

            // Start benchmark
            int counter = 0;

            // Warmup the JVM with 10 iterations
            while (counter < 10) {
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
