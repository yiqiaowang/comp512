#Usage: ./run_customer.sh [<rmi_name>]

./run_rmi.sh $2 > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.CustomerResourceManager $1 $2
