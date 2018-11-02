package Benchmark;

import java.util.ArrayList;

public class BenchmarkResult {
    private ArrayList<Long> results = new ArrayList<>();

    public ArrayList<Long> getResults() { 
        return this.results;
    }

    public void addResult(long result) {
        this.results.add(result);
    }  
}
