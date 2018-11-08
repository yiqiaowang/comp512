#Usage: ./run_customer.sh [<own_rmi_name> [port]] 
# eg: ./run_customer.sh Customers 5004

./run_rmi.sh $2 > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.CustomerResourceManager $1 $2
