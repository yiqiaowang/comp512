package Benchmark;

public class BenchmarkTimer {
    private long start_time;    


    public void init() {
        this.start_time = System.currentTimeMillis();
    } 

    public long getElapsedMillis() {
        return System.currentTimeMillis() - this.start_time; 
    }

    public long getElapsedSecs() {
        return (System.currentTimeMillis() - this.start_time) / 1000; 
    }
}
