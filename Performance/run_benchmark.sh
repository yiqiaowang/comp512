# usage ./run_benchmark.sh <number of clients> <transactions per second> <iterations>
java -Djava.security.policy=../Client/java.policy -cp .:../Server/RMIInterface.jar:../Client/RMIClient.jar Benchmark.BenchmarkRunner $1 $2 $3
