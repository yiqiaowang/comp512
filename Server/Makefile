all: java.policy compile-server-rmi compile-server-tcp

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

RMIInterface.jar: Server/Interface/IResourceManager.java
	@echo "Compiling RMI server interface"
	javac Server/Interface/IResourceManager.java
	jar cvf RMIInterface.jar Server/Interface/IResourceManager.class

ProcedureRequest.jar: Server/Common/ProcedureRequest.java
	@echo "Compiling TCP procedure request"
	javac Server/Common/ProcedureRequest.java
	jar cvf ProcedureRequest.jar Server/Common/ProcedureRequest.class

Procedure.jar: Server/Common/Procedure.java
	@echo "Compiling TCP procedure"
	javac Server/Common/Procedure.java
	jar cvf Procedure.jar Server/Common/Procedure.class

ClientCommunicationManager.jar: Server/Common/Procedure.java
	@echo "Compiling Client Communication Manager"
	javac Server/Common/ClientCommunicationManager.java
	jar cvf Procedure.jar Server/Common/ClientCommunicationManager.class

compile-server-rmi: Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java
	javac Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java

compile-server-tcp: Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java Procedure.jar
	javac Server/TCP/*.java Server/Interface/IResourceManager.java Server/Common/*.java

clean:
	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class
	rm -f RMIInterface.jar
	rm -f java.policy 
