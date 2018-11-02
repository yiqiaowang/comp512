package Benchmark;

public class BenchmarkTimer {
    private long start_time;    

    public static void wait(int delay) throws InterruptedException {
        Random rand = new Random();
        int n = (System.currentTimeMillis() % 2 == 0) ? 
            rand.nextInt(delay/10) :
            -rand.nextInt(delay/10);
        Thread.sleep(delay + n);
    }

    public void init() {
        this.start_time = System.currentTimeMillis();
    } 

    public long getElapsedMillis() {
        return System.currentTimeMillis() - this.start_time; 
    }

    public long getElapsedSecs() {
        return (System.currentTimeMillis() - this.start_time) / 1000; 
    }

    public BenchmarkTimer(){};
}
