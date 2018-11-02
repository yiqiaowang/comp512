all: compile-runner

compile-runner: compile-timer compile-worker compile-logger
	javac Benchmark/BenchmarkRunner.java

compile-timer:
	javac Benchmark/BenchmarkTimer.java

compile-worker: compile-result
	javac Benchmark/BenchmarkWorker.java

compile-result:
	javac Benchmark/BenchmarkResult.java

compile-logger:
	javac Benchmark/BenchmarkLogger.java

clean:
	rm -f Benchmark/*.class
