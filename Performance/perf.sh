# usage of run_benchmark.sh is
# ./run_benchmark.sh <number of clients> <transactions per second> <iterations>
ITERS=5
CLIENTS="1 2 3 4 5"
LOAD="1 2 3 4"
for clients in $CLIENTS;
do
    for load in $LOAD;
    do
        ./run_benchmark.sh $clients $load $ITERS
    done
done
