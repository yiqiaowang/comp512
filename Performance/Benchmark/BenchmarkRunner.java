package Benchmark;

import java.util.ArrayList;
// Import the client here
// Import the parametrized transaction type

public class BenchmarkRunner {

    private static int num_of_clients = 1;
    private static float transactions_per_sec = 1.0f;
    private static int iterations = 1;
    private static String logfile =
        "results_" + num_of_clients + "_" +transactions_per_sec + ".csv";
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
            iterations = Integer.parseInt(args[1]); 
        }

        if (args.length > 3) {
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

            BenchmarkLogger logger = new BenchmarkLogger(BenchmarkRunner.logfile);
            logger.writeRow(
                    BenchmarkRunner.transactions_per_sec,
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

    public float averageResults() {
        float t = 0;
        for (BenchmarkResult result : this.results) {
            t += result.getAverage(); 
        }
        return t / results.size();
    }

    public BenchmarkRunner() {

        this.delay_time = BenchmarkRunner.num_of_clients / BenchmarkRunner.transactions_per_sec;
        long delay_time_millis = (long) (this.delay_time * 1000);
        // Initialize ArrayList of clients 
        for (int i = 0; i < BenchmarkRunner.num_of_clients; i++) {
            this.clients.add(
                    new BenchmarkWorker(BenchmarkRunner.iterations,
                        delay_time_millis)
                    );
        }
    }
}
