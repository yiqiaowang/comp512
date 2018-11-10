package Benchmark;

import java.util.ArrayList;
import java.util.Arrays;
// Import the client here
// Import the parametrized transaction type

public class BenchmarkRunner {

    private static int num_of_clients = 1;
    private static float transactions_per_sec = 1.0f;
    private static int iterations = 1;
    private static int range = 0;
    private ArrayList<BenchmarkWorker> clients = new ArrayList<>();
    private ArrayList<BenchmarkResult> results = new ArrayList<>();
    private float delay_time = 1;

    public static void main(String[] args) {
        if (args.length > 0) {
            num_of_clients = Integer.parseInt(args[0]); 
        }

        if (args.length > 1) {
            transactions_per_sec = Float.parseFloat(args[1]); 
        }

        if (args.length > 2) {
            iterations = Integer.parseInt(args[2]); 
        }

        if (args.length > 3) {
            range = Integer.parseInt(args[3]); 
        }

        if (args.length > 4) {
            System.err.println((char)27 + "[31;1mBenchmarkRunner exception: " + (char)27 + "[0mUsage: java Performance.BenchmarkRunner [num_of_clients [transactions_per_sec]]");
            System.exit(1);
        }

        try {
            BenchmarkRunner runner = new BenchmarkRunner();
            ArrayList<Thread> clientThreads = new ArrayList<>();

            // Initialize State

            // Launch clients
            for (BenchmarkWorker client : runner.clients) {
                Thread t = new Thread(client);
                clientThreads.add(t);
                t.start();
            }

            // Wait until all clients finish
            for (Thread thread : clientThreads) {
                thread.join();
            }

            // Collect results
            for (BenchmarkWorker client : runner.clients) {
                runner.addResult(client.getResult());
            }

            String logdir = "range_logs/";
            String logfile = "results_load-" + BenchmarkRunner.range + ".csv";
            BenchmarkLogger logger = new BenchmarkLogger(logdir + logfile);
            logger.writeRow(
                    BenchmarkRunner.range,
                    runner.averageResults()
                    );
            logger.close();
            
            System.out.println("Benchmark Completed");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addResult(BenchmarkResult result) { 
        this.results.add(result);
    }

    // The average of the average of all client latency across each iteration
    // Equal to the average latency across all iterations and all clients
    // i.e. the average latency of the system
    public float averageResults() {
        float t = 0;
        for (BenchmarkResult result : this.results) {
            // The average of each client's latency across every iteration
            t += result.getAverage(); 
        }
        return t / results.size();
    }

    public BenchmarkRunner() {

        this.delay_time = BenchmarkRunner.num_of_clients / BenchmarkRunner.transactions_per_sec;
        long delay_time_millis = (long) (this.delay_time * 1000);


         
    ArrayList<Integer> set_a = new ArrayList<>(Arrays.asList(
                1, 2, 3, 4, 5
                ));


    ArrayList<Integer> set_b = new ArrayList<>(Arrays.asList(
                // 1, 2, 3, 4, 5
                6,7,8,9,10
                ));



        // Initialize ArrayList of clients 
        for (int i = 1; i <= BenchmarkRunner.num_of_clients; i++) {
                // this.clients.add(
                //         new BenchmarkWorker(BenchmarkRunner.iterations,
                //             delay_time_millis, i)
                //         );
            if (i % 2 == 0) {
                this.clients.add(
                        new BenchmarkWorker(BenchmarkRunner.iterations,
                            delay_time_millis, set_a, i)
                        );
            } else {
                this.clients.add(
                        new BenchmarkWorker(BenchmarkRunner.iterations,
                            delay_time_millis, set_b, i)
                        );
            }
        }
    }
}
