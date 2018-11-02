package Benchmark;

import java.util.ArrayList;
// Import the client here
// Import the parametrized transaction type

public class BenchmarkRunner {

    private static int num_of_clients = 1;
    private static float transactions_per_sec = 1.0f;
    private ArrayList<BenchmarkWorker> clients = new ArrayList<>();
    private float delay_time = 1;

    public static void main(String[] args) {
        if (args.length > 0) {
            num_of_clients = Integer.parseInt(args[0]); 
        }

        if (args.length > 1) {
            transactions_per_sec = Float.parseFloat(args[1]); 
        }

        if (args.length > 2) {
            System.err.println((char)27 + "[31;1mBenchmarkRunner exception: " + (char)27 + "[0mUsage: java Performance.BenchmarkRunner [num_of_clients [transactions_per_sec]]");
            System.exit(1);
        }

        try {
            BenchmarkRunner runner = new BenchmarkRunner();
            ArrayList<Thread> clientThreads = new ArrayList<>();

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

            System.out.println("Benchmark Completed");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public BenchmarkRunner() {
        this.delay_time = BenchmarkRunner.num_of_clients / BenchmarkRunner.transactions_per_sec;
        long delay_time_millis = (long) (this.delay_time * 1000);
        // Initialize ArrayList of clients 
        for (int i = 0; i < BenchmarkRunner.num_of_clients; i++) {
            this.clients.add(new BenchmarkWorker(delay_time_millis));
        }
    };

}
