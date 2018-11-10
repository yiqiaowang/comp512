# usage of run_benchmark.sh is
# ./run_benchmark.sh <number of clients> <transactions per second> <iterations>
ITERS=10
CLIENTS="1"
LOAD="100 200 300 400 500 600 700 800 900 1000"
for clients in $CLIENTS;
do
    for load in $LOAD;
    do
        ./run_benchmark.sh $clients $load $ITERS
        sleep 5
    done
done
