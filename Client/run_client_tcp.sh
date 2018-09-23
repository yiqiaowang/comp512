# Usage: ./run_client.sh [<server_hostname> [<server_rmiobject>]]

java -Djava.security.policy=java.policy -cp ../Server/ClientCommunicationManager.jar:../Server/ProcedureRequest.jar:../Server/ProcedureResponse.jar:../Server/Procedure.jar:. Client.TCPClient $1 $2
