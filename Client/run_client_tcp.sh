# Usage: ./run_client.sh [<server_hostname> [<server_rmiobject>]]

java -Djava.security.policy=java.policy -cp ../Server/RMIInterface.jar:../Server/MethodLambda.jar:. Client.TCPClient $1 $2
