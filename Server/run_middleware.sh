# ./run_rmi.sh $1 > /dev/null

# echo "Edit file run_middleware.sh to include instructions for launching the middleware"
# echo '  $1 - hostname of Flights'
# echo '  $2 - hostname of Cars'
# echo '  $3 - hostname of Rooms'

# usage ./run_middleware.sh [port [ FlightRM host:port [ RoomRM host:port  [ CarRM host:port [ CustomerRM host:port ] ]] ]]

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.RMIMiddleware $1 $2 $3 $4 $5
