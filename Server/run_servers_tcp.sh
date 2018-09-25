#Usage: ./run_server.sh [<rmi_name>]

rm manager.log
java -Djava.security.policy=java.policy Server.TCP.FlightResourceManager >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.CarResourceManager >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.RoomResourceManager >> manager.log &
java -Djava.security.policy=java.policy Server.TCP.CustomerResourceManager >> manager.log &
