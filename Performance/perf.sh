# usage of run_benchmark.sh is
# ./run_benchmark.sh <number of clients> <transactions per second> <iterations>
ITERS=100
CLIENTS="5"
LOAD="500 1000 1500 2000 2500 3000 3500 4000"
for clients in $CLIENTS;
do
    for load in $LOAD;
    do
        ./run_benchmark.sh $clients $load $ITERS
    done
done
