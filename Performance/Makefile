all: compile-runner

compile-runner: compile-timer compile-worker
	javac Benchmark/BenchmarkRunner.java

compile-timer:
	javac Benchmark/BenchmarkTimer.java

compile-worker: compile-result
	javac Benchmark/BenchmarkWorker.java

compile-result:
	javac Benchmark/BenchmarkResult.java

clean:
	rm -f Benchmark/*.class
