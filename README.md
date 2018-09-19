# comp512-project
![Build Status](https://travis-ci.com/yiqiaowang/comp512.svg?token=hr7c7sHjqwUBhqrhRBYy&branch=master)

To run the RMI resource manager:

```
cd Server/
./run_server.sh [<rmi_name>] # starts a single ResourceManager
./run_servers.sh # convenience script for starting multiple resource managers
```

To run the RMI client:

```
cd Client
./run_client.sh [<server_hostname> [<server_rmi_name>]]
```
