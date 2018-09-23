#Usage: ./run_server.sh [<rmi_name>]

java -Djava.security.policy=java.policy -cp Procedure.jar:. Server.TCP.TCPResourceManager $1
