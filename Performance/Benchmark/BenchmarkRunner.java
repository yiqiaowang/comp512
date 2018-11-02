package Benchmark;

// Import the client here
// Import the parametrized transaction type
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

    private static int num_of_clients = 1;
    private static int transactions_per_sec = 1;

    @Benchmark @BenchmarkMode(Mode.SampleTime)
    public void wellHelloThere() {
        // this method was intentionally left blank.
    }

    public static void main(String[] args) throws RunnerException {
        if (args.length > 0) {
            num_of_clients = Integer.parseInt(args[0]); 
        }

        if (args.length > 1) {
            transactions_per_sec = Integer.parseInt(args[1]); 
        }

        if (args.length > 2) {
            System.err.println((char)27 + "[31;1mBenchmarkRunner exception: " + (char)27 + "[0mUsage: java Performance.BenchmarkRunner [num_of_clients [transactions_per_sec]]");
            System.exit(1);
        }


        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public BenchmarkRunner() { };
}
