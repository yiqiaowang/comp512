all: compile-runner

compile-runner: compile-timer compile-worker compile-logger
	javac Benchmark/BenchmarkRunner.java

compile-timer:
	javac Benchmark/BenchmarkTimer.java

compile-worker: compile-result bundle-client
	javac -cp .:../Client/RMIClient.jar Benchmark/BenchmarkWorker.java

compile-result:
	javac Benchmark/BenchmarkResult.java

compile-logger:
	javac Benchmark/BenchmarkLogger.java

compile-client:
	make -C ../Client/ all

bundle-client: compile-client
	make -C ../Client/ bundle-client

clean:
	rm -f Benchmark/*.class
