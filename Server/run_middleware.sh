# echo "Edit file run_middleware.sh to include instructions for launching the middleware"
# echo '  $1 - hostname of Flights'
# echo '  $2 - hostname of Cars'
# echo '  $3 - hostname of Rooms'

java -Djava.security.policy=java.policy Server.TCP.TCPMiddleware
