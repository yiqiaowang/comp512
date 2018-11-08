#Usage: ./run_server.sh [<own_rmi_name> [ port ]]

./run_rmi.sh $2 > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.RMIResourceManager $1 $2
