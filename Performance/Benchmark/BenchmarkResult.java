package Benchmark;

import java.util.ArrayList;

public class BenchmarkResult {
    private ArrayList<Long> results = new ArrayList<>();

    public ArrayList<Long> getResults() { 
        return this.results;
    }

    public float getAverage() {
        long t = 0;
        for (long result : this.results) {
            t += result; 
        }
        return t / this.results.size();
    }

    public void addResult(long result) {
        this.results.add(result);
    }  
}
