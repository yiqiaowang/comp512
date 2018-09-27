#Usage: ./run_server.sh [<rmi_name>]

rm manager.log
java -Djava.security.policy=java.policy Server.TCP.FlightResourceManager $1 $2 >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.CarResourceManager $1 $2 >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.RoomResourceManager $1 $2 >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.CustomerResourceManager $1 $2 >> manager.log &
