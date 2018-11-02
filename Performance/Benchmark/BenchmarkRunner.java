package Benchmark;

import java.util.Random;

// Import the client here
// Import the parametrized transaction type

public class BenchmarkRunner {

    private static int num_of_clients = 1;
    private static float transactions_per_sec = 1.0f;
    private float delay_time = 1;

    public static void testFunction() throws InterruptedException {
        BenchmarkRunner.wait(1000);
    }

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
        } catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Benchmark Completed");
    }

    public BenchmarkRunner() {
        this.delay_time = BenchmarkRunner.num_of_clients / BenchmarkRunner.transactions_per_sec;
    };

}
