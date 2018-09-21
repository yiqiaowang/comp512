all: java.policy compile-client

java.policy: FORCE
	@echo "Creating client java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

../Server/RMIInterface.jar: ../Server/Server/Interface/IResourceManager.java
	make -C ../Server/ RMIInterface.jar

../Server/ProcedureRequest.jar: ../Server/Server/Common/ProcedureRequest.java
	make -C ../Server/ ProcedureRequest.jar

../Server/Procedure.jar: ../Server/Server/Common/Procedure.java
	make -C ../Server/ Procedure.jar

compile-client: ../Server/RMIInterface.jar ../Server/ProcedureRequest.jar ../Server/Procedure.jar
	javac -cp ../Server/RMIInterface.jar:../Server/ProcedureRequest.jar:../Server/Procedure.jar Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/RMIInterface.jar
	rm -f ../Server/ProcedureRequest.jar
	rm -f ../Server/Procedure.jar
	rm -f java.policy
